package com.chrono.station.dto.monthly;

import com.chrono.station.domain.attendance.DayCategory;
import com.chrono.station.domain.attendance.WorkType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DailyAttendancesDto(
        LocalDate date,               // 日付
        String dayOfWeek,             // 曜日
        LocalDateTime workStart,      // 出勤
        LocalDateTime workEnd,        // 退勤
        Integer totalWorkMinutes,     // 実働
        WorkType workType,            // enum
        String workTypeLabel,         // 表示用勤務区分（通常/欠勤/有休）
        DayCategory dayCategory       // 曜日種類(平日、休日・祝日、未来日)
){}
