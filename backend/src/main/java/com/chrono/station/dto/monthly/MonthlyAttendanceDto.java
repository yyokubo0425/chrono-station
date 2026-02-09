package com.chrono.station.dto.monthly;

import java.time.YearMonth;
import java.util.List;

public record MonthlyAttendanceDto(
        YearMonth yearMonth,  //年月
        List<DailyAttendancesDto> dailyAttendances,
        MonthlySummaryDto summary
) {
}
