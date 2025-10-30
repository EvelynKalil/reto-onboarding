package co.com.evelyn.onboardingreactivo.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TechnicalMessage {

    INTERNAL_ERROR("500", "Internal server error", ""),
    USER_NOT_FOUND("404", "User not found", "userId"),
    USER_ALREADY_EXISTS("409", "User already exists", "userId"),
    INVALID_REQUEST("400", "Invalid request", ""),
    DATABASE_ERROR("500", "Database operation failed", ""),
    NO_MATCHING_USERS_FOUND("404", "No matching users found", "name");

    private final String code;
    private final String message;
    private final String param;
}
