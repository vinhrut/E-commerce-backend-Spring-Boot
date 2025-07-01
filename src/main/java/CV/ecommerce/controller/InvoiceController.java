package CV.ecommerce.controller;

import CV.ecommerce.dto.request.Invoice.InvoiceFilterRequest;
import CV.ecommerce.dto.response.APIResponse;
import CV.ecommerce.dto.response.InvoiceResponse;
import CV.ecommerce.entity.Invoice;
import CV.ecommerce.mapper.InvoiceMapper;
import CV.ecommerce.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceMapper invoiceMapper;

    // Lọc + phân trang
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @PostMapping("/filter")
    public APIResponse<Page<InvoiceResponse>> filterInvoices(
            @RequestBody InvoiceFilterRequest filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "issuedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Invoice> invoicePage = invoiceService.filterInvoices(filter, pageable);
        Page<InvoiceResponse> responsePage = invoicePage.map(invoiceMapper::toResponse);

        return new APIResponse<>(1000, "Filter invoices success", responsePage);
    }

    // Get tất cả
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @GetMapping
    public APIResponse<List<InvoiceResponse>> getAllInvoices() {
        List<InvoiceResponse> responses = invoiceService.getAll()
                .stream().map(invoiceMapper::toResponse).collect(Collectors.toList());
        return new APIResponse<>(1000, "Get all invoices success", responses);
    }

    // Get theo ID
    @GetMapping("/{id}")
    public APIResponse<InvoiceResponse> getInvoiceById(@PathVariable String id) {
        Optional<Invoice> invoiceOpt = invoiceService.getById(id);
        if (invoiceOpt.isEmpty()) {
            return new APIResponse<>(1001, "Invoice not found", null);
        }
        return new APIResponse<>(1000, "Get invoice success", invoiceMapper.toResponse(invoiceOpt.get()));
    }

    // Get theo Order ID
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @GetMapping("/order/{orderId}")
    public APIResponse<InvoiceResponse> getInvoiceByOrderId(@PathVariable String orderId) {
        Optional<Invoice> invoiceOpt = invoiceService.getByOrderId(orderId);
        if (invoiceOpt.isEmpty()) {
            return new APIResponse<>(1001, "Invoice not found", null);
        }
        return new APIResponse<>(1000, "Get invoice by orderId success", invoiceMapper.toResponse(invoiceOpt.get()));
    }

    // Get theo User (cho người dùng xem hóa đơn của họ)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF', 'USER')")
    @GetMapping("/user/{userId}")
    public APIResponse<List<InvoiceResponse>> getInvoicesByUser(@PathVariable String userId) {
        List<InvoiceResponse> responses = invoiceService.getByUser(userId)
                .stream().map(invoiceMapper::toResponse).collect(Collectors.toList());
        return new APIResponse<>(1000, "Get invoices by user success", responses);
    }

    // Cập nhật link file PDF sau khi export
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @PutMapping("/{invoiceId}/file-path")
    public APIResponse<InvoiceResponse> updateFilePath(
            @PathVariable String invoiceId,
            @RequestParam("filePath") String filePath) {

        Invoice updated = invoiceService.updateFilePath(invoiceId, filePath);
        return new APIResponse<>(1000, "Update invoice file path success", invoiceMapper.toResponse(updated));
    }

    @PostMapping("/generate-pdf/{id}")
    public APIResponse<String> generateInvoicePdf(@PathVariable String id) {
        try {
            String url = invoiceService.generateAndUploadInvoicePdf(id);
            return new APIResponse<>(1000, "Generate invoice PDF success", url);
        } catch (Exception e) {
            return new APIResponse<>(1001, e.getMessage(), null);
        }
    }
}
