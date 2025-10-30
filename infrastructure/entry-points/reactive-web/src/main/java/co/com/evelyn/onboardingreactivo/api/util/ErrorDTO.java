package co.com.evelyn.onboardingreactivo.api.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ErrorDTO {
    private int status;
    private String message;
    private String param;

    public static ErrorDTO from(HttpStatus status, String message, String param) {
        return new ErrorDTO(status.value(), message, param);
    }
}
