package co.com.evelyn.onboardingreactivo.model.exceptions;

import co.com.evelyn.onboardingreactivo.model.enums.TechnicalMessage;

public class BusinessException extends ProcessorException {
    public BusinessException(TechnicalMessage technicalMessage) {
        super(technicalMessage);
    }
}
