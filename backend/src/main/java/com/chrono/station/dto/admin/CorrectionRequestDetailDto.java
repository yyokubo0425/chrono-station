package com.chrono.station.dto.admin;

import com.chrono.station.domain.request.correction.AttendanceCorrection;
import com.chrono.station.domain.request.correction.CorrectionStatus;
import com.chrono.station.domain.request.correction.CorrectionType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CorrectionRequestDetailDto(
        Long correctionId,
        LocalDate workDate,
        LocalDateTime beforeWorkStart,
        Integer beforeBreakMinutes,
        LocalDateTime beforeWorkEnd,
        LocalDateTime afterWorkStart,
        Integer afterBreakMinutes,
        LocalDateTime afterWorkEnd,
        String reason,
        CorrectionType type,    //ロジック用
        String typeLabel,       //表示用
        CorrectionStatus status,//ロジック用
        String statusLabel,     //表示用
        LocalDateTime createdAt
) {
    public static CorrectionRequestDetailDto from(AttendanceCorrection c) {
        return new CorrectionRequestDetailDto(
                c.getCorrectionId(),
                c.getWorkDate(),
                c.getBeforeWorkStart(),
                c.getBeforeBreakMinutes(),
                c.getBeforeWorkEnd(),
                c.getAfterWorkStart(),
                c.getAfterBreakMinutes(),
                c.getAfterWorkEnd(),
                c.getReason(),
                c.getType(),
                c.getType().getLabel(),
                c.getStatus(),
                c.getStatus().getLabel(),
                c.getCreatedAt()
        );
    }
}
