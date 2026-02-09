package com.chrono.station.dto.admin;

import com.chrono.station.domain.request.correction.AttendanceCorrection;
import com.chrono.station.domain.request.correction.CorrectionStatus;
import com.chrono.station.domain.request.correction.CorrectionType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CorrectionRequestListDto(
        Long correctionId,
        LocalDate workDate,
        CorrectionType type,    //ロジック用
        String typeLabel,       //表示用
        CorrectionStatus status,//ロジック用
        String statusLabel,     //表示用
        LocalDateTime createdAt
) {
    public static CorrectionRequestListDto from(AttendanceCorrection c){
        return new CorrectionRequestListDto(
                c.getCorrectionId(),
                c.getWorkDate(),
                c.getType(),
                c.getType().getLabel(),
                c.getStatus(),
                c.getStatus().getLabel(),
                c.getCreatedAt()
        );
    }
}
