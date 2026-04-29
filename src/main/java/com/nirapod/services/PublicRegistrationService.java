package com.nirapod.services;

import com.nirapod.dao.TaxpayerDAO;
import com.nirapod.dao.TaxpayerTypeDAO;
import com.nirapod.dao.TinDAO;
import com.nirapod.dao.UserDAO;
import com.nirapod.dto.RegistrationResponse;
import com.nirapod.dto.UserRegistrationRequest;
import com.nirapod.model.Address;
import com.nirapod.model.Taxpayer;
import com.nirapod.model.TaxpayerType;
import com.nirapod.model.Tin;
import com.nirapod.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * PublicRegistrationService
 *
 * Atomic flow — all three records (User, Taxpayer, Tin) are created in a single
 * transaction. If any step fails, the entire transaction rolls back so no
 * orphaned records are created.
 *
 * Duplicate detection:
 *   - email     → 409 Conflict (caught by GlobalExceptionHandler via IllegalStateException)
 *   - NID       → 409 Conflict (Individual)
 *   - RJSC No.  → 409 Conflict (Company)
 *
 * Security:
 *   - Password hashed with BCrypt (strength 12) before persisting
 *   - Role is always forced to "TAXPAYER" — the DTO has no role field by design
 */
@Service
@Transactional
public class PublicRegistrationService {

    // BCrypt strength 12 — good balance of security and performance for a
    // government portal on modern hardware (~250ms per hash)
    private static final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(12);

    @Autowired private UserDAO         userDAO;
    @Autowired private TaxpayerDAO     taxpayerDAO;
    @Autowired private TaxpayerTypeDAO taxpayerTypeDAO;
    @Autowired private TinDAO          tinDAO;

    public RegistrationResponse register(UserRegistrationRequest req) {

        // ── 1. Input validation ────────────────────────────────────────────────
        validateRequest(req);

        // ── 2. Duplicate checks — 409 via GlobalExceptionHandler ──────────────
        checkEmailNotTaken(req.getEmail());
        checkIdentityNotTaken(req);

        // ── 3. Create User (portal account) ───────────────────────────────────
        User user = buildUser(req);
        userDAO.save(user);

        // ── 4. Resolve TaxpayerType from master data ──────────────────────────
        TaxpayerType taxpayerType = resolveTaxpayerType(req.getAccountType());

        // ── 5. Create Taxpayer record ──────────────────────────────────────────
        Taxpayer taxpayer = buildTaxpayer(req, taxpayerType);
        taxpayerDAO.save(taxpayer);

        // ── 6. Create Tin via TinService (auto-generates TIN number + links back)
        Tin tin = buildTin(req, taxpayer.getId());
        tinDAO.saveAndFlush(tin);

        // TinService generateTIN pattern: "TIN-" + 9-digit padded ID
        String generatedTin = "TIN-" + String.format("%09d", tin.getId());
        tin.setTinNumber(generatedTin);
        tinDAO.save(tin);

        // Link TIN back to Taxpayer and User for quick lookup
        taxpayer.setTinNumber(generatedTin);
        taxpayerDAO.update(taxpayer);

        return new RegistrationResponse(
                user.getId(),
                taxpayer.getId(),
                generatedTin,
                req.getFullName(),
                req.getEmail(),
                req.getAccountType(),
                "Registration successful. Your TIN has been issued: " + generatedTin
        );
    }

    // ── Builders ──────────────────────────────────────────────────────────────

