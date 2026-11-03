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
import property.manager.dto.cost.property.CreatePropertyCostRequestDto;
import property.manager.dto.cost.property.PropertyCostFilterDto;
import property.manager.dto.cost.property.PropertyCostResponseDto;
import property.manager.dto.cost.property.UpdatePropertyCostRequestDto;
import property.manager.service.PropertyCostService;

@Tag(name = "10. Property Costs")
@RestController
@RequestMapping("/api/property-costs")
@RequiredArgsConstructor
public class PropertyCostController {

    private final PropertyCostService propertyCostService;

    @Operation(summary = "Create property costs",
            description = "Creates property costs based on the provided request")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public List<PropertyCostResponseDto> createPropertyCost(
            @RequestBody @Valid CreatePropertyCostRequestDto request) {
        return propertyCostService.createPropertyCost(request);
    }

    @Operation(summary = "Get property cost",
            description = "Returns a property cost by its id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public PropertyCostResponseDto getPropertyCost(@PathVariable Long id) {
        return propertyCostService.getPropertyCost(id);
    }

    @Operation(summary = "Get property costs",
            description = "Returns property costs matching the provided filters")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public List<PropertyCostResponseDto> getPropertyCosts(
            @ModelAttribute PropertyCostFilterDto filter) {
        return propertyCostService.getPropertyCosts(filter);
    }

    @Operation(summary = "Update property cost", description =
            "Updates an existing property cost. This operation is restricted to ADMIN users.")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PropertyCostResponseDto> updatePropertyCost(
            @PathVariable Long id, @RequestBody UpdatePropertyCostRequestDto request) {
        return propertyCostService.updatePropertyCost(id, request);
    }
}
