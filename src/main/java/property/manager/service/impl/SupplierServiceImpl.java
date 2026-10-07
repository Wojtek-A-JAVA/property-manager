package property.manager.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import property.manager.dto.supplier.CreateSupplierRequestDto;
import property.manager.dto.supplier.SupplierResponseDto;
import property.manager.dto.supplier.SupplierStatusRequestDto;
import property.manager.dto.supplier.UpdateSupplierRequestDto;
import property.manager.exception.EntityAlreadyExistsException;
import property.manager.exception.EntityNotFoundException;
import property.manager.exception.InvalidSupplierDataException;
import property.manager.mapper.SupplierMapper;
import property.manager.model.Supplier;
import property.manager.repository.SupplierRepository;
import property.manager.service.SupplierService;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public SupplierResponseDto createSupplier(CreateSupplierRequestDto request) {
        if (supplierRepository.existsByTaxId(request.taxId())) {
            throw new EntityAlreadyExistsException("Supplier with tax id "
                    + request.taxId() + " already exists in database");
        }
        Supplier supplier = supplierMapper.toEntity(request);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(savedSupplier);
    }

    @Override
    public SupplierResponseDto updateSupplier(Long id, UpdateSupplierRequestDto request) {
        if (request.name() == null && request.taxId() == null) {
            throw new InvalidSupplierDataException("Update is empty");
        }
        if (request.name() != null && request.name().isBlank()) {
            throw new InvalidSupplierDataException("Supplier name can't be blank");
        }
        Supplier supplier = findSupplier(id);
        if (request.taxId() != null && !supplier.getTaxId().equals(request.taxId())
                && supplierRepository.existsByTaxId(request.taxId())) {
            throw new EntityAlreadyExistsException("Tax id " + request.taxId()
                    + " already exist in database");
        }
        if (request.name() != null) {
            supplier.setName(request.name());
        }
        if (request.taxId() != null) {
            supplier.setTaxId(request.taxId());
        }
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(savedSupplier);
    }

    @Override
    public List<SupplierResponseDto> getSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();
        return supplierMapper.toDtoList(suppliers);
    }

    @Override
    public SupplierResponseDto changeSupplierStatus(Long id, SupplierStatusRequestDto request) {
        Supplier supplier = findSupplier(id);
        supplier.setActive(request.active());
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(savedSupplier);
    }

    private Supplier findSupplier(Long id) {
        return supplierRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Supplier with id " + id
                        + " doesn't exist in database")
        );
    }
}
