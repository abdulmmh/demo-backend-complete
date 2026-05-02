package com.nirapod.service;

import com.nirapod.dao.TaxpayerDAO;
import com.nirapod.dao.TaxpayerTypeDAO;
import com.nirapod.dao.TinDAO;
import com.nirapod.dao.UserDAO;
import com.nirapod.dto.request.UserRegistrationRequest;
import com.nirapod.dto.response.RegistrationResponse;
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

@Service
@Transactional
public class PublicRegistrationService {

    private static final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(12);

    private static final List<String> VALID_CATEGORIES =
            List.of("Individual", "Business", "Organization");

    @Autowired private UserDAO         userDAO;
    @Autowired private TaxpayerDAO     taxpayerDAO;
    @Autowired private TaxpayerTypeDAO taxpayerTypeDAO;
    @Autowired private TinDAO          tinDAO;

    public RegistrationResponse register(UserRegistrationRequest req) {

        // ── 1. Validate ────────────────────────────────────────────────────────
        validateRequest(req);

        // ── 2. Duplicate checks (targeted queries — no full table scan) ─────────
        checkEmailNotTaken(req.getEmail());
        checkIdentityNotTaken(req);

        // ── 3. Resolve TaxpayerType ────────────────────────────────────────────
        TaxpayerType taxpayerType = taxpayerTypeDAO
                .findById(req.getTaxpayerTypeId())
                .orElseThrow(() -> new IllegalStateException(
                        "Invalid taxpayer type selected. Please go back and reselect."));

        // ── 4. Create User ─────────────────────────────────────────────────────
        User user = buildUser(req);
        userDAO.save(user);

        // ── 5. Create Taxpayer ─────────────────────────────────────────────────
        Taxpayer taxpayer = buildTaxpayer(req, taxpayerType);
        taxpayerDAO.save(taxpayer);

        // ── 6. Duplicate TIN guard ─────────────────────────────────────────────
        checkTinNotAlreadyIssued(taxpayer.getId());

        // ── 7. Create Tin — set FK relationship, not a name copy ───────────────
        Tin tin = buildTin(req, taxpayer, taxpayerType);
        tinDAO.saveAndFlush(tin);

        String generatedTin = "TIN-" + String.format("%09d", tin.getId());
        tin.setTinNumber(generatedTin);
        tinDAO.save(tin);

        taxpayer.setTinNumber(generatedTin);
        taxpayerDAO.update(taxpayer);

        // ── 8. Return response ─────────────────────────────────────────────────
        return new RegistrationResponse(
                user.getId(),
                taxpayer.getId(),
                generatedTin,
                req.getFullName(),
                req.getEmail(),
                req.getAccountCategory(),
                taxpayerType.getTypeName(),
                "Registration successful. Your TIN has been issued: " + generatedTin
        );
    }

    // ── Builders ──────────────────────────────────────────────────────────────

    private User buildUser(UserRegistrationRequest req) {
        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail().toLowerCase().trim());
        user.setPhone(req.getPhone());
        user.setRole("TAXPAYER");
        user.setStatus("Active");
        user.setPassword(bcrypt.encode(req.getPassword()));
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
        t.setPresentAddress(new Address());
        t.setPermanentAddress(new Address());

