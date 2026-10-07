package property.manager.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import property.manager.dto.cost.property.CreatePropertyCostRequestDto;
import property.manager.dto.cost.property.PropertyCostFilterDto;
import property.manager.dto.cost.property.PropertyCostResponseDto;
import property.manager.dto.cost.property.UpdatePropertyCostRequestDto;
import property.manager.exception.EntityAlreadyExistsException;
import property.manager.exception.InvalidPropertyCostDataException;
import property.manager.mapper.PropertyCostMapper;
import property.manager.model.Property;
import property.manager.model.PropertyCost;
import property.manager.model.UtilityInvoice;
import property.manager.repository.PropertyCostRepository;
import property.manager.repository.PropertyRepository;
import property.manager.repository.UtilityInvoiceRepository;
import property.manager.service.PropertyCostService;
import property.manager.specification.PropertyCostSpecification;

@Service
@RequiredArgsConstructor
public class PropertyCostServiceImpl implements PropertyCostService {

    private final PropertyRepository propertyRepository;
    private final UtilityInvoiceRepository utilityInvoiceRepository;
    private final PropertyCostRepository propertyCostRepository;
    private final PropertyCostMapper propertyCostMapper;

    @Override
    @Transactional
    public List<PropertyCostResponseDto> createPropertyCost(CreatePropertyCostRequestDto request) {
        Property property = getProperty(request.propertyId());
        UtilityInvoice utilityInvoice = getInvoice(request.utilityInvoiceId());
        checkProperty(property.getId(), utilityInvoice);
        List<PropertyCost> propertyCostList = createPropertyCosts(utilityInvoice);
        List<PropertyCostResponseDto> propertyCostResponseDtoList = new ArrayList<>();
        for (PropertyCost propertyCost : propertyCostList) {
            propertyCostResponseDtoList.add(propertyCostMapper.toDto(propertyCost));
        }
        return propertyCostResponseDtoList;
    }

    @Override
    public PropertyCostResponseDto getPropertyCost(Long id) {
        PropertyCost propertyCost = findPropertyCost(id);
        return propertyCostMapper.toDto(propertyCost);
    }

    @Override
    public List<PropertyCostResponseDto> getPropertyCosts(PropertyCostFilterDto filter) {
        Specification<PropertyCost> specification = PropertyCostSpecification
                .hasPropertyId(filter.propertyId())
                .and(PropertyCostSpecification.hasBillingMonthBetween(
                        filter.billingMonthFrom(), filter.billingMonthTo()))
                .and(PropertyCostSpecification.hasAmountBetween(
                        filter.amountFrom(), filter.amountTo()))
                .and(PropertyCostSpecification.hasUtilityTypes(filter.utilityTypes()))
                .and(PropertyCostSpecification.hasNotes(filter.hasNotes()));
        List<PropertyCost> propertyCosts = propertyCostRepository.findAll(specification);
        return propertyCostMapper.toDtoList(propertyCosts);
    }

