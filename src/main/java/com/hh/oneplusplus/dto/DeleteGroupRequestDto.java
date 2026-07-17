package com.hh.oneplusplus.dto;

import java.util.List;
import java.util.UUID;

public record DeleteGroupRequestDto(
        List<UUID> ids
) {
}
