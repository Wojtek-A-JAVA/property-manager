package property.manager.controler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import property.manager.dto.unit.CreateUnitRequestDto;
import property.manager.dto.unit.UnitResponseDto;
import property.manager.service.UnitService;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @PostMapping
    public UnitResponseDto createUnit(@RequestBody @Valid CreateUnitRequestDto request) {
        return unitService.createUnit(request);
    }

    @GetMapping("/{id}")
    public UnitResponseDto getUnit(@PathVariable Long id) {
        return unitService.getUnit(id);
    }
}
