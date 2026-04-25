package com.nirapod.controller;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nirapod.model.*;
import com.nirapod.services.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    @Autowired private TaxpayerService taxpayerService;
    @Autowired private BusinessService businessService;
    @Autowired private VatReturnService vatReturnService;
    @Autowired private PaymentService paymentService;
    @Autowired private AuditService auditService;
    @Autowired private RefundService refundService;
    @Autowired private PenaltyService penaltyService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        List<Taxpayer>  taxpayers  = taxpayerService.getAll();
        List<Business>  businesses = businessService.getAll();
//        List<VatReturn> vatReturns = vatReturnService.get();
        List<Payment>   payments   = paymentService.getAll();
        List<Audit>     audits     = auditService.getAll();
        List<Refund>    refunds    = refundService.getAll();
        List<Penalty>   penalties  = penaltyService.getAll();

        double totalRevenue = payments.stream()
                .filter(p -> "Completed".equals(p.getStatus()))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0)
                .sum();

        long pendingAudits  = audits.stream().filter(a -> "Scheduled".equals(a.getStatus()) || "In Progress".equals(a.getStatus())).count();
        long pendingRefunds = refunds.stream().filter(r -> "Pending".equals(r.getStatus())).count();
        long issuedPenalties = penalties.stream().filter(p -> "Issued".equals(p.getStatus()) || "Pending".equals(p.getStatus())).count();
        long completedAudits = audits.stream().filter(a -> "Completed".equals(a.getStatus())).count();
        long flaggedCases    = audits.stream().filter(a -> "Flagged".equals(a.getStatus())).count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalTaxpayers",       taxpayers.size());
        stats.put("totalBusinesses",       businesses.size());
//        stats.put("totalVatReturns",       vatReturns.size());
        stats.put("totalPayments",         payments.size());
        stats.put("totalRevenue",          totalRevenue);
        stats.put("pendingAudits",         pendingAudits);
        stats.put("pendingRefunds",        pendingRefunds);
        stats.put("issuedPenalties",       issuedPenalties);
        stats.put("taxpayerGrowth",        5.2);
        stats.put("revenueGrowth",         8.7);
        stats.put("vatReturnGrowth",       3.1);
        stats.put("paymentGrowth",         6.4);
        stats.put("totalAudits",           audits.size());
        stats.put("completedAudits",       completedAudits);
        stats.put("flaggedCases",          flaggedCases);
        stats.put("auditGrowth",           2.3);
        stats.put("todayEntries",          0);
        stats.put("pendingTasks",          pendingAudits + pendingRefunds);
        stats.put("taxpayersAddedThisMonth", 0);
        stats.put("myVatReturns",          0);
        stats.put("myPayments",            0);
        stats.put("myPendingNotices",      0);
        stats.put("myRefundStatus",        0);
        stats.put("myTotalPaid",           0.0);

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
            m.put("taxpayerName",  p.getTaxpayerName());
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
//        List<VatReturn> all = vatReturnService.getAll();
        List<Map<String, Object>> chart = new ArrayList<>();
        for (String month : months) {
//            long count = all.stream().filter(v -> month.equalsIgnoreCase(v.getPeriodMonth())).count();
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("label", month);
//            point.put("value", count);
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
                                 month.equals(p.getPaymentDate().getMonth().name().substring(0,3).charAt(0)
                                     + p.getPaymentDate().getMonth().name().substring(1,3).toLowerCase()))
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
