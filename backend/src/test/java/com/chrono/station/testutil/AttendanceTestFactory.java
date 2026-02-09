package com.chrono.station.testutil;

import com.chrono.station.domain.attendance.Attendance;
import com.chrono.station.domain.attendance.WorkingStatus;
import com.chrono.station.domain.attendance.WorkType;
import com.chrono.station.domain.employee.Employee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AttendanceTestFactory {

    public static Attendance create(LocalDate date) {

        Employee employee = EmployeeTestFactory.create();

        return Attendance.builder()
                .attendanceId(1L)
                .employee(employee)
                .workDate(date)
                .workStart(LocalDateTime.of(date, LocalTime.of(9, 0)))
                .workEnd(LocalDateTime.of(date, LocalTime.of(18, 0)))
                .breakMinutes(60)
                .workingStatus(WorkingStatus.WORKING)
                .build();
    }

    //退勤済み
    public static Attendance finished(LocalDate date) {
        return Attendance.builder()
                .employee(EmployeeTestFactory.create())
                .workDate(date)
                .workingStatus(WorkingStatus.FINISHED)
                .workType(WorkType.NORMAL)
                .workStart(date.atTime(9, 0))
                .workEnd(date.atTime(18, 0))
                .breakMinutes(60)
                .totalWorkMinutes(480)
                .build();
    }

    //出勤中
    public static Attendance working(LocalDate date) {
        return Attendance.builder()
                .employee(EmployeeTestFactory.create())
                .workDate(date)
                .workingStatus(WorkingStatus.WORKING)
                .workType(WorkType.NORMAL)
                .workStart(date.atTime(9, 0))
                .workEnd(null)
                .build();
    }

    //有給
    public static Attendance paidLeave(LocalDate date) {
        return Attendance.builder()
                .employee(EmployeeTestFactory.create())
                .workDate(date)
                .workType(WorkType.PAID_LEAVE)
                .build();
    }
}
