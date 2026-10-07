package property.manager.controler;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import property.manager.dto.utility.invoice.CreateUtilityInvoiceRequestDto;
import property.manager.dto.utility.invoice.UpdateUtilityInvoiceRequestDto;
import property.manager.dto.utility.invoice.UtilityInvoiceFilterDto;
import property.manager.dto.utility.invoice.UtilityInvoiceResponseDto;
import property.manager.service.UtilityInvoiceService;

@RestController
@RequestMapping("/api/utility-invoices")
@RequiredArgsConstructor
public class UtilityInvoiceController {

    private final UtilityInvoiceService utilityInvoiceService;

    @PostMapping
    public UtilityInvoiceResponseDto createUtilityInvoice(
            @RequestBody @Valid CreateUtilityInvoiceRequestDto request) {
        return utilityInvoiceService.createUtilityInvoice(request);
    }

    @GetMapping("/{id}")
    public UtilityInvoiceResponseDto getUtilityInvoice(@PathVariable Long id) {
        return utilityInvoiceService.getUtilityInvoice(id);
    }

    @GetMapping
    public List<UtilityInvoiceResponseDto> getUtilityInvoices(
            @ModelAttribute UtilityInvoiceFilterDto filter) {
        return utilityInvoiceService.getUtilityInvoices(filter);
    }

    @PatchMapping("/{id}")
    public UtilityInvoiceResponseDto updateUtilityInvoice(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUtilityInvoiceRequestDto request) {
        return utilityInvoiceService.updateUtilityInvoice(id, request);
    }
}
