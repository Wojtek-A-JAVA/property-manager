package property.manager.service.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import property.manager.dto.unit.CreateUnitRequestDto;
import property.manager.dto.unit.UnitResponseDto;
import property.manager.exception.EntityAlreadyExistsException;
import property.manager.exception.EntityNotFoundException;
import property.manager.mapper.UnitMapper;
import property.manager.model.Property;
import property.manager.model.Unit;
import property.manager.repository.PropertyRepository;
import property.manager.repository.UnitRepository;
import property.manager.service.UnitService;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final PropertyRepository propertyRepository;
    private final UnitMapper unitMapper;

    @Override
    public UnitResponseDto createUnit(CreateUnitRequestDto request) {
        Optional<Unit> existingUnit = unitRepository.findByPropertyIdAndUnitNumber(
                request.propertyId(), request.unitNumber());

        if (existingUnit.isPresent()) {
            Unit unit = existingUnit.get();
            if (!unit.isActive()) {
                unit.setActive(true);
                Unit savedUnit = unitRepository.save(unit);
                return unitMapper.toDto(savedUnit);
            }
            throw new EntityAlreadyExistsException("Unit with number " + request.unitNumber()
                    + " already exists in property with id " + request.propertyId());
        }

        Property property = propertyRepository.findById(request.propertyId()).orElseThrow(
                () -> new EntityNotFoundException("Property with id " + request.propertyId()
                        + " not found in database."));

        Unit unit = unitMapper.toEntity(request);
        unit.setProperty(property);
        Unit savedUnit = unitRepository.save(unit);
        return unitMapper.toDto(savedUnit);
    }

    @Override
    public UnitResponseDto getUnit(Long id) {
        Unit unit = unitRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Unit with id " + id + " not found in database")
        );
        return unitMapper.toDto(unit);
    }
}
