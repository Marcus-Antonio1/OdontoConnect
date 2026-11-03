
package com.odontoconnect.user.dto;

import java.time.OffsetDateTime;
import java.util.Set;

public record UserResponse(
        Long id,
        String name,
        String email,
        String registrationNumber,
        boolean active,
        Set<String> roles,
        OffsetDateTime createdAt
) {
}
