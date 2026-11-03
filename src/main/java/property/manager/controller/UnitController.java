package property.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import property.manager.dto.unit.CreateUnitRequestDto;
import property.manager.dto.unit.UnitResponseDto;
import property.manager.service.UnitService;

@Tag(name = "03. Units")
@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @Operation(summary = "Create unit", description =
            "Creates a new unit. This operation is restricted to ADMIN users.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UnitResponseDto createUnit(@RequestBody @Valid CreateUnitRequestDto request) {
        return unitService.createUnit(request);
    }

    @Operation(summary = "Get unit",
            description = "Returns a unit by its id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public UnitResponseDto getUnit(@PathVariable Long id) {
        return unitService.getUnit(id);
    }

    @Operation(summary = "Get units",
            description = "Returns all units")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public List<UnitResponseDto> getUnits() {
        return unitService.getUnits();
    }

    @Operation(summary = "Change unit active status",
            description = "Activates or deactivates an existing unit."
                    + " This operation is restricted to ADMIN users.")
    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public UnitResponseDto toggleUnitActiveStatus(@PathVariable Long id) {
        return unitService.toggleActiveStatus(id);
    }
}
