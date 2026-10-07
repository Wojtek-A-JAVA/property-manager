package property.manager.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import property.manager.dto.property.CreatePropertyRequestDto;
import property.manager.dto.property.PropertyResponseDto;
import property.manager.exception.EntityAlreadyExistsException;
import property.manager.mapper.PropertyMapper;
import property.manager.model.Property;
import property.manager.model.Unit;
import property.manager.repository.PropertyRepository;
import property.manager.repository.UnitRepository;
import property.manager.service.PropertyService;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;
    private final UnitRepository unitRepository;

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
        PropertyResponseDto responseDto = propertyMapper.toDto(savedProperty);
        responseDto.setUnitIds(List.of());
        return responseDto;
    }

    @Override
    public PropertyResponseDto getProperty(Long id) {
        Property property = findProperty(id);
        PropertyResponseDto responseDto = propertyMapper.toDto(property);
        responseDto.setUnitIds(getUnitIds(id));
        return responseDto;
    }

    @Override
    public List<PropertyResponseDto> getProperties() {
        List<Property> properties = propertyRepository.findAll();
        List<PropertyResponseDto> propertyResponseDtoList = propertyMapper.toDtoList(properties);
        for (PropertyResponseDto response : propertyResponseDtoList) {
            response.setUnitIds(getUnitIds(response.getId()));
        }
        return propertyResponseDtoList;
    }

    @Override
    public PropertyResponseDto toggleActiveStatus(Long id) {
        Property property = findProperty(id);
        property.setActive(!property.isActive());
        Property savedProperty = propertyRepository.save(property);
        return propertyMapper.toDto(savedProperty);
    }

    private Property findProperty(Long id) {
        return propertyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Property with id " + id
                        + " not found in database"));
    }

    private List<Long> getUnitIds(Long propertyId) {
        List<Unit> units = unitRepository.findAllByPropertyId(propertyId);
        return units.stream().map(Unit::getId).toList();
    }
}
