package property.manager.service;

import java.util.List;
import property.manager.dto.supplier.CreateSupplierRequestDto;
import property.manager.dto.supplier.SupplierResponseDto;
import property.manager.dto.supplier.SupplierStatusRequestDto;
import property.manager.dto.supplier.UpdateSupplierRequestDto;

public interface SupplierService {

    SupplierResponseDto createSupplier(CreateSupplierRequestDto request);

    SupplierResponseDto updateSupplier(Long id, UpdateSupplierRequestDto request);

    List<SupplierResponseDto> getSuppliers();

    SupplierResponseDto changeSupplierStatus(Long id, SupplierStatusRequestDto request);
}
