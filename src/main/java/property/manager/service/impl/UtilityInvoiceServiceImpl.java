package property.manager.service.impl;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import property.manager.dto.utility.invoice.CreateUtilityInvoiceRequestDto;
import property.manager.dto.utility.invoice.UpdateUtilityInvoiceRequestDto;
import property.manager.dto.utility.invoice.UtilityInvoiceFilterDto;
import property.manager.dto.utility.invoice.UtilityInvoiceResponseDto;
import property.manager.exception.EntityAlreadyExistsException;
import property.manager.exception.EntityNotFoundException;
import property.manager.exception.InactiveEntityException;
import property.manager.exception.InvalidUtilityInvoiceDataException;
import property.manager.mapper.UtilityInvoiceMapper;
import property.manager.model.Property;
import property.manager.model.Supplier;
import property.manager.model.UtilityInvoice;
import property.manager.repository.PropertyRepository;
import property.manager.repository.SupplierRepository;
import property.manager.repository.UtilityInvoiceRepository;
import property.manager.service.UtilityInvoiceService;
import property.manager.specification.UtilityInvoiceSpecification;

@Service
@RequiredArgsConstructor
public class UtilityInvoiceServiceImpl implements UtilityInvoiceService {

    private final PropertyRepository propertyRepository;
    private final SupplierRepository supplierRepository;
    private final UtilityInvoiceRepository utilityInvoiceRepository;
    private final UtilityInvoiceMapper utilityInvoiceMapper;

    @Override
    public UtilityInvoiceResponseDto createUtilityInvoice(CreateUtilityInvoiceRequestDto request) {
        checkDates(request.billingFrom(), request.billingTo(), request.invoiceDate());
        checkDuplicate(request.supplierId(), request.invoiceNumber());
        Property property = getProperty(request.propertyId());
        Supplier supplier = findSupplier(request.supplierId());
        UtilityInvoice invoice = utilityInvoiceMapper.toEntity(request);
        if (request.consumption() != null) {
            invoice.setUnitPrice(request.amount().divide(
                    request.consumption(), 2, RoundingMode.HALF_UP));
        }
        invoice.setProperty(property);
        invoice.setSupplier(supplier);
        UtilityInvoice savedInvoice = utilityInvoiceRepository.save(invoice);
        return utilityInvoiceMapper.toDto(savedInvoice);
    }

    @Override
    public UtilityInvoiceResponseDto getUtilityInvoice(Long id) {
        UtilityInvoice invoice = getUtilityInvoiceEntity(id);
        return utilityInvoiceMapper.toDto(invoice);
    }

    @Override
    public List<UtilityInvoiceResponseDto> getUtilityInvoices(UtilityInvoiceFilterDto filter) {
        Specification<UtilityInvoice> specification = UtilityInvoiceSpecification
                .hasPropertyId(filter.propertyId())
                .and(UtilityInvoiceSpecification.hasUtilityType(filter.utilityType()))
                .and(UtilityInvoiceSpecification.hasSupplierTaxId(filter.supplierTaxId()))
                .and(UtilityInvoiceSpecification.hasInvoiceDateBetween(
                        filter.invoiceDateFrom(), filter.invoiceDateTo()))
                .and(UtilityInvoiceSpecification.hasBillingPeriodOverlap(
                        filter.billingFrom(), filter.billingTo()))
                .and(UtilityInvoiceSpecification.hasAmountBetween(
                        filter.amountFrom(), filter.amountTo()));
        List<UtilityInvoice> utilityInvoices = utilityInvoiceRepository.findAll(specification);
        return utilityInvoiceMapper.toDto(utilityInvoices);
    }