    @Override
    @Transactional
    public List<PropertyCostResponseDto> updatePropertyCost(
            Long id, UpdatePropertyCostRequestDto request) {
        if (request.propertyId() == null && request.utilityInvoiceId() == null) {
            throw new InvalidPropertyCostDataException("Update is empty");
        }
        PropertyCost existingCost = findPropertyCost(id);
        Long originalInvoiceId = existingCost.getUtilityInvoice().getId();
        List<PropertyCost> existingCosts = propertyCostRepository.findAllByUtilityInvoice_Id(
                originalInvoiceId);
        existingCosts.sort(Comparator.comparing(PropertyCost::getBillingMonth));
        Property property = request.propertyId() != null
                ? getProperty(request.propertyId()) : existingCost.getProperty();
        UtilityInvoice invoice = request.utilityInvoiceId() != null
                ? getInvoice(request.utilityInvoiceId()) : existingCost.getUtilityInvoice();
        if (!property.getId().equals(invoice.getProperty().getId())) {
            throw new InvalidPropertyCostDataException("Property id " + property.getId()
                    + " doesn't match property id " + invoice.getProperty().getId()
                    + " in utility invoice with id " + invoice.getId());
        }
        YearMonth startMonth = YearMonth.from(invoice.getBillingFrom());
        YearMonth endMonth = YearMonth.from(invoice.getBillingTo());
        if (propertyCostRepository.existsConflictingCost(
                property.getId(), invoice.getUtilityType(), startMonth, endMonth,
                originalInvoiceId)) {
            throw new EntityAlreadyExistsException("Property cost for property with id "
                    + property.getId() + ", utility type " + invoice.getUtilityType()
                    .name().toLowerCase(Locale.ROOT).replace('_', ' ')
                    + " and billing period " + startMonth + " - " + endMonth
                    + " already exists in database");
        }
        int numberOfMonths = Math.toIntExact(ChronoUnit.MONTHS.between(startMonth, endMonth) + 1);
        BigDecimal amount = invoice.getAmount().divide(
                BigDecimal.valueOf(numberOfMonths), 2, RoundingMode.HALF_UP);
        String notes = null;
        if (numberOfMonths > 1) {
            notes = "Property cost created from a " + numberOfMonths + "-month utility invoice";
        }
        List<PropertyCost> updatedCosts = new ArrayList<>();
        for (int i = 0; i < numberOfMonths; i++) {
            PropertyCost cost;
            if (i < existingCosts.size()) {
                cost = existingCosts.get(i);
            } else {
                cost = new PropertyCost();
            }
            BigDecimal monthlyAmount = amount;
            if (i == numberOfMonths - 1) {
                monthlyAmount = invoice.getAmount().subtract(
                        amount.multiply(BigDecimal.valueOf(numberOfMonths - 1)));
            }
            cost.setProperty(property);
            cost.setUtilityInvoice(invoice);
            cost.setBillingMonth(startMonth.plusMonths(i));
            cost.setAmount(monthlyAmount);
            cost.setNotes(notes);
            updatedCosts.add(cost);
        }
        if (existingCosts.size() > numberOfMonths) {
            List<PropertyCost> costsToDelete = existingCosts.subList(
                    numberOfMonths, existingCosts.size());
            propertyCostRepository.deleteAll(costsToDelete);
        }
        List<PropertyCost> savedCosts = propertyCostRepository.saveAll(updatedCosts);
        return propertyCostMapper.toDtoList(savedCosts);
    }

    @Override
    @Transactional
    public List<PropertyCost> createPropertyCosts(UtilityInvoice utilityInvoice) {
        YearMonth startMonth = YearMonth.from(utilityInvoice.getBillingFrom());
        YearMonth endMonth = YearMonth.from(utilityInvoice.getBillingTo());
        checkDuplicate(utilityInvoice.getProperty(), utilityInvoice, startMonth, endMonth);
        List<PropertyCost> propertyCostList = new ArrayList<>();
        long numberOfMonths = ChronoUnit.MONTHS.between(startMonth, endMonth) + 1;
        BigDecimal amount = utilityInvoice.getAmount().divide(
                BigDecimal.valueOf(numberOfMonths), 2, RoundingMode.HALF_UP);
        String notes = null;
        if (numberOfMonths > 1) {
            notes = "Property cost created from a " + numberOfMonths + "-month utility invoice";
        }
        for (int i = 0; i < numberOfMonths; i++) {
            PropertyCost cost = new PropertyCost(utilityInvoice.getProperty(), utilityInvoice,
                    amount, startMonth.plusMonths(i), notes);
            if (numberOfMonths > 1 && i == numberOfMonths - 1) {
                cost.setAmount(utilityInvoice.getAmount().subtract(
                        amount.multiply(BigDecimal.valueOf(numberOfMonths - 1))
                ));
            }
            PropertyCost savedCost = propertyCostRepository.save(cost);
            propertyCostList.add(savedCost);
        }
        return propertyCostList;
    }

    private Property getProperty(Long id) {
        return propertyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Property with id " + id
                        + " doesn't exist in database"));
    }

    private UtilityInvoice getInvoice(Long id) {
        return utilityInvoiceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Utility invoice with id " + id
                        + " doesn't exist in database"));
    }

    private void checkProperty(Long propertyId, UtilityInvoice invoice) {
        if (!propertyId.equals(invoice.getProperty().getId())) {
            throw new InvalidPropertyCostDataException("Property id " + propertyId
                    + " doesn't match with property id on invoice with id " + invoice.getId());
        }
    }

    private void checkDuplicate(Property property, UtilityInvoice invoice,
                                YearMonth startMonth, YearMonth endMonth) {
        if (propertyCostRepository
                .existsByPropertyIdAndUtilityInvoiceUtilityTypeAndBillingMonthBetween(
                        property.getId(), invoice.getUtilityType(), startMonth, endMonth)) {
            throw new EntityAlreadyExistsException("Property cost for property with id "
                    + property.getId() + ", utility type " + invoice.getUtilityType()
                    .name().toLowerCase(Locale.ROOT).replace('_', ' ')
                    + " and billing period " + startMonth + " - " + endMonth
                    + " already exists in database");
        }
    }

    private PropertyCost findPropertyCost(Long id) {
        return propertyCostRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "Property cost with id " + id + " doesn't exist in database")
        );
    }
}
