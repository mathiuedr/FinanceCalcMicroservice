package org.matthew.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Builder;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String code;
    private final String message;
    private final String path;
    private final Map<String, Object> details;

    public static ApiErrorResponse of(int status, String error, String code,
                                      String message, String path) {
        return ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status)
                .error(error)
                .code(code)
                .message(message)
                .path(path)
                .details(new HashMap<>())
                .build();
    }

    public ApiErrorResponse withDetail(String key, Object value) {
        this.details.put(key, value);
        return this;
    }
}