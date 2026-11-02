package property.manager.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
import property.manager.dto.meter.UpdateMeterReadingRequestDto;
import property.manager.service.MeterService;

@RestController
@RequestMapping("/api/meters")
@RequiredArgsConstructor
public class MeterController {

    private final MeterService meterService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public MeterResponseDto create(@RequestBody @Valid CreateMeterRequestDto request) {
        return meterService.create(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public MeterResponseDto getMeter(@PathVariable Long id) {
        return meterService.getMeterById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public List<MeterResponseDto> getMeters(@ModelAttribute MeterFilterDto filter) {
        return meterService.getMeters(filter);
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public MeterResponseDto toggleMeterActiveStatus(@PathVariable Long id) {
        return meterService.toggleMeterActiveStatus(id);
    }

    @PostMapping("/{meterId}/readings")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public MeterReadingResponseDto createReading(
            @PathVariable Long meterId, @RequestBody @Valid CreateMeterReadingRequestDto request) {
        return meterService.createReading(meterId, request);
    }

    @GetMapping("/readings/{readingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public MeterReadingResponseDto getReading(@PathVariable Long readingId) {
        return meterService.getReadingById(readingId);
    }

    @GetMapping("/readings")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public List<MeterReadingResponseDto> getReadings(
            @ModelAttribute MeterReadingFilterDto filter) {
        return meterService.getReadings(filter);
    }

    @PatchMapping("/readings/{readingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public MeterReadingResponseDto updateReading(
            @PathVariable Long readingId,
            @RequestBody @Valid UpdateMeterReadingRequestDto request) {
        return meterService.updateReadings(readingId, request);
    }
}
