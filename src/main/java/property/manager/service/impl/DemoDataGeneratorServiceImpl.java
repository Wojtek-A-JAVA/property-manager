package property.manager.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import property.manager.dto.settlement.CreateSettlementRequestDto;
import property.manager.exception.InvalidMeterDataException;
import property.manager.exception.InvalidUtilityInvoiceDataException;
import property.manager.model.MeasurementType;
import property.manager.model.MeterPurpose;
import property.manager.model.MeterReading;
import property.manager.model.UtilityInvoice;
import property.manager.model.UtilityType;
import property.manager.repository.MeterReadingRepository;
import property.manager.repository.UtilityInvoiceRepository;
import property.manager.service.DemoDataGeneratorService;
import property.manager.service.SettlementService;

@Service
@RequiredArgsConstructor
public class DemoDataGeneratorServiceImpl implements DemoDataGeneratorService {

    private final MeterReadingRepository meterReadingRepository;
    private final UtilityInvoiceRepository utilityInvoiceRepository;
    private final SettlementService settlementService;

    @Override
    @Transactional
    public void generateMissingDemoData() {
        YearMonth lastReadingMonth = meterReadingRepository.findMaxReadingMonth()
                .orElseThrow(() -> new EntityNotFoundException("No meter readings found"));
        LocalDate lastInvoiceDate = utilityInvoiceRepository.findMaxInvoiceDate()
                .orElseThrow(() -> new EntityNotFoundException("No utility invoices found"));
        while (lastReadingMonth.isBefore(YearMonth.now())) {
            List<Long> meterIds = meterReadingRepository.findByReadingMonth(lastReadingMonth)
                    .stream()
                    .map(meterReading -> meterReading.getMeter().getId()).toList();
            generateMeterReadings(meterIds, lastReadingMonth);
            lastReadingMonth = lastReadingMonth.plusMonths(1L);
        }
        while (YearMonth.from(lastInvoiceDate).isBefore(YearMonth.now())) {
            List<Long> invoiceIds = utilityInvoiceRepository.findByInvoiceDate(lastInvoiceDate)
                    .stream().map(UtilityInvoice::getId).toList();
            List<UtilityInvoice> generatedUtilityInvoices = generateUtilityInvoices(
                    invoiceIds, lastInvoiceDate);
            generateSettlements(generatedUtilityInvoices);
            lastInvoiceDate = generatedUtilityInvoices.getFirst().getInvoiceDate();
        }
    }

    private void generateMeterReadings(List<Long> meterIds, YearMonth readingMonth) {
        List<MeterReading> meterReadings = new ArrayList<>();
        for (Long id : meterIds) {
            meterReadings.add(meterReadingRepository.findByMeterIdAndReadingMonth(id, readingMonth)
                    .orElseThrow(() -> new EntityNotFoundException("Meter with id " + id
                            + " and month " + readingMonth + " doesn't exist in database")));
        }
        for (MeterReading meterReading : meterReadings) {
            int randomIncrease = 0;
            if (meterReading.getMeter().getMeasurementType() == MeasurementType.ELECTRICITY
                    && meterReading.getMeter().getPurpose() == MeterPurpose.GENERAL) {
                randomIncrease = ThreadLocalRandom.current().nextInt(100, 301);
            } else if (meterReading.getMeter().getMeasurementType() == MeasurementType.WATER
                    && meterReading.getMeter().getPurpose() == MeterPurpose.COLD_WATER) {
                randomIncrease = ThreadLocalRandom.current().nextInt(2, 8);
            } else if (meterReading.getMeter().getMeasurementType() == MeasurementType.ELECTRICITY
                    && meterReading.getMeter().getPurpose() == MeterPurpose.HEATING
            ) {
                randomIncrease = ThreadLocalRandom.current().nextInt(100, 500);
            } else {
                throw new InvalidMeterDataException("Unsupported measurement type when generating"
                        + " meter reading: " + meterReading.getMeter().getMeasurementType());
            }
            MeterReading newMeterReading = new MeterReading();
            newMeterReading.setMeter(meterReading.getMeter());
            newMeterReading.setReadingMonth(readingMonth.plusMonths(1L));
            newMeterReading.setValue(meterReading.getValue().add(
                    BigDecimal.valueOf(randomIncrease)));
            meterReadingRepository.save(newMeterReading);
        }
    }

