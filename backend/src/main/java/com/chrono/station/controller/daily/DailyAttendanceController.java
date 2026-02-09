package com.chrono.station.controller.daily;

import com.chrono.station.application.daily.DailyAttendanceUseCase;
import com.chrono.station.application.auth.CurrentUserProvider;
import com.chrono.station.dto.daily.DailyAttendanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/daily-attendance")
public class DailyAttendanceController {

    private final DailyAttendanceUseCase dailyAttendanceUseCase;
    private final CurrentUserProvider currentUserProvider;

    //日次勤怠取得
    @GetMapping
    public DailyAttendanceDto getDaily(@RequestParam LocalDate date) {
        Long employeeId = currentUserProvider.getEmployeeId();
        return dailyAttendanceUseCase.getDaily(employeeId, date);
    }
}
