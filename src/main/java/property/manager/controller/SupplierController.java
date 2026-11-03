package property.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import property.manager.dto.supplier.CreateSupplierRequestDto;
import property.manager.dto.supplier.SupplierResponseDto;
import property.manager.dto.supplier.SupplierStatusRequestDto;
import property.manager.dto.supplier.UpdateSupplierRequestDto;
import property.manager.service.SupplierService;

@Tag(name = "06. Suppliers")
@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @Operation(summary = "Create supplier",
            description = "Creates a new supplier")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public SupplierResponseDto createSupplier(
            @RequestBody @Valid CreateSupplierRequestDto request) {
        return supplierService.createSupplier(request);
    }

    @Operation(summary = "Update supplier", description =
            "Updates an existing supplier. This operation is restricted to ADMIN users.")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SupplierResponseDto updateSupplier(
            @PathVariable Long id, @RequestBody @Valid UpdateSupplierRequestDto request) {
        return supplierService.updateSupplier(id, request);
    }

    @Operation(summary = "Get suppliers",
            description = "Returns all suppliers")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public List<SupplierResponseDto> getSuppliers() {
        return supplierService.getSuppliers();
    }

    @Operation(summary = "Change supplier status",
            description = "Changes the status of an existing supplier."
                    + " This operation is restricted to ADMIN users.")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public SupplierResponseDto changeSupplierStatus(
            @PathVariable Long id, @RequestBody @Valid SupplierStatusRequestDto request) {
        return supplierService.changeSupplierStatus(id, request);
    }
}
