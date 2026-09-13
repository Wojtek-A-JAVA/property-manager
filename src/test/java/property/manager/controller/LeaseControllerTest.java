package property.manager.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import property.manager.controler.LeaseController;
import property.manager.dto.lease.CreateLeaseRequestDto;
import property.manager.dto.lease.LeaseFilterDto;
import property.manager.dto.lease.LeaseResponseDto;
import property.manager.dto.lease.LeaseStatusRequestDto;
import property.manager.exception.EntityAlreadyExistsException;
import property.manager.exception.EntityNotFoundException;
import property.manager.exception.GlobalExceptionHandler;
import property.manager.exception.InactiveEntityException;
import property.manager.exception.InvalidLeaseDataException;
import property.manager.model.LeaseStatus;
import property.manager.model.VatRate;
import property.manager.service.LeaseService;

@WebMvcTest(LeaseController.class)
@Import(GlobalExceptionHandler.class)
class LeaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeaseService leaseService;

    @Test
    void createLeaseShouldReturnCreatedLease() throws Exception {
        LeaseResponseDto response = createLeaseResponse(LeaseStatus.ACTIVE);

        when(leaseService.createLease(any(CreateLeaseRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/leases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLeaseJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tenantId").value(10))
                .andExpect(jsonPath("$.unitId").value(20))
                .andExpect(jsonPath("$.startDate").value("2026-01-01"))
                .andExpect(jsonPath("$.endDate").value("2026-12-31"))
                .andExpect(jsonPath("$.rentNetAmount").value(2500.00))
                .andExpect(jsonPath("$.vatRate").value("VAT_23"))
                .andExpect(jsonPath("$.paymentDueDay").value(10))
                .andExpect(jsonPath("$.leaseStatus").value("ACTIVE"));

        ArgumentCaptor<CreateLeaseRequestDto> captor =
                ArgumentCaptor.forClass(CreateLeaseRequestDto.class);

        verify(leaseService).createLease(captor.capture());

        CreateLeaseRequestDto request = captor.getValue();

        assertThat(request.tenantId()).isEqualTo(10L);
        assertThat(request.unitId()).isEqualTo(20L);
        assertThat(request.startDate())
                .isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(request.endDate())
                .isEqualTo(LocalDate.of(2026, 12, 31));
        assertThat(request.rentNetAmount())
                .isEqualByComparingTo("2500.00");
        assertThat(request.vatRate()).isEqualTo(VatRate.VAT_23);
        assertThat(request.paymentDueDay()).isEqualTo(10);
        assertThat(request.leaseStatus())
                .isEqualTo(LeaseStatus.ACTIVE);
    }

    @Test
    void createLeaseShouldReturnBadRequestWhenTenantIdIsNull()
            throws Exception {

        mockMvc.perform(post("/api/leases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "unitId": 20,
                                  "startDate": "2026-01-01",
                                  "endDate": "2026-12-31",
                                  "rentNetAmount": 2500.00,
                                  "vatRate": "VAT_23",
                                  "paymentDueDay": 10,
                                  "leaseStatus": "ACTIVE"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message",
                        containsString("tenantId")));

        verify(leaseService, never())
                .createLease(any(CreateLeaseRequestDto.class));
    }

    @Test
    void createLeaseShouldReturnBadRequestWhenPaymentDueDayIsTooLow()
            throws Exception {

        mockMvc.perform(post("/api/leases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tenantId": 10,
                                  "unitId": 20,
                                  "startDate": "2026-01-01",
                                  "endDate": "2026-12-31",
                                  "rentNetAmount": 2500.00,
                                  "vatRate": "VAT_23",
                                  "paymentDueDay": 0,
                                  "leaseStatus": "ACTIVE"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message",
                        containsString("paymentDueDay")));

        verify(leaseService, never())
                .createLease(any(CreateLeaseRequestDto.class));
    }

    @Test
    void createLeaseShouldReturnBadRequestWhenPaymentDueDayIsTooHigh()
            throws Exception {

        mockMvc.perform(post("/api/leases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tenantId": 10,
                                  "unitId": 20,
                                  "startDate": "2026-01-01",
                                  "endDate": "2026-12-31",
                                  "rentNetAmount": 2500.00,
                                  "vatRate": "VAT_23",
                                  "paymentDueDay": 32,
                                  "leaseStatus": "ACTIVE"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message",
                        containsString("paymentDueDay")));

        verify(leaseService, never())
                .createLease(any(CreateLeaseRequestDto.class));
    }

    @Test
    void createLeaseShouldReturnBadRequestWhenNoticePeriodIsZero()
            throws Exception {

        mockMvc.perform(post("/api/leases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tenantId": 10,
                                  "unitId": 20,
                                  "startDate": "2026-01-01",
                                  "rentNetAmount": 2500.00,
                                  "vatRate": "VAT_23",
                                  "paymentDueDay": 10,
                                  "noticePeriodMonths": 0,
                                  "leaseStatus": "ACTIVE"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message",
                        containsString("noticePeriodMonths")));

        verify(leaseService, never())
                .createLease(any(CreateLeaseRequestDto.class));
    }

    @Test
    void createLeaseShouldReturnBadRequestForInvalidDateRange()
            throws Exception {

        when(leaseService.createLease(any(CreateLeaseRequestDto.class)))
                .thenThrow(new InvalidLeaseDataException(
                        "Lease end date 2026-01-01"
                                + " is before start date 2026-02-01"));

        mockMvc.perform(post("/api/leases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tenantId": 10,
                                  "unitId": 20,
                                  "startDate": "2026-02-01",
                                  "endDate": "2026-01-01",
                                  "rentNetAmount": 2500.00,
                                  "vatRate": "VAT_23",
                                  "paymentDueDay": 10,
                                  "leaseStatus": "ACTIVE"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message",
                        containsString("before start date")));
    }

    @Test
    void createLeaseShouldReturnBadRequestForOpenEndedLeaseWithoutNoticePeriod()
            throws Exception {

        when(leaseService.createLease(any(CreateLeaseRequestDto.class)))
                .thenThrow(new InvalidLeaseDataException(
                        "Open-ended lease requires notice period"));

        mockMvc.perform(post("/api/leases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tenantId": 10,
                                  "unitId": 20,
                                  "startDate": "2026-01-01",
                                  "rentNetAmount": 2500.00,
                                  "vatRate": "VAT_23",
                                  "paymentDueDay": 10,
                                  "leaseStatus": "ACTIVE"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Open-ended lease requires notice period"));
    }

    @Test
    void createLeaseShouldReturnConflictWhenUnitIsAlreadyLeased()
            throws Exception {

        when(leaseService.createLease(any(CreateLeaseRequestDto.class)))
                .thenThrow(new EntityAlreadyExistsException(
                        "Unit with id 20 is already leased till 2026-12-31"));

        mockMvc.perform(post("/api/leases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLeaseJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message",
                        containsString("already leased")));
    }

    @Test
    void createLeaseShouldReturnConflictWhenTenantIsInactive()
            throws Exception {

        when(leaseService.createLease(any(CreateLeaseRequestDto.class)))
                .thenThrow(new InactiveEntityException(
                        "Tenant with id 10 is inactive"));

        mockMvc.perform(post("/api/leases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLeaseJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Tenant with id 10 is inactive"));
    }

    @Test
    void getLeaseShouldReturnLease() throws Exception {
        when(leaseService.getLease(1L))
                .thenReturn(createLeaseResponse(LeaseStatus.ACTIVE));

        mockMvc.perform(get("/api/leases/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tenantId").value(10))
                .andExpect(jsonPath("$.unitId").value(20))
                .andExpect(jsonPath("$.leaseStatus").value("ACTIVE"));

        verify(leaseService).getLease(1L);
    }

    @Test
    void getLeaseShouldReturnNotFoundWhenLeaseDoesNotExist()
            throws Exception {

        when(leaseService.getLease(999L))
                .thenThrow(new EntityNotFoundException(
                        "Lease with id 999 doesn't exist in database"));

        mockMvc.perform(get("/api/leases/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Lease with id 999 doesn't exist in database"));
    }

    @Test
    void getLeasesShouldBindFilterAndReturnLeases()
            throws Exception {

        when(leaseService.getLeases(any(LeaseFilterDto.class)))
                .thenReturn(List.of(
                        createLeaseResponse(LeaseStatus.ACTIVE)));

        mockMvc.perform(get("/api/leases")
                        .param("status", "ACTIVE")
                        .param("tenantId", "10")
                        .param("unitId", "20")
                        .param("vatRate", "VAT_23")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-12-31")
                        .param("openEnded", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tenantId").value(10))
                .andExpect(jsonPath("$[0].unitId").value(20))
                .andExpect(jsonPath("$[0].leaseStatus")
                        .value("ACTIVE"));

        ArgumentCaptor<LeaseFilterDto> captor =
                ArgumentCaptor.forClass(LeaseFilterDto.class);

        verify(leaseService).getLeases(captor.capture());

        LeaseFilterDto filter = captor.getValue();

        assertThat(filter.status()).isEqualTo(LeaseStatus.ACTIVE);
        assertThat(filter.tenantId()).isEqualTo(10L);
        assertThat(filter.unitId()).isEqualTo(20L);
        assertThat(filter.vatRate()).isEqualTo(VatRate.VAT_23);
        assertThat(filter.startDate())
                .isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(filter.endDate())
                .isEqualTo(LocalDate.of(2026, 12, 31));
        assertThat(filter.openEnded()).isFalse();
    }

    @Test
    void getLeasesShouldReturnBadRequestForInvalidDateRange()
            throws Exception {

        when(leaseService.getLeases(any(LeaseFilterDto.class)))
                .thenThrow(new InvalidLeaseDataException(
                        "Lease end date 2026-01-01"
                                + " is before start date 2026-02-01"));

        mockMvc.perform(get("/api/leases")
                        .param("startDate", "2026-02-01")
                        .param("endDate", "2026-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message",
                        containsString("before start date")));
    }

    @Test
    void changeLeaseStatusShouldReturnUpdatedLease()
            throws Exception {

        when(leaseService.changeLeaseStatus(
                any(Long.class),
                any(LeaseStatusRequestDto.class)))
                .thenReturn(createLeaseResponse(LeaseStatus.ENDED));

        mockMvc.perform(patch("/api/leases/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "ENDED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.leaseStatus").value("ENDED"));

        ArgumentCaptor<LeaseStatusRequestDto> captor =
                ArgumentCaptor.forClass(LeaseStatusRequestDto.class);

        verify(leaseService).changeLeaseStatus(
                org.mockito.ArgumentMatchers.eq(1L),
                captor.capture());

        assertThat(captor.getValue().status())
                .isEqualTo(LeaseStatus.ENDED);
    }

    @Test
    void changeLeaseStatusShouldReturnBadRequestForInvalidStatus()
            throws Exception {

        mockMvc.perform(patch("/api/leases/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "INVALID"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message",
                        containsString("Allowed values")));

        verify(leaseService, never())
                .changeLeaseStatus(
                        any(Long.class),
                        any(LeaseStatusRequestDto.class));
    }

    @Test
    void changeLeaseStatusShouldReturnNotFoundWhenLeaseDoesNotExist()
            throws Exception {

        when(leaseService.changeLeaseStatus(
                any(Long.class),
                any(LeaseStatusRequestDto.class)))
                .thenThrow(new EntityNotFoundException(
                        "Lease with id 999 doesn't exist in database"));

        mockMvc.perform(patch("/api/leases/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "ENDED"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Lease with id 999 doesn't exist in database"));
    }

    private String validLeaseJson() {
        return """
                {
                  "tenantId": 10,
                  "unitId": 20,
                  "startDate": "2026-01-01",
                  "endDate": "2026-12-31",
                  "rentNetAmount": 2500.00,
                  "additionalCostsNetAmount": 350.00,
                  "depositAmount": 3000.00,
                  "vatRate": "VAT_23",
                  "paymentDueDay": 10,
                  "noticePeriodMonths": 3,
                  "leaseStatus": "ACTIVE",
                  "notes": "Test lease"
                }
                """;
    }

    private LeaseResponseDto createLeaseResponse(
            LeaseStatus leaseStatus) {

        return new LeaseResponseDto(
                1L,
                10L,
                20L,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                new BigDecimal("2500.00"),
                new BigDecimal("350.00"),
                new BigDecimal("3000.00"),
                VatRate.VAT_23,
                10,
                3,
                leaseStatus,
                "Test lease"
        );
    }

    @Test
    void getLeasesWithoutOpenEndedParameterShouldPassNull()
            throws Exception {

        when(leaseService.getLeases(any(LeaseFilterDto.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/leases"))
                .andExpect(status().isOk());

        ArgumentCaptor<LeaseFilterDto> captor =
                ArgumentCaptor.forClass(LeaseFilterDto.class);

        verify(leaseService).getLeases(captor.capture());

        assertThat(captor.getValue().openEnded()).isNull();
    }
}
