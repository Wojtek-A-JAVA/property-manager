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
import property.manager.dto.cost.property.CreatePropertyCostRequestDto;
import property.manager.dto.cost.property.PropertyCostFilterDto;
import property.manager.dto.cost.property.PropertyCostResponseDto;
import property.manager.dto.cost.property.UpdatePropertyCostRequestDto;
import property.manager.service.PropertyCostService;

@RestController
@RequestMapping("/api/property-costs")
@RequiredArgsConstructor
public class PropertyCostController {

    private final PropertyCostService propertyCostService;

    @PostMapping
    public List<PropertyCostResponseDto> createPropertyCost(
            @RequestBody @Valid CreatePropertyCostRequestDto request) {
        return propertyCostService.createPropertyCost(request);
    }

    @GetMapping("/{id}")
    public PropertyCostResponseDto getPropertyCost(@PathVariable Long id) {
        return propertyCostService.getPropertyCost(id);
    }

    @GetMapping
    public List<PropertyCostResponseDto> getPropertyCosts(
            @ModelAttribute PropertyCostFilterDto filter) {
        return propertyCostService.getPropertyCosts(filter);
    }

    @PatchMapping("/{id}")
    public List<PropertyCostResponseDto> updatePropertyCost(
            @PathVariable Long id, @RequestBody UpdatePropertyCostRequestDto request) {
        return propertyCostService.updatePropertyCost(id, request);
    }
}
