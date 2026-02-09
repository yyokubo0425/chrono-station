package com.chrono.station.controller.attendance;

import com.chrono.station.application.auth.CurrentUserProvider;
import com.chrono.station.domain.attendance.Attendance;
import com.chrono.station.dto.attendance.AttendanceDto;
import com.chrono.station.application.attendance.AttendanceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceUseCase attendanceUseCase;
    private final CurrentUserProvider currentUserProvider;

    //出勤打刻
    @PostMapping("/clock-in")
    public AttendanceDto clockIn() {
        Long employeeId = currentUserProvider.getEmployeeId();

        Attendance attendance = attendanceUseCase.clockIn(employeeId);
        return AttendanceDto.from(attendance);
    }

    //退勤打刻
    @PostMapping("/clock-out")
    public AttendanceDto clockOut() {
        Long employeeId = currentUserProvider.getEmployeeId();

        Attendance attendance = attendanceUseCase.clockOut(employeeId);
        return AttendanceDto.from(attendance);
    }

    //休憩打刻
    @PostMapping("/break")
    public AttendanceDto breakToggle() {
        Long employeeId = currentUserProvider.getEmployeeId();

        Attendance attendance = attendanceUseCase.breakToggle(employeeId);
        return AttendanceDto.from(attendance);
    }

    //今日の勤怠を取得
    @GetMapping("/today")
    public AttendanceDto getTodayAttendance() {
        Long employeeId = currentUserProvider.getEmployeeId();

        Attendance attendance = attendanceUseCase.getTodayAttendance(employeeId);
        if (attendance == null) {
            return null;
        }

        return AttendanceDto.from(attendance);
    }
}
