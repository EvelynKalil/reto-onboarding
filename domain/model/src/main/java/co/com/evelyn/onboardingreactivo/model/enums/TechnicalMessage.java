package co.com.evelyn.onboardingreactivo.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TechnicalMessage {

    INTERNAL_ERROR("500", "Internal server error", ""),
    USER_NOT_FOUND("404", "User not found", "userId"),
    INVALID_REQUEST("400", "Invalid request", ""),
    DATABASE_ERROR("500", "Database operation failed", ""),
    NO_MATCHING_USERS_FOUND("404", "No matching users found", "name"),

    SQS_MESSAGE_PARSING_ERROR("502", "Error parsing SQS message", "messageBody"),
    DYNAMODB_SAVE_ERROR("500", "Error saving user to DynamoDB", "userId");

    private final String code;
    private final String message;
    private final String param;
}
