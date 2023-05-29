package hanghae.fleamarket.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class RestApiExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public String notMathUserId(NoHandlerFoundException ex) {

        return "good";
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleApiRequestException(IllegalArgumentException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(restApiException, HttpStatus.BAD_REQUEST);
    }
}