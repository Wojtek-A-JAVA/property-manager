package property.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "09. Meters & Readings")
@RestController
@RequestMapping("/api/meters")
@RequiredArgsConstructor
public class MeterController {

    private final MeterService meterService;

    @Operation(summary = "Create meter",
            description = "Creates a new meter for a property or unit")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public MeterResponseDto create(@RequestBody @Valid CreateMeterRequestDto request) {
        return meterService.create(request);
    }

    @Operation(summary = "Get meter",
            description = "Returns a meter by its id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public MeterResponseDto getMeter(@PathVariable Long id) {
        return meterService.getMeterById(id);
    }

    @Operation(summary = "Get meters",
            description = "Returns meters matching the provided filters")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public List<MeterResponseDto> getMeters(@ModelAttribute MeterFilterDto filter) {
        return meterService.getMeters(filter);
    }

    @Operation(summary = "Change meter active status", description =
            "Activates or deactivates an existing meter."
                    + " This operation is restricted to ADMIN users.")
    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public MeterResponseDto toggleMeterActiveStatus(@PathVariable Long id) {
        return meterService.toggleMeterActiveStatus(id);
    }

    @Operation(summary = "Create meter reading",
            description = "Creates a new reading for the specified meter")
    @PostMapping("/{meterId}/readings")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public MeterReadingResponseDto createReading(
            @PathVariable Long meterId, @RequestBody @Valid CreateMeterReadingRequestDto request) {
        return meterService.createReading(meterId, request);
    }

    @Operation(summary = "Get meter reading",
            description = "Returns a meter reading by its id")
    @GetMapping("/readings/{readingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public MeterReadingResponseDto getReading(@PathVariable Long readingId) {
        return meterService.getReadingById(readingId);
    }

    @Operation(summary = "Get meter readings",
            description = "Returns meter readings matching the provided filters")
    @GetMapping("/readings")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public List<MeterReadingResponseDto> getReadings(
            @ModelAttribute MeterReadingFilterDto filter) {
        return meterService.getReadings(filter);
    }

    @Operation(summary = "Update meter reading", description =
            "Updates an existing meter reading. This operation is restricted to ADMIN users.")
    @PatchMapping("/readings/{readingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public MeterReadingResponseDto updateReading(
            @PathVariable Long readingId,
            @RequestBody @Valid UpdateMeterReadingRequestDto request) {
        return meterService.updateReadings(readingId, request);
    }
}
