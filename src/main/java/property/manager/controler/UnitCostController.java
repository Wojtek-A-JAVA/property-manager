package property.manager.controler;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/unit-costs")
@RequiredArgsConstructor
public class UnitCostController {

    private final UnitCostService unitCostService;

    @PostMapping
    public UnitCostResponseDto createUnitCost(
            @RequestBody @Valid CreateUnitCostRequestDto request) {
        return unitCostService.createUnitCost(request);
    }

    @PostMapping("/multiple")
    public List<UnitCostResponseDto> createUnitCosts(
            @RequestBody @Valid CreateUnitCostsRequestDto request) {
        return unitCostService.createUnitCosts(request);
    }

    @GetMapping("/{id}")
    public UnitCostResponseDto getUnitCost(@PathVariable Long id) {
        return unitCostService.getUnitCost(id);
    }

    @GetMapping
    public List<UnitCostResponseDto> getUnitCosts(
            @ModelAttribute UnitCostFilterRequestDto filter) {
        return unitCostService.getUnitCosts(filter);
    }

    @PutMapping("/{id}")
    public UnitCostResponseDto updateUnitCost(
            @PathVariable Long id, @RequestBody @Valid UpdateUnitCostRequestDto request) {
        return unitCostService.updateUnitCost(id, request);
    }

    @PatchMapping("/{id}/notes")
    public UnitCostResponseDto updateUnitCostNotes(
            @PathVariable Long id, @RequestBody @Valid UpdateUnitCostNotesRequestDto request) {
        return unitCostService.updateUnitCostNotes(id, request);
    }
}
