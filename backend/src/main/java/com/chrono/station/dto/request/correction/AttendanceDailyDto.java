package com.chrono.station.dto.request.correction;

import com.chrono.station.domain.attendance.Attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;

//打刻修正申請で元の勤怠を取得する用
public record AttendanceDailyDto(
        Long attendanceId,
        LocalDate workDate,
        LocalDateTime workStart,
        LocalDateTime workEnd,
        Integer breakMinutes
) {
    public static AttendanceDailyDto from(Attendance a){
        return new AttendanceDailyDto(
                a.getAttendanceId(),
                a.getWorkDate(),
                a.getWorkStart(),
                a.getWorkEnd(),
                a.getBreakMinutes()
        );
    }
}
