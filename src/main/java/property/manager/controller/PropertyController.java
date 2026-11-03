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
import property.manager.dto.property.CreatePropertyRequestDto;
import property.manager.dto.property.PropertyResponseDto;
import property.manager.service.PropertyService;

@Tag(name = "02. Properties")
@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @Operation(summary = "Create property",
            description = "Creates a new property")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public PropertyResponseDto createProperty(@RequestBody @Valid
                                              CreatePropertyRequestDto request) {
        return propertyService.createProperty(request);
    }

    @Operation(summary = "Get property",
            description = "Returns a property by its id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public PropertyResponseDto getProperty(@PathVariable Long id) {
        return propertyService.getProperty(id);
    }

    @Operation(summary = "Get properties",
            description = "Returns all properties")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEMO_ADMIN')")
    public List<PropertyResponseDto> getProperties() {
        return propertyService.getProperties();
    }

    @Operation(summary = "Change property active status", description =
            "Activates or deactivates an existing property."
                    + " This operation is restricted to ADMIN users.")
    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public PropertyResponseDto togglePropertyActiveStatus(@PathVariable Long id) {
        return propertyService.toggleActiveStatus(id);
    }
}
