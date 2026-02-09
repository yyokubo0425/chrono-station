package com.chrono.station.dto.request.correction;

import java.time.LocalTime;

public record CorrectionRequestDto(
        LocalTime requestedWorkStart,
        LocalTime requestedWorkEnd,
        Integer requestedBreakMinutes,
        String reason
) {
}
