package property.manager.service.impl;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import property.manager.dto.tenant.CreateTenantRequestDto;
import property.manager.dto.tenant.TenantFilterDto;
import property.manager.dto.tenant.TenantResponseDto;
import property.manager.dto.tenant.UpdateTenantRequestDto;
import property.manager.exception.EntityAlreadyExistsException;
import property.manager.exception.EntityNotFoundException;
import property.manager.exception.InvalidTenantDataException;
import property.manager.mapper.TenantMapper;
import property.manager.model.Tenant;
import property.manager.model.TenantType;
import property.manager.repository.TenantRepository;
import property.manager.service.TenantService;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    @Override
    public TenantResponseDto createTenant(CreateTenantRequestDto request) {

        if (request.type() == TenantType.INDIVIDUAL) {
            validateIndividualTenant(request);
        } else if (request.type() == TenantType.COMPANY) {
            validateCompanyTenant(request);
        }

        checkDuplicates(request);

        Tenant tenant = tenantMapper.toEntity(request);

        if (isMissing(tenant.getPesel())) {
            tenant.setPesel(null);
        }
        if (isMissing(tenant.getTaxId())) {
            tenant.setTaxId(null);
        }
        if (request.type() == TenantType.INDIVIDUAL) {
            setDefaultContactData(tenant);
        }

        Tenant tenantSaved = tenantRepository.save(tenant);
        return tenantMapper.toDto(tenantSaved);
    }

    @Override
    public TenantResponseDto getTenant(Long id) {
        Tenant tenant = findTenantById(id);
        return tenantMapper.toDto(tenant);
    }

    @Override
    public TenantResponseDto updateTenant(Long id, UpdateTenantRequestDto request) {
        Tenant tenant = findTenantById(id);
        validateTenantUpdate(tenant, request);
        checkDuplicatesForUpdate(id, request);
        tenantMapper.updateEntity(request, tenant);
        Tenant savedTenant = tenantRepository.save(tenant);
        return tenantMapper.toDto(savedTenant);
    }

    @Override
    public TenantResponseDto toggleActiveStatus(Long id) {
        Tenant tenant = findTenantById(id);
        tenant.setActive(!tenant.isActive());
        Tenant savedTenant = tenantRepository.save(tenant);
        return tenantMapper.toDto(savedTenant);
    }

    @Override
    public List<TenantResponseDto> getTenants(TenantFilterDto filter) {
        List<Tenant> tenantList;
        if (filter.active() != null && filter.type() == null) {
            tenantList = tenantRepository.findByActive(filter.active());
        } else if (filter.active() == null && filter.type() != null) {
            tenantList = tenantRepository.findByType(filter.type());
        } else if (filter.active() != null && filter.type() != null) {
            tenantList = tenantRepository.findByActiveAndType(filter.active(), filter.type());
        } else {
            tenantList = List.of();
        }
        return tenantMapper.toDtoList(tenantList);
    }

    private boolean isProvided(String value) {
        return value != null && !value.isBlank();
    }

    private boolean isMissing(String value) {
        return value == null || value.isBlank();
    }

    private void validateIndividualTenant(CreateTenantRequestDto request) {
        if (isMissing(request.firstName()) || isMissing(request.lastName())) {
            throw new InvalidTenantDataException(
                    "Individual tenant requires firstName and lastName");
        }
        if (isProvided(request.companyName())) {
            throw new InvalidTenantDataException(
                    "CompanyName must not be provided for individual tenant");
        }
    }

    private void validateCompanyTenant(CreateTenantRequestDto request) {
        if (isMissing(request.companyName()) || isMissing(request.taxId())
                || isMissing(request.contactFirstName()) || isMissing(request.contactLastName())) {
            throw new InvalidTenantDataException("Company tenant requires companyName, "
                    + "taxId, contactFirstName and contactLastName");
        }
        if ((isProvided(request.firstName()))
                || (isProvided(request.lastName()))
                || (isProvided(request.pesel()))
        ) {
            throw new InvalidTenantDataException("Company tenant firstName, lastName "
                    + " and pesel must not be provided");
        }
    }

    private void checkDuplicates(CreateTenantRequestDto request) {
        if (request.type() == TenantType.COMPANY) {
            if (tenantRepository.existsByTaxId(request.taxId())) {
                throw new EntityAlreadyExistsException("Company tenant with tax id "
                        + request.taxId() + " already exists in database");
            }
            if (tenantRepository.existsByCompanyName(request.companyName())) {
                throw new EntityAlreadyExistsException("Company tenant with name "
                        + request.companyName() + " already exists in database");
            }
        } else if (request.type() == TenantType.INDIVIDUAL) {
            if ((isProvided(request.pesel()))
                    && tenantRepository.existsByPesel(request.pesel())) {
                throw new EntityAlreadyExistsException("Individual tenant with pesel "
                        + request.pesel() + " already exists in database");
            }
            if (tenantRepository.existsByFirstNameAndLastName(
                    request.firstName(), request.lastName())) {
                throw new EntityAlreadyExistsException("Individual tenant with first name "
                        + request.firstName() + " and last name " + request.lastName()
                        + " already exists in database");
            }
        }
    }

    private void setDefaultContactData(Tenant tenant) {
        if (isMissing(tenant.getContactFirstName())) {
            tenant.setContactFirstName(tenant.getFirstName());
        }
        if (isMissing(tenant.getContactLastName())) {
            tenant.setContactLastName(tenant.getLastName());
        }
    }

    private Tenant findTenantById(Long id) {
        return tenantRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Tenant with id " + id
                        + " doesn't exist in database")
        );
    }

    private void checkDuplicatesForUpdate(Long id, UpdateTenantRequestDto request) {
        if (isProvided(request.pesel())
                && tenantRepository.existsByPeselAndIdNot(request.pesel(), id)) {
            throw new EntityAlreadyExistsException("Pesel " + request.pesel()
                    + " already exists in database");
        }
        if (isProvided(request.taxId())
                && tenantRepository.existsByTaxIdAndIdNot(request.taxId(), id)) {
            throw new EntityAlreadyExistsException("Tax id " + request.taxId()
                    + " already exists in database");
        }
    }

    private void validateTenantUpdate(Tenant tenant, UpdateTenantRequestDto request) {
        if (tenant.getType() == TenantType.COMPANY) {
            if (isProvided(request.firstName())) {
                throw new InvalidTenantDataException("First name can't be added to company tenant");
            }
            if (isProvided(request.lastName())) {
                throw new InvalidTenantDataException("Last name can't be added to company tenant");
            }
            if (isProvided(request.pesel())) {
                throw new InvalidTenantDataException("PESEL can't be added to company tenant");
            }
        }
        if (tenant.getType() == TenantType.INDIVIDUAL) {
            if (isProvided(request.companyName())) {
                throw new InvalidTenantDataException("Company name can't be added to "
                        + "individual tenant");
            }
        }
    }
}
