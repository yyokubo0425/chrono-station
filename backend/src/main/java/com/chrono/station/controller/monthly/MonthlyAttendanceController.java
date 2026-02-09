package com.chrono.station.controller.monthly;

import com.chrono.station.application.auth.CurrentUserProvider;
import com.chrono.station.application.monthly.MonthlyAttendanceUseCase;
import com.chrono.station.dto.monthly.MonthlyAttendanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/monthly-attendance")
public class MonthlyAttendanceController {

    private final MonthlyAttendanceUseCase monthlyAttendanceUseCase;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public MonthlyAttendanceDto getMonthly(
            @RequestParam int year,
            @RequestParam int month
    ) {
        Long employeeId = currentUserProvider.getEmployeeId();
        YearMonth yearMonth = YearMonth.of(year, month);

        return monthlyAttendanceUseCase.getMonthly(employeeId, yearMonth);
    }
}
