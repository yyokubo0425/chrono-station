package com.chrono.station.application.attendance;

import com.chrono.station.application.monthly.MonthlyAttendanceUseCase;
import com.chrono.station.domain.attendance.Attendance;
import com.chrono.station.domain.attendance.AttendanceRepository;
import com.chrono.station.domain.holiday.HolidayService;
import com.chrono.station.dto.monthly.DailyAttendancesDto;
import com.chrono.station.dto.monthly.MonthlyAttendanceDto;
import com.chrono.station.testutil.AttendanceTestFactory;
import com.chrono.station.testutil.EmployeeTestFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MonthlyAttendanceUseCaseTest {

    @Mock
    AttendanceRepository attendanceRepository;

    @Mock
    HolidayService holidayService;

    @InjectMocks
    MonthlyAttendanceUseCase useCase;

    Long employeeId = 1L;
    YearMonth ym = YearMonth.of(2025, 12);

    @Test
    void 平日の勤怠が正しく月次集計される() {

        Attendance decl = AttendanceTestFactory.finished(
                LocalDate.of(2025, 12, 1)
        );

        when(attendanceRepository.findByEmployeeAndWorkDateBetween(
                eq(EmployeeTestFactory.create()),
                eq(LocalDate.of(2025, 12, 1)),
                eq(LocalDate.of(2025, 12, 31))
        )).thenReturn(List.of(decl));

        when(holidayService.isHoliday(any(LocalDate.class)))
                .thenReturn(false);

        MonthlyAttendanceDto result =
                useCase.getMonthly(employeeId, ym);

        assertEquals(result.summary().scheduledWorkDays(),
                result.dailyAttendances().size());
        assertEquals(1, result.summary().workDays());
        assertEquals(480, result.summary().monthlyTotalWorkMinutes());
    }

    @Test
    void 勤怠がない日は欠勤として補完される() {

        when(attendanceRepository.findByEmployeeAndWorkDateBetween(
                any(), any(), any())).thenReturn(List.of());

        when(holidayService.isHoliday(any(LocalDate.class)))
                .thenReturn(false);

        MonthlyAttendanceDto result = useCase.getMonthly(employeeId, ym);

        DailyAttendancesDto firstDay =
                result.dailyAttendances().get(0);

        assertEquals("欠勤", firstDay.workTypeLabel());
    }

    @Test
    void 出勤した日は勤務日としてカウントされる() {

        Attendance attendance = AttendanceTestFactory.finished(
                LocalDate.of(2025, 12, 1)
        );

        when(attendanceRepository.findByEmployeeAndWorkDateBetween(
                any(), any(), any()
        )).thenReturn(List.of(attendance));

        when(holidayService.isHoliday(any(LocalDate.class)))
                .thenReturn(false);

        MonthlyAttendanceDto result = useCase.getMonthly(employeeId, ym);

        assertEquals(1, result.summary().workDays());
        assertEquals(480, result.summary().monthlyTotalWorkMinutes());
    }
}
