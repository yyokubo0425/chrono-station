package com.chrono.station.dto.daily;

import com.chrono.station.domain.attendance.Attendance;
import com.chrono.station.domain.attendance.WorkType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public record DailyAttendanceDto(
        Long attendanceId,
        LocalDate workDate,               //日付
        String dayOfWeek,                 //曜日
        LocalDateTime workStart,          //出勤時刻
        LocalDateTime workEnd,            //退勤時刻
        Integer breakMinutes,             //休憩時間
        Integer totalWorkMinutes,         //労働時間
        String workType,                //勤務区分
        //↓拡張用
        //残業時間
        //深夜時間
        //休日労働時間
        //修正ステータス
        //打刻方法
        boolean exists
) {

    //勤怠なし
    public static DailyAttendanceDto empty(LocalDate date, WorkType type) {
        return new DailyAttendanceDto(
                null,
                date,
                toDayOfWeek(date),
                null,
                null,
                null,
                null,
                type.getLabel(),
                false
        );
    }

    //勤怠あり
    public static DailyAttendanceDto from(Attendance a) {
        return new DailyAttendanceDto(
                a.getAttendanceId(),
                a.getWorkDate(),
                toDayOfWeek(a.getWorkDate()),
                a.getWorkStart(),
                a.getWorkEnd(),
                a.getBreakMinutes(),
                a.getTotalWorkMinutes(),
                a.getWorkType().getLabel(),
                true
        );
    }

    //曜日変換
    private static String toDayOfWeek(LocalDate date) {
        return date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPAN);
    }
}
