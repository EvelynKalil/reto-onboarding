package co.com.evelyn.onboardingreactivo.model.exceptions;

import co.com.evelyn.onboardingreactivo.model.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class ProcessorException extends RuntimeException {
    private final TechnicalMessage technicalMessage;

    public ProcessorException(TechnicalMessage technicalMessage) {
        super(technicalMessage.getMessage());
        this.technicalMessage = technicalMessage;
    }

    public ProcessorException(Throwable cause, TechnicalMessage technicalMessage) {
        super(technicalMessage.getMessage(), cause);
        this.technicalMessage = technicalMessage;
    }
}
