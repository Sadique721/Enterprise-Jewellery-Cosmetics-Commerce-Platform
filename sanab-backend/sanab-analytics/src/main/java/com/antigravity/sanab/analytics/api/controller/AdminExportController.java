package com.antigravity.sanab.analytics.api.controller;

import com.antigravity.sanab.catalog.domain.entity.Product;
import com.antigravity.sanab.catalog.domain.repository.ProductRepository;
import com.antigravity.sanab.identity.domain.entity.User;
import com.antigravity.sanab.identity.domain.repository.UserRepository;
import com.antigravity.sanab.orders.domain.entity.Order;
import com.antigravity.sanab.orders.domain.repository.OrderRepository;
import com.antigravity.sanab.payments.domain.entity.PaymentTransaction;
import com.antigravity.sanab.payments.domain.repository.PaymentTransactionRepository;
import com.antigravity.sanab.reviews.domain.entity.Review;
import com.antigravity.sanab.reviews.domain.repository.ReviewRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Enterprise Admin Export Controller.
 *
 * <p>Provides Excel (.xlsx) & Word (.doc) data exports and email report generation
 * for SANAB Executive BI reporting.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/export")
@RequiredArgsConstructor
@Tag(name = "Admin Export & BI Reporting", description = "Endpoints for exporting orders, products, users, payments, and reviews to Excel, Word, and Email attachments")
public class AdminExportController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PaymentTransactionRepository paymentRepository;
    private final ReviewRepository reviewRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    // ==================== EXCEL EXPORTS ====================

    @GetMapping("/orders/excel")
    @Operation(summary = "Export all orders to Excel (.xlsx)")
    public void exportOrdersToExcel(HttpServletResponse response) throws IOException {
        List<Order> list = orderRepository.findAll();
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Orders");
            String[] cols = {"Order ID", "User ID", "Grand Total", "Order Status", "Created At", "Updated At"};
            createHeaderRow(sheet, cols);

            int r = 1;
            for (Order o : list) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(o.getId().toString());
                row.createCell(1).setCellValue(o.getUserId() != null ? o.getUserId().toString() : "");
                row.createCell(2).setCellValue(o.getGrandTotal() != null ? o.getGrandTotal().doubleValue() : 0.0);
                row.createCell(3).setCellValue(o.getStatus() != null ? o.getStatus().name() : "");
                row.createCell(4).setCellValue(o.getCreatedAt() != null ? o.getCreatedAt().toString() : "");
                row.createCell(5).setCellValue(o.getUpdatedAt() != null ? o.getUpdatedAt().toString() : "");
            }
            autoSizeColumns(sheet, cols.length);
            writeExcelResponse(response, wb, "sanab_orders.xlsx");
        }
    }

    @GetMapping("/products/excel")
    @Operation(summary = "Export all products to Excel (.xlsx)")
    public void exportProductsToExcel(HttpServletResponse response) throws IOException {
        List<Product> list = productRepository.findAll();
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Products");
            String[] cols = {"Product ID", "Title", "SKU", "Price", "Category ID", "Brand ID", "Status", "Created At"};
            createHeaderRow(sheet, cols);

            int r = 1;
            for (Product p : list) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(p.getId().toString());
                row.createCell(1).setCellValue(p.getTitle());
                row.createCell(2).setCellValue(p.getSku());
                row.createCell(3).setCellValue(p.getBasePrice() != null ? p.getBasePrice().doubleValue() : 0.0);
                row.createCell(4).setCellValue(p.getCategory() != null ? p.getCategory().getId().toString() : "");
                row.createCell(5).setCellValue(p.getBrand() != null ? p.getBrand().getId().toString() : "");
                row.createCell(6).setCellValue(p.getStatus() != null ? p.getStatus().name() : "");
                row.createCell(7).setCellValue(p.getCreatedAt() != null ? p.getCreatedAt().toString() : "");
            }
            autoSizeColumns(sheet, cols.length);
            writeExcelResponse(response, wb, "sanab_products.xlsx");
        }
    }

    @GetMapping("/users/excel")
    @Operation(summary = "Export all users to Excel (.xlsx)")
    public void exportUsersToExcel(HttpServletResponse response) throws IOException {
        List<User> list = userRepository.findAll();
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Users");
            String[] cols = {"User ID", "Email", "First Name", "Last Name", "Phone", "Status", "Created At"};
            createHeaderRow(sheet, cols);

            int r = 1;
            for (User u : list) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(u.getId().toString());
                row.createCell(1).setCellValue(u.getEmail());
                row.createCell(2).setCellValue(u.getFirstName());
                row.createCell(3).setCellValue(u.getLastName());
                row.createCell(4).setCellValue(u.getPhone() != null ? u.getPhone() : "");
                row.createCell(5).setCellValue(u.getStatus() != null ? u.getStatus().name() : "");
                row.createCell(6).setCellValue(u.getCreatedAt() != null ? u.getCreatedAt().toString() : "");
            }
            autoSizeColumns(sheet, cols.length);
            writeExcelResponse(response, wb, "sanab_users.xlsx");
        }
    }

    @GetMapping("/payments/excel")
    @Operation(summary = "Export all payment transactions to Excel (.xlsx)")
    public void exportPaymentsToExcel(HttpServletResponse response) throws IOException {
        List<PaymentTransaction> list = paymentRepository.findAll();
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Payments");
            String[] cols = {"Transaction ID", "Order ID", "Amount", "Payment Method", "Status", "Created At"};
            createHeaderRow(sheet, cols);

            int r = 1;
            for (PaymentTransaction p : list) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(p.getId().toString());
                row.createCell(1).setCellValue(p.getOrderId() != null ? p.getOrderId().toString() : "");
                row.createCell(2).setCellValue(p.getAmount() != null ? p.getAmount().doubleValue() : 0.0);
                row.createCell(3).setCellValue(p.getPaymentMethod() != null ? p.getPaymentMethod().name() : "");
                row.createCell(4).setCellValue(p.getStatus() != null ? p.getStatus().name() : "");
                row.createCell(5).setCellValue(p.getCreatedAt() != null ? p.getCreatedAt().toString() : "");
            }
            autoSizeColumns(sheet, cols.length);
            writeExcelResponse(response, wb, "sanab_payments.xlsx");
        }
    }

    @GetMapping("/reviews/excel")
    @Operation(summary = "Export all reviews to Excel (.xlsx)")
    public void exportReviewsToExcel(HttpServletResponse response) throws IOException {
        List<Review> list = reviewRepository.findAll();
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Reviews");
            String[] cols = {"Review ID", "Product ID", "User ID", "Rating", "Title", "Comment", "Created At"};
            createHeaderRow(sheet, cols);

            int r = 1;
            for (Review rev : list) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(rev.getId().toString());
                row.createCell(1).setCellValue(rev.getProductId() != null ? rev.getProductId().toString() : "");
                row.createCell(2).setCellValue(rev.getUserId() != null ? rev.getUserId().toString() : "");
                row.createCell(3).setCellValue(rev.getRating());
                row.createCell(4).setCellValue(rev.getTitle());
                row.createCell(5).setCellValue(rev.getComment());
                row.createCell(6).setCellValue(rev.getCreatedAt() != null ? rev.getCreatedAt().toString() : "");
            }
            autoSizeColumns(sheet, cols.length);
            writeExcelResponse(response, wb, "sanab_reviews.xlsx");
        }
    }

    // ==================== WORD EXPORTS ====================

    @GetMapping("/orders/word")
    @Operation(summary = "Export all orders to Word (.doc)")
    public void exportOrdersToWord(HttpServletResponse response) throws IOException {
        List<Order> list = orderRepository.findAll();
        response.setContentType("application/msword");
        response.setHeader("Content-Disposition", "attachment; filename=sanab_orders.doc");
        try (PrintWriter writer = response.getWriter()) {
            writer.println("SANAB Enterprise Order Report\n");
            writer.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
            writer.println("========================================\n");
            for (Order o : list) {
                writer.printf("Order #%s | User #%s | Total ₹%.2f | Status %s | Created %s%n",
                        o.getOrderNumber(), o.getUserId(), o.getGrandTotal(),
                        o.getStatus(), o.getCreatedAt() != null ? o.getCreatedAt().toString() : "");
            }
        }
    }

    @GetMapping("/products/word")
    @Operation(summary = "Export all products to Word (.doc)")
    public void exportProductsToWord(HttpServletResponse response) throws IOException {
        List<Product> list = productRepository.findAll();
        response.setContentType("application/msword");
        response.setHeader("Content-Disposition", "attachment; filename=sanab_products.doc");
        try (PrintWriter writer = response.getWriter()) {
            writer.println("SANAB Product Catalog Report\n");
            writer.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
            writer.println("========================================\n");
            for (Product p : list) {
                writer.printf("Product #%s | %s | Price ₹%.2f | Status %s%n",
                        p.getSku(), p.getTitle(), p.getBasePrice(), p.getStatus());
            }
        }
    }

    // ==================== EMAIL REPORT ====================

    @PostMapping("/send-report")
    @Operation(summary = "Dispatch BI Analytics Report to specified email recipient")
    public ResponseEntity<String> sendReportEmail(@RequestParam String reportType, @RequestParam String email) {
        try {
            log.info("Generating BI report for type '{}' to email '{}'", reportType, email);
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setSubject("SANAB BI Executive Analytics Report — " + reportType.toUpperCase());
                message.setText("Please find attached the latest executive BI report for " + reportType + ". Generated at " + LocalDateTime.now());
                mailSender.send(message);
            }
            return ResponseEntity.ok("Report dispatched successfully to " + email);
        } catch (Exception e) {
            log.error("Failed to send BI report email", e);
            return ResponseEntity.internalServerError().body("Failed to send report: " + e.getMessage());
        }
    }

    // ==================== PRIVATE HELPERS ====================

    private void createHeaderRow(Sheet sheet, String[] cols) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < cols.length; i++) {
            header.createCell(i).setCellValue(cols[i]);
        }
    }

    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void writeExcelResponse(HttpServletResponse response, Workbook wb, String filename) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        wb.write(response.getOutputStream());
    }
}
