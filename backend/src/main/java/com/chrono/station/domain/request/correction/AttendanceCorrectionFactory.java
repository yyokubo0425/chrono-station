package com.chrono.station.domain.request.correction;

import com.chrono.station.domain.attendance.Attendance;

import java.time.LocalDateTime;

public class AttendanceCorrectionFactory {

    public static AttendanceCorrection create(
            Attendance attendance,
            LocalDateTime afterWorkStart,
            LocalDateTime afterWorkEnd,
            Integer afterBreakMinutes,
            String reason
    ) {
        return AttendanceCorrection.builder()
                .employeeId(attendance.getEmployee().getEmployeeId())
                .attendance(attendance)
                .workDate(attendance.getWorkDate())
                //修正前
                .beforeWorkStart(attendance.getWorkStart())
                .beforeWorkEnd(attendance.getWorkEnd())
                .beforeBreakMinutes(attendance.getBreakMinutes())
                //修正後
                .afterWorkStart(afterWorkStart)
                .afterWorkEnd(afterWorkEnd)
                .afterBreakMinutes(afterBreakMinutes)
                .reason(reason)
                .type(CorrectionType.TIME_RECORD)
                .status(CorrectionStatus.REQUESTED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
