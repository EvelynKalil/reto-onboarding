package co.com.evelyn.onboardingreactivo.model.exceptions;

import co.com.evelyn.onboardingreactivo.model.enums.TechnicalMessage;

public class TechnicalException extends ProcessorException {
    public TechnicalException(TechnicalMessage technicalMessage) {
        super(technicalMessage);
    }
    public TechnicalException(Throwable cause, TechnicalMessage technicalMessage) {
        super(cause, technicalMessage);
    }
}
