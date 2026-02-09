package com.chrono.station.application.attendance;

import com.chrono.station.domain.attendance.Attendance;
import com.chrono.station.domain.attendance.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceUseCase {

    private final AttendanceService attendanceService;

    //出勤打刻
    @Transactional
    public Attendance clockIn(Long employeeId) {
        return attendanceService.clockIn(employeeId);
    }

    //退勤打刻
    @Transactional
    public Attendance clockOut(Long employeeId) {
        return attendanceService.clockOut(employeeId);
    }

    //休憩打刻
    @Transactional
    public Attendance breakToggle(Long employeeId) {
        return attendanceService.breakToggle(employeeId);
    }

    //今日の勤怠を取得
    @Transactional(readOnly = true)
    public Attendance getTodayAttendance(Long employeeId) {
        return attendanceService.getTodayAttendance(employeeId);
    }
}
