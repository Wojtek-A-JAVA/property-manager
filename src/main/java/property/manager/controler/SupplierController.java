package property.manager.controler;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public SupplierResponseDto createSupplier(
            @RequestBody @Valid CreateSupplierRequestDto request) {
        return supplierService.createSupplier(request);
    }

    @PatchMapping("/{id}")
    public SupplierResponseDto updateSupplier(
            @PathVariable Long id, @RequestBody @Valid UpdateSupplierRequestDto request) {
        return supplierService.updateSupplier(id, request);
    }

    @GetMapping
    public List<SupplierResponseDto> getSuppliers() {
        return supplierService.getSuppliers();
    }

    @PatchMapping("/{id}/status")
    public SupplierResponseDto changeSupplierStatus(
            @PathVariable Long id, @RequestBody @Valid SupplierStatusRequestDto request) {
        return supplierService.changeSupplierStatus(id, request);
    }
}