    private List<UtilityInvoice> generateUtilityInvoices(
            List<Long> invoiceIds, LocalDate invoiceDate) {
        List<UtilityInvoice> invoices = utilityInvoiceRepository.findAllById(invoiceIds);
        invoiceDate = invoiceDate.withDayOfMonth(1);
        LocalDate nextInvoiceDate = invoiceDate.plusMonths(1L);
        List<UtilityInvoice> utilityInvoices = new ArrayList<>();
        for (UtilityInvoice invoice : invoices) {
            StringBuilder sb = new StringBuilder().append(nextInvoiceDate.getYear()).append("/")
                    .append(nextInvoiceDate.getMonthValue()).append("/P")
                    .append(invoice.getProperty().getId());
            BigDecimal consumption = BigDecimal.ZERO;
            BigDecimal amount = invoice.getAmount();
            UtilityInvoice newUtilityInvoice = new UtilityInvoice();
            if (invoice.getUtilityType() == UtilityType.ELECTRICITY) {
                sb.insert(0, "ELE/");
                consumption = getRandomConsumption(invoice, -100, 101);
                amount = consumption.multiply(invoice.getUnitPrice());
            } else if (invoice.getUtilityType() == UtilityType.WASTE) {
                sb.insert(0, "WST/");
                consumption = null;
            } else if (invoice.getUtilityType() == UtilityType.CLEANING) {
                sb.insert(0, "CLN/");
                consumption = null;
            } else if (invoice.getUtilityType() == UtilityType.WATER) {
                sb.insert(0, "WTR/");
                consumption = getRandomConsumption(invoice, -5, 6);
                amount = consumption.multiply(invoice.getUnitPrice());
            } else {
                throw new InvalidUtilityInvoiceDataException("Unsupported utility type when "
                        + "generating utility invoice: " + invoice.getUtilityType());
            }
            newUtilityInvoice.setProperty(invoice.getProperty());
            newUtilityInvoice.setUtilityType(invoice.getUtilityType());
            newUtilityInvoice.setSupplier(invoice.getSupplier());
            newUtilityInvoice.setInvoiceNumber(sb.toString());
            newUtilityInvoice.setInvoiceDate(nextInvoiceDate);
            newUtilityInvoice.setBillingFrom(invoiceDate);
            newUtilityInvoice.setBillingTo(nextInvoiceDate.minusDays(1));
            newUtilityInvoice.setConsumption(consumption);
            newUtilityInvoice.setAmount(amount);
            newUtilityInvoice.setUnitPrice(invoice.getUnitPrice());
            utilityInvoiceRepository.save(newUtilityInvoice);
            utilityInvoices.add(newUtilityInvoice);
        }
        return utilityInvoices;
    }

    private BigDecimal getRandomConsumption(UtilityInvoice invoice, int min, int max) {
        BigDecimal consumption = invoice.getConsumption().add(BigDecimal.valueOf(
                ThreadLocalRandom.current().nextInt(min, max)));
        if (consumption.compareTo(BigDecimal.ZERO) <= 0) {
            consumption = invoice.getConsumption().add(BigDecimal.valueOf(5));
        }
        return consumption;
    }

    private void generateSettlements(List<UtilityInvoice> utilityInvoiceList) {
        for (UtilityInvoice utilityInvoice : utilityInvoiceList) {
            List<UtilityType> utilityTypes = switch (utilityInvoice.getUtilityType()) {
                case ELECTRICITY -> List.of(UtilityType.ELECTRICITY, UtilityType.HEATING);
                case WASTE -> List.of(UtilityType.WASTE);
                case CLEANING -> List.of(UtilityType.CLEANING);
                case WATER -> List.of(UtilityType.WATER_COLD);
                default -> throw new IllegalStateException("Unexpected value: "
                        + utilityInvoice.getUtilityType());
            };
            settlementService.createSettlement(
                    utilityInvoice.getId(), new CreateSettlementRequestDto(utilityTypes));
        }
    }
}
