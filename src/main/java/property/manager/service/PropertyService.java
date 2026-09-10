package property.manager.service;

import property.manager.dto.property.CreatePropertyRequestDto;
import property.manager.dto.property.PropertyResponseDto;

public interface PropertyService {

    PropertyResponseDto createProperty(CreatePropertyRequestDto request);

    PropertyResponseDto getProperty(Long id);
}
