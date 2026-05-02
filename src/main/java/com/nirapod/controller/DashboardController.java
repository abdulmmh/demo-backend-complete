package com.nirapod.controller;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nirapod.dto.response.AuditResponse;
import com.nirapod.dto.response.PenaltyResponse;
import com.nirapod.dto.response.RefundResponse;
import com.nirapod.model.Business;
import com.nirapod.model.Payment;
import com.nirapod.model.Taxpayer;
import com.nirapod.service.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private TaxpayerService taxpayerService;
    @Autowired private BusinessService businessService;
    @Autowired private PaymentService  paymentService;
    @Autowired private AuditService    auditService;
    @Autowired private RefundService   refundService;
    @Autowired private PenaltyService  penaltyService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {

        List<Taxpayer>      taxpayers  = taxpayerService.getAll();
        List<Business>      businesses = businessService.getAll();
        List<Payment>       payments   = paymentService.getAll();

        // AuditService, RefundService, PenaltyService now return Response DTOs
        // after the enterprise refactor — use those directly
        List<AuditResponse>   audits    = auditService.getAll();
        List<RefundResponse>  refunds   = refundService.getAll();
        List<PenaltyResponse> penalties = penaltyService.getAll();

        double totalRevenue = payments.stream()
                .filter(p -> "Completed".equals(p.getStatus()))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0)
                .sum();

        long pendingAudits   = audits.stream()
                .filter(a -> "Scheduled".equals(a.getStatus()) || "In Progress".equals(a.getStatus()))
                .count();
        long completedAudits = audits.stream()
                .filter(a -> "Completed".equals(a.getStatus())).count();
        long flaggedCases    = audits.stream()
                .filter(a -> "Flagged".equals(a.getStatus())).count();
        long pendingRefunds  = refunds.stream()
                .filter(r -> "Pending".equals(r.getStatus())).count();
        long issuedPenalties = penalties.stream()
                .filter(p -> "Issued".equals(p.getStatus()) || "Pending".equals(p.getStatus()))
                .count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalTaxpayers",          taxpayers.size());
        stats.put("totalBusinesses",          businesses.size());
        stats.put("totalPayments",            payments.size());
        stats.put("totalRevenue",             totalRevenue);
        stats.put("pendingAudits",            pendingAudits);
        stats.put("pendingRefunds",           pendingRefunds);
        stats.put("issuedPenalties",          issuedPenalties);
        stats.put("taxpayerGrowth",           5.2);
        stats.put("revenueGrowth",            8.7);
        stats.put("vatReturnGrowth",          3.1);
        stats.put("paymentGrowth",            6.4);
        stats.put("totalAudits",              audits.size());
        stats.put("completedAudits",          completedAudits);
        stats.put("flaggedCases",             flaggedCases);
        stats.put("auditGrowth",              2.3);
        stats.put("todayEntries",             0);
        stats.put("pendingTasks",             pendingAudits + pendingRefunds);
        stats.put("taxpayersAddedThisMonth",  0);
        stats.put("myVatReturns",             0);
        stats.put("myPayments",               0);
        stats.put("myPendingNotices",         0);
        stats.put("myRefundStatus",           0);
        stats.put("myTotalPaid",              0.0);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recent-taxpayers")
    public ResponseEntity<List<Map<String, Object>>> getRecentTaxpayers() {
        List<Taxpayer> all = taxpayerService.getAll();
        int limit = Math.min(5, all.size());
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = all.size() - 1; i >= all.size() - limit; i--) {
            Taxpayer t = all.get(i);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",               t.getId());
            m.put("tin",              t.getTinNumber());
            m.put("fullName",         t.getFullName());
            m.put("email",            t.getEmail());
            m.put("phone",            t.getPhone());
            m.put("status",           t.getStatus());
            m.put("registrationDate", t.getRegistrationDate());
            result.add(m);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/recent-payments")
    public ResponseEntity<List<Map<String, Object>>> getRecentPayments() {
        List<Payment> all = paymentService.getAll();
        int limit = Math.min(5, all.size());
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = all.size() - 1; i >= all.size() - limit; i--) {
            Payment p = all.get(i);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",            p.getId());
            m.put("transactionId", p.getTransactionId());
            // taxpayerName now resolved from FK — not from stored column
            m.put("taxpayerName",  p.getTaxpayer() != null
                ? (p.getTaxpayer().getFullName() != null
                    ? p.getTaxpayer().getFullName()
                    : p.getTaxpayer().getCompanyName())
                : "N/A");
            m.put("amount",        p.getAmount());
            m.put("paymentType",   p.getPaymentType());
            m.put("paymentDate",   p.getPaymentDate());
            m.put("status",        p.getStatus());
            result.add(m);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/vat-chart")
    public ResponseEntity<List<Map<String, Object>>> getVatChart() {
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        List<Map<String, Object>> chart = new ArrayList<>();
        for (String month : months) {
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("label", month);
            chart.add(point);
        }
        return ResponseEntity.ok(chart);
    }

    @GetMapping("/payment-chart")
    public ResponseEntity<List<Map<String, Object>>> getPaymentChart() {
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        List<Payment> all = paymentService.getAll();
        List<Map<String, Object>> chart = new ArrayList<>();
        for (String month : months) {
            double total = all.stream()
                    .filter(p -> p.getPaymentDate() != null &&
                                 month.equals(p.getPaymentDate().getMonth().name().substring(0, 3).charAt(0)
                                     + p.getPaymentDate().getMonth().name().substring(1, 3).toLowerCase()))
                    .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0)
                    .sum();
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("label", month);
            point.put("value", total);
            chart.add(point);
        }
        return ResponseEntity.ok(chart);
    }
}
