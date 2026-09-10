package property.manager.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import property.manager.dto.error.ErrorResponseDto;
import tools.jackson.databind.exc.InvalidFormatException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
//            HttpServletRequest request, MethodArgumentNotValidException ex) {
//        String message = ex.getBindingResult()
//                .getAllErrors()
//                .getFirst()
//                .getDefaultMessage();
//        return buildErrorResponse(request, HttpStatus.BAD_REQUEST, message
//        );
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
            HttpServletRequest request, MethodArgumentNotValidException ex) {
        var fieldError = ex.getBindingResult()
                .getFieldErrors()
                .getFirst();
        String message = fieldError.getField() + ": " + fieldError.getDefaultMessage();
        return buildErrorResponse(request, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(
            HttpServletRequest request, EntityNotFoundException ex) {
        return buildErrorResponse(request, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityAlreadyExistsException(
            HttpServletRequest request, EntityAlreadyExistsException ex) {
        return buildErrorResponse(request, HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidTenantDataException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidTenantDataException(
            HttpServletRequest request, InvalidTenantDataException ex) {
        return buildErrorResponse(request, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(
            HttpServletRequest request, HttpMessageNotReadableException ex) {
        String message = "Invalid request value.";
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException
                && invalidFormatException.getTargetType().isEnum()) {
            Object[] enumConstants = invalidFormatException
                    .getTargetType()
                    .getEnumConstants();
            message = "Invalid value. Allowed values: " + Arrays.toString(enumConstants);
        }
        return buildErrorResponse(request, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllExceptions(
            HttpServletRequest request, Exception ex) {
        log.error("Unhandled exception", ex);
        String message = "An unexpected error occurred";
        return buildErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(
            HttpServletRequest request, HttpStatus status, String message) {
        ErrorResponseDto body = new ErrorResponseDto(
                OffsetDateTime.now(), status.value(), status.getReasonPhrase(),
                request.getRequestURI(), message);
        return new ResponseEntity<>(body, status);
    }
}
