package property.manager.service;

import java.util.List;
import property.manager.dto.property.CreatePropertyRequestDto;
import property.manager.dto.property.PropertyResponseDto;

public interface PropertyService {

    PropertyResponseDto createProperty(CreatePropertyRequestDto request);

    PropertyResponseDto getProperty(Long id);

    List<PropertyResponseDto> getProperties();

    PropertyResponseDto toggleActiveStatus(Long id);
}
