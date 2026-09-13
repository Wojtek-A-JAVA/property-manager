package property.manager.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import property.manager.controler.MeterController;
import property.manager.dto.meter.CreateMeterReadingRequestDto;
import property.manager.dto.meter.CreateMeterRequestDto;
import property.manager.dto.meter.MeterFilterDto;
import property.manager.dto.meter.MeterReadingFilterDto;
import property.manager.dto.meter.MeterReadingResponseDto;
import property.manager.dto.meter.MeterResponseDto;
import property.manager.exception.EntityAlreadyExistsException;
import property.manager.exception.EntityNotFoundException;
import property.manager.exception.GlobalExceptionHandler;
import property.manager.exception.InactiveEntityException;
import property.manager.exception.InvalidMeterDataException;
import property.manager.model.MeasurementType;
import property.manager.model.MeterPurpose;
import property.manager.service.MeterService;

@WebMvcTest(MeterController.class)
@Import(GlobalExceptionHandler.class)
class MeterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MeterService meterService;

    @Test
    void createShouldReturnCreatedMeter() throws Exception {
        MeterResponseDto response = createMeterResponse(true);

        when(meterService.create(any(CreateMeterRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/meters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "propertyId": 1,
                                  "unitId": 10,
                                  "measurementType": "WATER",
                                  "purpose": "COLD_WATER",
                                  "name": "Water meter",
                                  "notes": "Test meter"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.propertyId").value(1))
                .andExpect(jsonPath("$.unitId").value(10))
                .andExpect(jsonPath("$.measurementType").value("WATER"))
                .andExpect(jsonPath("$.purpose").value("COLD_WATER"))
                .andExpect(jsonPath("$.name").value("Water meter"))
                .andExpect(jsonPath("$.active").value(true));

        ArgumentCaptor<CreateMeterRequestDto> captor =
                ArgumentCaptor.forClass(CreateMeterRequestDto.class);

        verify(meterService).create(captor.capture());

        CreateMeterRequestDto request = captor.getValue();

        assertThat(request.propertyId()).isEqualTo(1L);
        assertThat(request.unitId()).isEqualTo(10L);
        assertThat(request.measurementType())
                .isEqualTo(MeasurementType.WATER);
        assertThat(request.purpose())
                .isEqualTo(MeterPurpose.COLD_WATER);
        assertThat(request.name()).isEqualTo("Water meter");
    }

    @Test
    void createShouldReturnBadRequestWhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/meters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "propertyId": 1,
                                  "measurementType": "WATER",
                                  "purpose": "COLD_WATER",
                                  "name": " "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(
                        "name: must not be blank"));

        verify(meterService, never())
                .create(any(CreateMeterRequestDto.class));
    }

    @Test
    void createShouldReturnConflictWhenMeterAlreadyExists()
            throws Exception {

        when(meterService.create(any(CreateMeterRequestDto.class)))
                .thenThrow(new EntityAlreadyExistsException(
                        "Meter already exists"));

        mockMvc.perform(post("/api/meters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "propertyId": 1,
                                  "measurementType": "WATER",
                                  "purpose": "COLD_WATER",
                                  "name": "Water meter"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Meter already exists"));
    }

    @Test
    void getMeterShouldReturnMeter() throws Exception {
        when(meterService.getMeterById(100L))
                .thenReturn(createMeterResponse(true));

        mockMvc.perform(get("/api/meters/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.propertyId").value(1))
                .andExpect(jsonPath("$.unitId").value(10))
                .andExpect(jsonPath("$.name").value("Water meter"))
                .andExpect(jsonPath("$.active").value(true));

        verify(meterService).getMeterById(100L);
    }

    @Test
    void getMeterShouldReturnNotFoundWhenMeterDoesNotExist()
            throws Exception {

        when(meterService.getMeterById(100L))
                .thenThrow(new EntityNotFoundException(
                        "Meter with id 100 doesn't exist in database"));

        mockMvc.perform(get("/api/meters/100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(
                        "Meter with id 100 doesn't exist in database"));
    }

    @Test
    void getMetersShouldBindFilterAndReturnMeters()
            throws Exception {

        when(meterService.getMeters(any(MeterFilterDto.class)))
                .thenReturn(List.of(createMeterResponse(true)));

        mockMvc.perform(get("/api/meters")
                        .param("propertyId", "1")
                        .param("unitId", "10")
                        .param("measurementType", "WATER")
                        .param("purpose", "COLD_WATER")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].measurementType")
                        .value("WATER"));

        ArgumentCaptor<MeterFilterDto> captor =
                ArgumentCaptor.forClass(MeterFilterDto.class);

        verify(meterService).getMeters(captor.capture());

        MeterFilterDto filter = captor.getValue();

        assertThat(filter.propertyId()).isEqualTo(1L);
        assertThat(filter.unitId()).isEqualTo(10L);
        assertThat(filter.measurementType())
                .isEqualTo(MeasurementType.WATER);
        assertThat(filter.purpose())
                .isEqualTo(MeterPurpose.COLD_WATER);
        assertThat(filter.active()).isTrue();
    }

    @Test
    void toggleMeterActiveStatusShouldReturnUpdatedMeter()
            throws Exception {

        when(meterService.toggleMeterActiveStatus(100L))
                .thenReturn(createMeterResponse(false));

        mockMvc.perform(patch("/api/meters/100/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.active").value(false));

        verify(meterService).toggleMeterActiveStatus(100L);
    }

    @Test
    void createReadingShouldReturnCreatedReading()
            throws Exception {

        LocalDate readingDate = LocalDate.of(2026, 3, 1);

        MeterReadingResponseDto response =
                new MeterReadingResponseDto(
                        1L,
                        100L,
                        readingDate,
                        new BigDecimal("25.321"),
                        "Monthly reading");

        when(meterService.createReading(
                any(Long.class),
                any(CreateMeterReadingRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/meters/100/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "readingDate": "2026-03-01",
                                  "value": 25.321,
                                  "notes": "Monthly reading"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.meterId").value(100))
                .andExpect(jsonPath("$.readingDate")
                        .value("2026-03-01"))
                .andExpect(jsonPath("$.value").value(25.321))
                .andExpect(jsonPath("$.notes")
                        .value("Monthly reading"));

        ArgumentCaptor<CreateMeterReadingRequestDto> captor =
                ArgumentCaptor.forClass(
                        CreateMeterReadingRequestDto.class);

        verify(meterService)
                .createReading(org.mockito.ArgumentMatchers.eq(100L),
                        captor.capture());

        assertThat(captor.getValue().readingDate())
                .isEqualTo(readingDate);
        assertThat(captor.getValue().value())
                .isEqualByComparingTo("25.321");
    }

    @Test
    void createReadingShouldReturnBadRequestWhenValueIsNegative()
            throws Exception {

        mockMvc.perform(post("/api/meters/100/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "readingDate": "2026-03-01",
                                  "value": -1.000
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(meterService, never())
                .createReading(
                        any(Long.class),
                        any(CreateMeterReadingRequestDto.class));
    }

    @Test
    void createReadingShouldReturnBadRequestWhenDateIsNull()
            throws Exception {

        mockMvc.perform(post("/api/meters/100/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "value": 25.321
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(meterService, never())
                .createReading(
                        any(Long.class),
                        any(CreateMeterReadingRequestDto.class));
    }

    @Test
    void createReadingShouldReturnConflictWhenMeterIsInactive()
            throws Exception {

        when(meterService.createReading(
                any(Long.class),
                any(CreateMeterReadingRequestDto.class)))
                .thenThrow(new InactiveEntityException(
                        "Meter with id 100 is inactive"));

        mockMvc.perform(post("/api/meters/100/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "readingDate": "2026-03-01",
                                  "value": 25.321
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Meter with id 100 is inactive"));
    }

    @Test
    void getReadingShouldReturnReading() throws Exception {
        MeterReadingResponseDto response =
                createReadingResponse();

        when(meterService.getReadingById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/meters/readings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.meterId").value(100))
                .andExpect(jsonPath("$.readingDate")
                        .value("2026-03-01"))
                .andExpect(jsonPath("$.value").value(25.321));

        verify(meterService).getReadingById(1L);
    }

    @Test
    void getReadingShouldReturnNotFoundWhenReadingDoesNotExist()
            throws Exception {

        when(meterService.getReadingById(1L))
                .thenThrow(new EntityNotFoundException(
                        "Meter reading with id 1 doesn't exist in database"));

        mockMvc.perform(get("/api/meters/readings/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(
                        "Meter reading with id 1 doesn't exist in database"));
    }

    @Test
    void getReadingsShouldBindFilterAndReturnReadings()
            throws Exception {

        when(meterService.getReadings(
                any(MeterReadingFilterDto.class)))
                .thenReturn(List.of(createReadingResponse()));

        mockMvc.perform(get("/api/meters/readings")
                        .param("meterId", "100")
                        .param("fromDate", "2026-01-01")
                        .param("toDate", "2026-03-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].meterId").value(100));

        ArgumentCaptor<MeterReadingFilterDto> captor =
                ArgumentCaptor.forClass(
                        MeterReadingFilterDto.class);

        verify(meterService).getReadings(captor.capture());

        MeterReadingFilterDto filter = captor.getValue();

        assertThat(filter.meterId()).isEqualTo(100L);
        assertThat(filter.fromDate())
                .isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(filter.toDate())
                .isEqualTo(LocalDate.of(2026, 3, 31));
    }

    @Test
    void getReadingsShouldReturnBadRequestForInvalidDateRange()
            throws Exception {

        when(meterService.getReadings(
                any(MeterReadingFilterDto.class)))
                .thenThrow(new InvalidMeterDataException(
                        "Meter reading end date 2026-02-01"
                                + " is before start date 2026-03-01"));

        mockMvc.perform(get("/api/meters/readings")
                        .param("meterId", "100")
                        .param("fromDate", "2026-03-01")
                        .param("toDate", "2026-02-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(
                        "Meter reading end date 2026-02-01"
                                + " is before start date 2026-03-01"));
    }

    @Test
    void createShouldReturnBadRequestForInvalidEnum()
            throws Exception {

        mockMvc.perform(post("/api/meters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "propertyId": 1,
                                  "measurementType": "INVALID",
                                  "purpose": "COLD_WATER",
                                  "name": "Water meter"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString(
                                "Allowed values")));

        verify(meterService, never())
                .create(any(CreateMeterRequestDto.class));
    }

    private MeterResponseDto createMeterResponse(boolean active) {
        return new MeterResponseDto(
                100L,
                1L,
                10L,
                MeasurementType.WATER,
                MeterPurpose.COLD_WATER,
                "Water meter",
                "Test meter",
                active);
    }

    private MeterReadingResponseDto createReadingResponse() {
        return new MeterReadingResponseDto(
                1L,
                100L,
                LocalDate.of(2026, 3, 1),
                new BigDecimal("25.321"),
                "Monthly reading");
    }
}
