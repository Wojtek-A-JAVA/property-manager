package property.manager.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import property.manager.dto.property.CreatePropertyRequestDto;
import property.manager.dto.property.PropertyResponseDto;
import property.manager.exception.EntityAlreadyExistsException;
import property.manager.mapper.PropertyMapper;
import property.manager.model.Property;
import property.manager.repository.PropertyRepository;
import property.manager.service.PropertyService;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;

    @Override
    public PropertyResponseDto createProperty(CreatePropertyRequestDto request) {
        Optional<Property> existingProperty =
                propertyRepository.findByNameAndAddress(request.name(), request.address());
        if (existingProperty.isPresent()) {
            Property property = existingProperty.get();
            if (!property.isActive()) {
                property.setActive(true);
                Property savedProperty = propertyRepository.save(property);
                return propertyMapper.toDto(savedProperty);
            }
            throw new EntityAlreadyExistsException("Property with name: " + request.name()
                    + " and address " + request.address() + " already exists.");
        }
        Property property = propertyMapper.toEntity(request);
        Property savedProperty = propertyRepository.save(property);
        return propertyMapper.toDto(savedProperty);
    }

    @Override
    public PropertyResponseDto getProperty(Long id) {
        Property property = propertyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Property with id " + id
                        + " not found in database")
        );
        return propertyMapper.toDto(property);
    }
}
