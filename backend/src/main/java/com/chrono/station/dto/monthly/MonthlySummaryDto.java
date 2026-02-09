package com.chrono.station.dto.monthly;

public record MonthlySummaryDto (
        int scheduledWorkDays,              // 所定労働日数
        int workDays,                       // 出勤日数
        int absenceDays,                    // 欠勤日数
        Integer overtimeMinutes,            // 残業時間
        Integer monthlyTotalWorkMinutes,    // 総労働時間
        int paidLeaveDays                   // 有休日数
){}