    @Override
    public UtilityInvoiceResponseDto updateUtilityInvoice(
            Long id, UpdateUtilityInvoiceRequestDto request) {
        UtilityInvoice invoice = getUtilityInvoiceEntity(id);

        LocalDate billingFrom =
                request.billingFrom() != null ? request.billingFrom() : invoice.getBillingFrom();
        LocalDate billingTo =
                request.billingTo() != null ? request.billingTo() : invoice.getBillingTo();
        LocalDate invoiceDate =
                request.invoiceDate() != null ? request.invoiceDate() : invoice.getInvoiceDate();
        checkDates(billingFrom, billingTo, invoiceDate);
        invoice.setBillingFrom(billingFrom);
        invoice.setBillingTo(billingTo);
        invoice.setInvoiceDate(invoiceDate);

        if (request.propertyId() != null) {
            Property property = getProperty(request.propertyId());
            invoice.setProperty(property);
        }
        if (request.utilityType() != null) {
            invoice.setUtilityType(request.utilityType());
        }
        checkInvoiceNumber(request.invoiceNumber());
        Long supplierId = request.supplierId() != null
                ? request.supplierId() : invoice.getSupplier().getId();
        String invoiceNumber = request.invoiceNumber() != null
                ? request.invoiceNumber() : invoice.getInvoiceNumber();
        Supplier supplier = findSupplier(supplierId);
        if (!invoice.getSupplier().getId().equals(supplierId)
                || !invoice.getInvoiceNumber().equals(invoiceNumber)) {
            checkDuplicate(supplierId, invoiceNumber);
        }
        invoice.setSupplier(supplier);
        invoice.setInvoiceNumber(invoiceNumber);

        if (request.amount() != null) {
            invoice.setAmount(request.amount());
        }
        if (request.consumption() != null) {
            invoice.setConsumption(request.consumption());
        }
        if (invoice.getConsumption() != null && (request.amount() != null
                || request.consumption() != null)) {
            invoice.setUnitPrice(invoice.getAmount().divide(
                    invoice.getConsumption(), 2, RoundingMode.HALF_UP));
        }
        if (request.notes() != null) {
            invoice.setNotes(request.notes());
        }
        UtilityInvoice savedInvoice = utilityInvoiceRepository.save(invoice);
        return utilityInvoiceMapper.toDto(savedInvoice);
    }

    @Override
    public UtilityInvoice getUtilityInvoiceEntity(Long id) {
        return utilityInvoiceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Utility invoice with id " + id
                        + " doesn't exist in database")
        );
    }

    private void checkInvoiceNumber(String invoiceNumber) {
        if (invoiceNumber != null && invoiceNumber.isBlank()) {
            throw new InvalidUtilityInvoiceDataException("Invoice number can't be blank");
        }
    }

    private void checkDates(LocalDate billingFrom, LocalDate billingTo, LocalDate invoiceDate) {
        if (billingTo.isBefore(billingFrom)) {
            throw new InvalidUtilityInvoiceDataException("Billing to date " + billingTo
                    + " is before billing from date " + billingFrom);
        }
        if (invoiceDate.isBefore(billingTo)) {
            throw new InvalidUtilityInvoiceDataException("Invoice date " + invoiceDate
                    + " is before billing date " + billingTo);
        }
    }

    private Property getProperty(Long id) {
        Property property = propertyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Property with id " + id
                        + " doesn't exist in database"));
        if (!property.isActive()) {
            throw new InactiveEntityException("Property with id " + id
                    + " is inactive");
        }
        return property;
    }

    private Supplier findSupplier(Long supplierId) {
        return supplierRepository.findById(supplierId).orElseThrow(
                () -> new EntityNotFoundException("Supplier with id " + supplierId
                        + " doesn't exist in database")
        );
    }

    private void checkDuplicate(Long supplierId, String invoiceNumber) {
        if (utilityInvoiceRepository.existsBySupplierIdAndInvoiceNumber(
                supplierId, invoiceNumber)) {
            throw new EntityAlreadyExistsException("Utility invoice with nr "
                    + invoiceNumber + " from supplier with id " + supplierId
                    + " already exists in database");
        }
    }

}
