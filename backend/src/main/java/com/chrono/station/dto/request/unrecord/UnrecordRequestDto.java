package com.chrono.station.dto.request.unrecord;

import java.time.LocalTime;

public record UnrecordRequestDto(
        LocalTime requestedWorkStart,
        LocalTime requestedWorkEnd,
        Integer requestedBreakMinutes,
        String reason
) {
}
