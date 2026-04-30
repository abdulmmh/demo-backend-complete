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

        // ── 2. Duplicate check ─────────────────────────────────────────────────
        checkEmailNotTaken(req.getEmail());
        checkIdentityNotTaken(req);

        // ── 3. Resolve TaxpayerType from DB using the ID sent by frontend ──────
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

        // ── 6. Create Tin — PENDING first, then update with generated number ───
        Tin tin = buildTin(req, taxpayer.getId(), taxpayerType);
        tinDAO.saveAndFlush(tin);

        String generatedTin = "TIN-" + String.format("%09d", tin.getId());
        tin.setTinNumber(generatedTin);
        tinDAO.save(tin);

        taxpayer.setTinNumber(generatedTin);
        taxpayerDAO.update(taxpayer);

        // ── 7. Return response ─────────────────────────────────────────────────
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
            if (req.getDateOfBirth() != null && !req.getDateOfBirth().isBlank()) {
                t.setDateOfBirth(LocalDate.parse(req.getDateOfBirth()));
            }
        } else {
            // Business or Organization
            t.setCompanyName(req.getFullName());
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

    private Tin buildTin(UserRegistrationRequest req, Long taxpayerId, TaxpayerType type) {
        Tin tin = new Tin();
        tin.setTinNumber("PENDING");
        tin.setTaxpayerId(taxpayerId);
        tin.setEmail(req.getEmail().toLowerCase().trim());
        tin.setPhone(req.getPhone());
        tin.setStatus("Active");
        tin.setIssuedDate(LocalDate.now());

        // tinCategory = actual typeName from DB (e.g. "Sole Proprietor", "NGO")
        tin.setTinCategory(type.getTypeName());

        if ("Individual".equals(req.getAccountCategory())) {
            tin.setTaxpayerName(req.getFullName());
            tin.setNid(req.getNid());
            tin.setGender(req.getGender());
            if (req.getDateOfBirth() != null && !req.getDateOfBirth().isBlank()) {
                tin.setDateOfBirth(LocalDate.parse(req.getDateOfBirth()));
            }
        } else {
            // Business or Organization
            tin.setTaxpayerName(req.getFullName());
            tin.setNid(req.getAuthorizedPersonNid());
            if (req.getIncorporationDate() != null && !req.getIncorporationDate().isBlank()) {
                tin.setIncorporationDate(LocalDate.parse(req.getIncorporationDate()));
            }
        }
        return tin;
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private void validateRequest(UserRegistrationRequest req) {

        // taxpayerTypeId
        if (req.getTaxpayerTypeId() == null) {
            throw new IllegalArgumentException("Taxpayer type is required.");
        }

        // accountCategory
        if (req.getAccountCategory() == null ||
                !VALID_CATEGORIES.contains(req.getAccountCategory())) {
            throw new IllegalArgumentException(
                    "Invalid account category. Must be Individual, Business, or Organization.");
        }

        // email
        if (req.getEmail() == null || req.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }

        // password
        if (req.getPassword() == null || req.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }

        // Individual-specific required fields
        if ("Individual".equals(req.getAccountCategory())) {
            if (req.getNid() == null || req.getNid().isBlank()) {
                throw new IllegalArgumentException("NID is required for Individual registration.");
            }
            if (req.getGender() == null || req.getGender().isBlank()) {
                throw new IllegalArgumentException("Gender is required for Individual registration.");
            }
        }

        // Business / Organization-specific required fields
        // rjscNo is optional for Government Organization — no check here
        if ("Business".equals(req.getAccountCategory()) ||
                "Organization".equals(req.getAccountCategory())) {
            if (req.getAuthorizedPersonName() == null || req.getAuthorizedPersonName().isBlank()) {
                throw new IllegalArgumentException("Authorized person name is required.");
            }
            if (req.getAuthorizedPersonNid() == null || req.getAuthorizedPersonNid().isBlank()) {
                throw new IllegalArgumentException("Authorized person NID is required.");
            }
        }
    }

    // ── Duplicate checks ──────────────────────────────────────────────────────

    private void checkEmailNotTaken(String email) {
        boolean exists = userDAO.getAll().stream()
                .anyMatch(u -> email.equalsIgnoreCase(u.getEmail()));
        if (exists) {
            throw new IllegalStateException(
                    "An account with this email already exists. " +
                    "Please log in or use a different email.");
        }
    }

    private void checkIdentityNotTaken(UserRegistrationRequest req) {
        if ("Individual".equals(req.getAccountCategory())) {
            boolean nidExists = taxpayerDAO.getAll().stream()
                    .anyMatch(t -> req.getNid().equals(t.getNid()));
            if (nidExists) {
                throw new IllegalStateException(
                        "A TIN is already registered with this NID. " +
                        "If you forgot your login, please use password reset.");
            }
        } else {
            // Business / Organization — check RJSC only if provided
            if (req.getRjscNo() != null && !req.getRjscNo().isBlank()) {
                boolean rjscExists = taxpayerDAO.getAll().stream()
                        .anyMatch(t -> req.getRjscNo().equals(t.getRjscNo()));
                if (rjscExists) {
                    throw new IllegalStateException(
                            "A TIN is already registered with this RJSC number. " +
                            "Please contact the NBR helpdesk if you believe this is an error.");
                }
            }
        }
    }
}