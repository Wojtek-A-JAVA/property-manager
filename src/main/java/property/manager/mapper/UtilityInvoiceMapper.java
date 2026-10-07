package property.manager.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import property.manager.config.MapperConfig;
import property.manager.dto.utility.invoice.CreateUtilityInvoiceRequestDto;
import property.manager.dto.utility.invoice.UtilityInvoiceResponseDto;
import property.manager.model.UtilityInvoice;

@Mapper(config = MapperConfig.class)
public interface UtilityInvoiceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "property", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    UtilityInvoice toEntity(CreateUtilityInvoiceRequestDto request);

    @Mapping(target = "propertyId", source = "property.id")
    @Mapping(target = "supplierId", source = "supplier.id")
    UtilityInvoiceResponseDto toDto(UtilityInvoice utilityInvoice);

    List<UtilityInvoiceResponseDto> toDto(List<UtilityInvoice> utilityInvoices);
}
