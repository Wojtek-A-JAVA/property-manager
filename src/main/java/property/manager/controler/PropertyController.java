package property.manager.controler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import property.manager.dto.property.CreatePropertyRequestDto;
import property.manager.dto.property.PropertyResponseDto;
import property.manager.service.PropertyService;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    public PropertyResponseDto createProperty(@RequestBody @Valid
                                                  CreatePropertyRequestDto request) {
        return propertyService.createProperty(request);
    }

    @GetMapping("/{id}")
    public PropertyResponseDto getProperty(@PathVariable Long id) {
        return propertyService.getProperty(id);
    }
}
