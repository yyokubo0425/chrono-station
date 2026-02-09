package com.chrono.station.application.monthly;

import com.chrono.station.domain.attendance.Attendance;
import com.chrono.station.domain.attendance.AttendanceRepository;
import com.chrono.station.domain.attendance.DayCategory;
import com.chrono.station.domain.attendance.WorkType;
import com.chrono.station.domain.employee.Employee;
import com.chrono.station.domain.employee.EmployeeRepository;
import com.chrono.station.domain.holiday.HolidayService;
import com.chrono.station.dto.monthly.DailyAttendancesDto;
import com.chrono.station.dto.monthly.MonthlyAttendanceDto;
import com.chrono.station.dto.monthly.MonthlySummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonthlyAttendanceUseCase {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final HolidayService holidayService;

    @Transactional(readOnly = true)
    public MonthlyAttendanceDto getMonthly(Long employeeId, YearMonth yearMonth) {
        LocalDate today = LocalDate.now();
        //月初～月末
        LocalDate from = yearMonth.atDay(1);
        LocalDate to = yearMonth.atEndOfMonth();

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("従業員が存在しません"));

        //勤怠取得
        List<Attendance> attendances =
                attendanceRepository.findByEmployeeAndWorkDateBetween(employee, from, to);


        //日次Map(欠勤判定用)
        Map<LocalDate, Attendance> attendanceMap =
                attendances.stream()
                        .collect(Collectors.toMap(
                                Attendance::getWorkDate,
                                a -> a,
                                (existing, replacement) -> existing
                        ));

        //日次DTO作成(欠勤日も含める)
        List<DailyAttendancesDto> dailyDtos = new ArrayList<>();

        //１か月の日次DTO作成
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {

            Attendance attendance = attendanceMap.get(date);
            dailyDtos.add(toDailyDto(date, attendance, today));
        }

        //サマリー算出
        MonthlySummaryDto summary = calcSummary(dailyDtos);

        return new MonthlyAttendanceDto(yearMonth, dailyDtos, summary);
    }

    //１か月の日次勤怠作成
    private DailyAttendancesDto toDailyDto(LocalDate date, Attendance attendance, LocalDate today) {
        //曜日取得
        String dayOfWeek =
                date.getDayOfWeek()
                        .getDisplayName(TextStyle.SHORT, Locale.JAPAN);

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

        //勤怠なし
        if (attendance == null) {
            WorkType type =
                    category == DayCategory.WEEKDAY ? WorkType.ABSENT : WorkType.NONE;


            return new DailyAttendancesDto(
                    date,
                    dayOfWeek,
                    null,
                    null,
                    null,
                    type,
                    toWorkTypeLabel(type),
                    category
            );
        }


        //勤怠あり
        return new DailyAttendancesDto(
                date,
                dayOfWeek,
                attendance.getWorkStart(),
                attendance.getWorkEnd(),
                attendance.getTotalWorkMinutes(),
                attendance.getWorkType(),
                toWorkTypeLabel(attendance.getWorkType()),
                category
        );
    }

    //月次サマリー算出
    private MonthlySummaryDto calcSummary(List<DailyAttendancesDto> dailyDtos
    ) {
        //所定労働日数
        int scheduledWorkDays = (int) dailyDtos.stream()
                .filter(d -> !holidayService.isHoliday(d.date()))
                .count();

        //出勤日数を算出（実労働が発生した日）
        int workDays = (int) dailyDtos.stream()
                .filter(d ->
                        d.totalWorkMinutes() != null &&
                                d.totalWorkMinutes() > 0)
                .count();

        //有給日数を算出
        int paidLeaveDays = (int) dailyDtos.stream()
                .filter(d -> d.workType() == WorkType.PAID_LEAVE)
                .count();

        //欠勤日数を算出
        int absenceDays = (int) dailyDtos.stream()
                .filter(d -> d.dayCategory() == DayCategory.WEEKDAY &&
                        d.workType() == WorkType.ABSENT)
                .count();

        //月間労働時間の合計を算出(分)
        int monthlyTotalWorkMinutes = dailyDtos.stream()
                .map(DailyAttendancesDto::totalWorkMinutes)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        return new MonthlySummaryDto(
                scheduledWorkDays,
                workDays,
                absenceDays,
                0,
                monthlyTotalWorkMinutes,
                paidLeaveDays
        );
    }

    //ステータスを日本語に変換
    private String toWorkTypeLabel(WorkType type) {
        return switch (type) {
            case NONE -> "―";
            case NORMAL -> "通常";
            case ABSENT -> "欠勤";
            case PAID_LEAVE -> "有給";
            case HOLIDAY_WORK -> "休出";
        };
    }
}
