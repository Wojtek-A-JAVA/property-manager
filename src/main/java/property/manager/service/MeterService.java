package property.manager.service;

import java.util.List;
import property.manager.dto.meter.CreateMeterReadingRequestDto;
import property.manager.dto.meter.CreateMeterRequestDto;
import property.manager.dto.meter.MeterFilterDto;
import property.manager.dto.meter.MeterReadingFilterDto;
import property.manager.dto.meter.MeterReadingResponseDto;
import property.manager.dto.meter.MeterResponseDto;

public interface MeterService {

    MeterResponseDto create(CreateMeterRequestDto request);

    MeterResponseDto getMeterById(Long id);

    List<MeterResponseDto> getMeters(MeterFilterDto filter);

    MeterResponseDto toggleMeterActiveStatus(Long id);

    MeterReadingResponseDto createReading(Long id, CreateMeterReadingRequestDto request);

    MeterReadingResponseDto getReadingById(Long readingId);

    List<MeterReadingResponseDto> getReadings(MeterReadingFilterDto filter);
}
