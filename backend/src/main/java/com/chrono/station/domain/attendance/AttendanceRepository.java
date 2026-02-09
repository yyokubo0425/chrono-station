package com.chrono.station.domain.attendance;

import com.chrono.station.domain.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    //指定した従業員と日付の勤怠を取得
    Optional<Attendance> findByEmployeeAndWorkDate(Employee employee, LocalDate workDate);

    //年月を取得
    List<Attendance> findByEmployeeAndWorkDateBetween(Employee employee, LocalDate from, LocalDate to);

    Optional<Attendance> findByAttendanceId(Long attendanceIOd);

    Optional<Attendance> findTopByAttendanceIdOrderByCreatedAtDesc(Long attendanceId);

    //打刻漏れ申請用
    boolean existsByEmployee_EmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);
}
