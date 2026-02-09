package com.chrono.station.application.daily;

import com.chrono.station.domain.attendance.Attendance;
import com.chrono.station.domain.attendance.AttendanceRepository;
import com.chrono.station.domain.attendance.DayCategory;
import com.chrono.station.domain.attendance.WorkType;
import com.chrono.station.domain.employee.Employee;
import com.chrono.station.domain.employee.EmployeeRepository;
import com.chrono.station.domain.holiday.HolidayService;
import com.chrono.station.dto.daily.DailyAttendanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DailyAttendanceUseCase {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final HolidayService holidayService;

    public DailyAttendanceDto getDaily(Long employeeId, LocalDate date) {
        LocalDate today = LocalDate.now();

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("従業員が存在しません"));


        Attendance attendance = attendanceRepository.findByEmployeeAndWorkDate(employee, date)
                .orElse(null);

        DayCategory category;
        if (date.isAfter(today)) {
            //未来
            category = DayCategory.FUTURE;
        } else if (holidayService.isHoliday(date)) {
            //過去の土日
            category = DayCategory.HOLIDAY;
        } else {
            //過去の平日
            category = DayCategory.WEEKDAY;
        }

        if (attendance == null) {
            WorkType type = category == DayCategory.WEEKDAY ? WorkType.ABSENT : WorkType.NONE;
            return DailyAttendanceDto.empty(date, type);
        }

        return DailyAttendanceDto.from(attendance);
    }

    //打刻修正申請の勤怠を１件取得
    @Transactional(readOnly = true)
    public Attendance getCorrectionAttendance(Long attendanceId){
        return attendanceRepository.findByAttendanceId(attendanceId)
                .orElseThrow(() -> new IllegalArgumentException("勤怠が存在しません。"));
    }
}