    private User buildUser(UserRegistrationRequest req) {
        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail().toLowerCase().trim());
        user.setPhone(req.getPhone());
        user.setRole("TAXPAYER");        // ALWAYS forced — cannot be overridden by client
        user.setStatus("Active");
        user.setPassword(bcrypt.encode(req.getPassword()));   // BCrypt hash
        return user;
    }

    private Taxpayer buildTaxpayer(UserRegistrationRequest req, TaxpayerType type) {
        Taxpayer t = new Taxpayer();
        t.setTaxpayerType(type);
        t.setEmail(req.getEmail().toLowerCase().trim());
        t.setPhone(req.getPhone());
        t.setStatus("Active");
        t.setRegistrationDate(LocalDate.now());
        t.setSameAsPermanent(false);

        // Default empty addresses (citizen can complete profile later)
        t.setPresentAddress(new Address());
        t.setPermanentAddress(new Address());

        if ("Individual".equals(req.getAccountType())) {
            t.setFullName(req.getFullName());
            t.setNid(req.getNid());
            t.setGender(req.getGender());
            t.setProfession(req.getProfession());
            if (req.getDateOfBirth() != null && !req.getDateOfBirth().isBlank()) {
                t.setDateOfBirth(LocalDate.parse(req.getDateOfBirth()));
            }
        } else {
            t.setCompanyName(req.getFullName());   // fullName doubles as company display name
            t.setRjscNo(req.getRjscNo());
            t.setNatureOfBusiness(req.getNatureOfBusiness());
            t.setAuthorizedPersonName(req.getAuthorizedPersonName());
            t.setAuthorizedPersonNid(req.getAuthorizedPersonNid());
            if (req.getIncorporationDate() != null && !req.getIncorporationDate().isBlank()) {
                t.setIncorporationDate(LocalDate.parse(req.getIncorporationDate()));
            }
        }
        return t;
    }

    private Tin buildTin(UserRegistrationRequest req, Long taxpayerId) {
        Tin tin = new Tin();
        tin.setTinNumber("PENDING");   // placeholder until ID is known
        tin.setTaxpayerId(taxpayerId);
        tin.setEmail(req.getEmail().toLowerCase().trim());
        tin.setPhone(req.getPhone());
        tin.setStatus("Active");
        tin.setIssuedDate(LocalDate.now());

        if ("Individual".equals(req.getAccountType())) {
            tin.setTaxpayerName(req.getFullName());
            tin.setTinCategory("Individual");
            tin.setNid(req.getNid());
            if (req.getDateOfBirth() != null && !req.getDateOfBirth().isBlank()) {
                tin.setDateOfBirth(LocalDate.parse(req.getDateOfBirth()));
            }
        } else {
            tin.setTaxpayerName(req.getFullName());
            tin.setTinCategory("Company");
            tin.setNid(req.getAuthorizedPersonNid());   // authorized person NID
            if (req.getIncorporationDate() != null && !req.getIncorporationDate().isBlank()) {
                tin.setIncorporationDate(LocalDate.parse(req.getIncorporationDate()));
            }
        }
        return tin;
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private void validateRequest(UserRegistrationRequest req) {
        if (req.getAccountType() == null ||
            (!req.getAccountType().equals("Individual") && !req.getAccountType().equals("Company"))) {
            throw new IllegalArgumentException("accountType must be 'Individual' or 'Company'.");
        }
        if (req.getEmail() == null || req.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (req.getPassword() == null || req.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }
        if ("Individual".equals(req.getAccountType())) {
            if (req.getNid() == null || req.getNid().isBlank()) {
                throw new IllegalArgumentException("NID is required for Individual registration.");
            }
        } else {
            if (req.getRjscNo() == null || req.getRjscNo().isBlank()) {
                throw new IllegalArgumentException("RJSC registration number is required for Company registration.");
            }
        }
    }

    private void checkEmailNotTaken(String email) {
        boolean exists = userDAO.getAll().stream()
                .anyMatch(u -> email.equalsIgnoreCase(u.getEmail()));
        if (exists) {
            throw new IllegalStateException(
                    "An account with this email address already exists. Please log in or use a different email."
            );
        }
    }

    private void checkIdentityNotTaken(UserRegistrationRequest req) {
        if ("Individual".equals(req.getAccountType())) {
            // Check NID uniqueness against existing taxpayers
            boolean nidExists = taxpayerDAO.getAll().stream()
                    .anyMatch(t -> req.getNid().equals(t.getNid()));
            if (nidExists) {
                throw new IllegalStateException(
                        "A TIN is already registered with this NID. " +
                        "If you have forgotten your login, please use password reset."
                );
            }
        } else {
            // Check RJSC No uniqueness against existing taxpayers
            boolean rjscExists = taxpayerDAO.getAll().stream()
                    .anyMatch(t -> req.getRjscNo().equals(t.getRjscNo()));
            if (rjscExists) {
                throw new IllegalStateException(
                        "A TIN is already registered with this RJSC number. " +
                        "Please contact the NBR helpdesk if you believe this is an error."
                );
            }
        }
    }

    // ── Helper: resolve TaxpayerType entity from master data ─────────────────

    private TaxpayerType resolveTaxpayerType(String accountType) {
        return taxpayerTypeDAO.findByTypeNameIgnoreCase(accountType)
                .orElseThrow(() -> new IllegalStateException(
                    "TaxpayerType '" + accountType + "' not found in master data."));
    }
}
