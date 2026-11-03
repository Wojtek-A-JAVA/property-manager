package property.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import property.manager.dto.utility.invoice.CreateUtilityInvoiceRequestDto;
import property.manager.dto.utility.invoice.UpdateUtilityInvoiceRequestDto;
import property.manager.dto.utility.invoice.UtilityInvoiceFilterDto;
import property.manager.dto.utility.invoice.UtilityInvoiceResponseDto;
import property.manager.service.UtilityInvoiceService;

@Tag(name = "07. Utility Invoices")
@RestController
@RequestMapping("/api/utility-invoices")
@RequiredArgsConstructor
public class UtilityInvoiceController {

    private final UtilityInvoiceService utilityInvoiceService;

    @Operation(summary = "Create utility invoice",
            description = "Creates a new utility invoice")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public UtilityInvoiceResponseDto createUtilityInvoice(
            @RequestBody @Valid CreateUtilityInvoiceRequestDto request) {
        return utilityInvoiceService.createUtilityInvoice(request);
    }

    @Operation(summary = "Get utility invoice",
            description = "Returns a utility invoice by its id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public UtilityInvoiceResponseDto getUtilityInvoice(@PathVariable Long id) {
        return utilityInvoiceService.getUtilityInvoice(id);
    }

    @Operation(summary = "Get utility invoices",
            description = "Returns utility invoices matching the provided filters")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public List<UtilityInvoiceResponseDto> getUtilityInvoices(
            @ModelAttribute UtilityInvoiceFilterDto filter) {
        return utilityInvoiceService.getUtilityInvoices(filter);
    }

    @Operation(summary = "Update utility invoice", description =
            "Updates an existing utility invoice. This operation is restricted to ADMIN users.")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UtilityInvoiceResponseDto updateUtilityInvoice(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUtilityInvoiceRequestDto request) {
        return utilityInvoiceService.updateUtilityInvoice(id, request);
    }
}
