package in.srinithamizh.oxygen_cylinder.exception.handler;

import in.srinithamizh.oxygen_cylinder.dto.ErrorResponse;
import in.srinithamizh.oxygen_cylinder.exception.RedundantPasswordException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RestControllerAdvice
public class GlobalExceptionHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception, HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now().format(formatter),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
            UsernameNotFoundException exception,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now().format(formatter),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(RedundantPasswordException.class)
    public ResponseEntity<ErrorResponse> handleRedundantPasswordException(
            RedundantPasswordException exception,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now().format(formatter),
                HttpStatus.CONFLICT.value(),
                "Password is Same as Current",
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
