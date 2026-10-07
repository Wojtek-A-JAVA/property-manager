package property.manager.service;

import java.util.List;
import property.manager.dto.utility.invoice.CreateUtilityInvoiceRequestDto;
import property.manager.dto.utility.invoice.UpdateUtilityInvoiceRequestDto;
import property.manager.dto.utility.invoice.UtilityInvoiceFilterDto;
import property.manager.dto.utility.invoice.UtilityInvoiceResponseDto;
import property.manager.model.UtilityInvoice;

public interface UtilityInvoiceService {
    UtilityInvoiceResponseDto createUtilityInvoice(CreateUtilityInvoiceRequestDto request);

    UtilityInvoiceResponseDto getUtilityInvoice(Long id);

    List<UtilityInvoiceResponseDto> getUtilityInvoices(UtilityInvoiceFilterDto filter);

    UtilityInvoiceResponseDto updateUtilityInvoice(Long id, UpdateUtilityInvoiceRequestDto request);

    UtilityInvoice getUtilityInvoiceEntity(Long id);
}