        if ("Individual".equals(req.getAccountCategory())) {
            t.setFullName(req.getFullName());
            t.setNid(req.getNid());
            t.setGender(req.getGender());
            t.setProfession(req.getProfession());
            if (req.getDateOfBirth() != null && !req.getDateOfBirth().isBlank())
                t.setDateOfBirth(LocalDate.parse(req.getDateOfBirth()));
        } else {
            t.setCompanyName(req.getFullName());
            t.setRjscNo(req.getRjscNo());
            t.setNatureOfBusiness(req.getNatureOfBusiness());
            t.setAuthorizedPersonName(req.getAuthorizedPersonName());
            t.setAuthorizedPersonNid(req.getAuthorizedPersonNid());
            if (req.getIncorporationDate() != null && !req.getIncorporationDate().isBlank())
                t.setIncorporationDate(LocalDate.parse(req.getIncorporationDate()));
        }
        return t;
    }

    /**
     * Enterprise fix: Tin now links to Taxpayer via FK (@ManyToOne), not via a
     * stored taxpayerId long or a copied taxpayerName string.
     * setTaxpayer(taxpayer) — not setTaxpayerId() or setTaxpayerName().
     */
    private Tin buildTin(UserRegistrationRequest req, Taxpayer taxpayer, TaxpayerType type) {
        Tin tin = new Tin();
        tin.setTinNumber("PENDING");
        tin.setTaxpayer(taxpayer);          // ← FK relationship, not a name copy
        tin.setEmail(req.getEmail().toLowerCase().trim());
        tin.setPhone(req.getPhone());
        tin.setStatus("Active");
        tin.setIssuedDate(LocalDate.now());
        tin.setTinCategory(type.getTypeName());

        if ("Individual".equals(req.getAccountCategory())) {
            tin.setNid(req.getNid());
            tin.setGender(req.getGender());
            if (req.getDateOfBirth() != null && !req.getDateOfBirth().isBlank())
                tin.setDateOfBirth(LocalDate.parse(req.getDateOfBirth()));
        } else {
            tin.setNid(req.getAuthorizedPersonNid());
            if (req.getIncorporationDate() != null && !req.getIncorporationDate().isBlank())
                tin.setIncorporationDate(LocalDate.parse(req.getIncorporationDate()));
        }
        // taxpayerName intentionally NOT set — resolved at query time via taxpayer FK
        return tin;
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private void validateRequest(UserRegistrationRequest req) {
        if (req.getTaxpayerTypeId() == null)
            throw new IllegalArgumentException("Taxpayer type is required.");

        if (req.getAccountCategory() == null ||
                !VALID_CATEGORIES.contains(req.getAccountCategory()))
            throw new IllegalArgumentException(
                    "Invalid account category. Must be Individual, Business, or Organization.");

        if (req.getEmail() == null || req.getEmail().isBlank())
            throw new IllegalArgumentException("Email is required.");

        if (req.getPassword() == null || req.getPassword().length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters.");

        if ("Individual".equals(req.getAccountCategory())) {
            if (req.getNid() == null || req.getNid().isBlank())
                throw new IllegalArgumentException("NID is required for Individual registration.");
            if (req.getGender() == null || req.getGender().isBlank())
                throw new IllegalArgumentException("Gender is required for Individual registration.");
        }

        if ("Business".equals(req.getAccountCategory()) ||
                "Organization".equals(req.getAccountCategory())) {
            if (req.getAuthorizedPersonName() == null || req.getAuthorizedPersonName().isBlank())
                throw new IllegalArgumentException("Authorized person name is required.");
            if (req.getAuthorizedPersonNid() == null || req.getAuthorizedPersonNid().isBlank())
                throw new IllegalArgumentException("Authorized person NID is required.");
        }
    }

    // ── Duplicate checks — targeted WHERE queries, not full table scans ───────

    private void checkEmailNotTaken(String email) {
        // Fix Bug 5: replaced getAll().stream() with targeted findByEmail() query
        if (userDAO.findByEmail(email).isPresent()) {
            throw new IllegalStateException(
                    "An account with this email already exists. " +
                    "Please log in or use a different email.");
        }
    }

    private void checkIdentityNotTaken(UserRegistrationRequest req) {
        if ("Individual".equals(req.getAccountCategory())) {
            // Fix Bug 5: replaced getAll().stream() with targeted findByNid() query
            if (taxpayerDAO.findByNid(req.getNid()).isPresent()) {
                throw new IllegalStateException(
                        "A TIN is already registered with this NID. " +
                        "If you forgot your login, please use password reset.");
            }
        } else {
            if (req.getRjscNo() != null && !req.getRjscNo().isBlank()) {
                // Fix Bug 5: replaced getAll().stream() with targeted findByRjscNo() query
                if (taxpayerDAO.findByRjscNo(req.getRjscNo()).isPresent()) {
                    throw new IllegalStateException(
                            "A TIN is already registered with this RJSC number. " +
                            "Please contact the NBR helpdesk if you believe this is an error.");
                }
            }
        }
    }

    // Fix Bug 4: guard against duplicate TIN issuance
    private void checkTinNotAlreadyIssued(Long taxpayerId) {
        if (tinDAO.findByTaxpayer_Id(taxpayerId).isPresent()) {
            throw new IllegalStateException(
                    "A TIN has already been issued for taxpayer ID: " + taxpayerId +
                    ". Please use the TIN Management screen to view it.");
        }
    }
}
