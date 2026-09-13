package property.manager.controler;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import property.manager.dto.meter.CreateMeterReadingRequestDto;
import property.manager.dto.meter.CreateMeterRequestDto;
import property.manager.dto.meter.MeterFilterDto;
import property.manager.dto.meter.MeterReadingFilterDto;
import property.manager.dto.meter.MeterReadingResponseDto;
import property.manager.dto.meter.MeterResponseDto;
import property.manager.service.MeterService;

@RestController
@RequestMapping("/api/meters")
@RequiredArgsConstructor
public class MeterController {

    private final MeterService meterService;

    @PostMapping
    public MeterResponseDto create(@RequestBody @Valid CreateMeterRequestDto request) {
        return meterService.create(request);
    }

    @GetMapping("/{id}")
    public MeterResponseDto getMeter(@PathVariable Long id) {
        return meterService.getMeterById(id);
    }

    @GetMapping
    public List<MeterResponseDto> getMeters(@ModelAttribute MeterFilterDto filter) {
        return meterService.getMeters(filter);
    }

    @PatchMapping("/{id}/active")
    public MeterResponseDto toggleMeterActiveStatus(@PathVariable Long id) {
        return meterService.toggleMeterActiveStatus(id);
    }

    @PostMapping("/{meterId}/readings")
    public MeterReadingResponseDto createReading(
            @PathVariable Long meterId, @RequestBody @Valid CreateMeterReadingRequestDto request) {
        return meterService.createReading(meterId, request);
    }

    @GetMapping("/readings/{readingId}")
    public MeterReadingResponseDto getReading(@PathVariable Long readingId) {
        return meterService.getReadingById(readingId);
    }

    @GetMapping("/readings")
    public List<MeterReadingResponseDto> getReadings(
            @ModelAttribute MeterReadingFilterDto filter) {
        return meterService.getReadings(filter);
    }
}
