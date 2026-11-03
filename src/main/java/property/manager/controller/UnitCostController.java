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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import property.manager.dto.cost.unit.CreateUnitCostRequestDto;
import property.manager.dto.cost.unit.CreateUnitCostsRequestDto;
import property.manager.dto.cost.unit.UnitCostFilterRequestDto;
import property.manager.dto.cost.unit.UnitCostResponseDto;
import property.manager.dto.cost.unit.UpdateUnitCostNotesRequestDto;
import property.manager.dto.cost.unit.UpdateUnitCostRequestDto;
import property.manager.service.UnitCostService;

@Tag(name = "11. Unit Costs")
@RestController
@RequestMapping("/api/unit-costs")
@RequiredArgsConstructor
public class UnitCostController {

    private final UnitCostService unitCostService;

    @Operation(summary = "Create unit cost",
            description = "Creates a new unit cost")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public UnitCostResponseDto createUnitCost(
            @RequestBody @Valid CreateUnitCostRequestDto request) {
        return unitCostService.createUnitCost(request);
    }

    @Operation(summary = "Create multiple unit costs",
            description = "Creates multiple unit costs in a single request")
    @PostMapping("/multiple")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public List<UnitCostResponseDto> createUnitCosts(
            @RequestBody @Valid CreateUnitCostsRequestDto request) {
        return unitCostService.createUnitCosts(request);
    }

    @Operation(summary = "Get unit cost",
            description = "Returns a unit cost by its id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public UnitCostResponseDto getUnitCost(@PathVariable Long id) {
        return unitCostService.getUnitCost(id);
    }

    @Operation(summary = "Get unit costs",
            description = "Returns unit costs matching the provided filters")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public List<UnitCostResponseDto> getUnitCosts(
            @ModelAttribute UnitCostFilterRequestDto filter) {
        return unitCostService.getUnitCostsByUtilityRuleId(filter);
    }

    @Operation(summary = "Update unit cost", description =
            "Updates an existing unit cost. This operation is restricted to ADMIN users.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UnitCostResponseDto updateUnitCost(
            @PathVariable Long id, @RequestBody @Valid UpdateUnitCostRequestDto request) {
        return unitCostService.updateUnitCost(id, request);
    }

    @Operation(summary = "Update unit cost notes",
            description = "Updates notes for an existing unit cost")
    @PatchMapping("/{id}/notes")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public UnitCostResponseDto updateUnitCostNotes(
            @PathVariable Long id, @RequestBody @Valid UpdateUnitCostNotesRequestDto request) {
        return unitCostService.updateUnitCostNotes(id, request);
    }
}
