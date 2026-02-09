package com.chrono.station.dto.attendance;

import com.chrono.station.domain.attendance.Attendance;
import com.chrono.station.domain.employee.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AttendanceDto {

    private Long attendanceId;
    private Long employeeId;
    private LocalDate workDate;
    private String workingStatus;
    private String workingType;
    private LocalDateTime workStart;
    private LocalDateTime workEnd;
    private LocalDateTime breakStart;
    private LocalDateTime breakEnd;
    private Integer breakMinutes;
    private Integer totalWorkMinutes;

    //Entity　→ DTOに変換
    public static AttendanceDto from(Attendance attendance) {
        return new AttendanceDto(
                attendance.getAttendanceId(),
                attendance.getEmployee().getEmployeeId(),
                attendance.getWorkDate(),
                attendance.getWorkingStatus().name(),
                attendance.getWorkType().name(),
                attendance.getWorkStart(),
                attendance.getWorkEnd(),
                attendance.getBreakStart(),
                attendance.getBreakEnd(),
                attendance.getBreakMinutes(),
                attendance.getTotalWorkMinutes()
        );
    }
}
