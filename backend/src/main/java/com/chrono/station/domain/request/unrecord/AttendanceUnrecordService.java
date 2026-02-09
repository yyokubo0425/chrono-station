package com.chrono.station.domain.request.unrecord;

import com.chrono.station.domain.request.correction.AttendanceCorrection;
import com.chrono.station.domain.request.correction.CorrectionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendanceUnrecordService {

    //打刻漏れチェック
    public void validateForApply(AttendanceCorrection correction) {
        if (correction.getType() == CorrectionType.TIME_UNRECORD){
            if(correction.getAfterWorkStart() == null){
                throw new IllegalArgumentException("出勤時刻は必須です。");
            }
            if(correction.getReason() == null || correction.getReason().isBlank()){
                throw new IllegalArgumentException("理由は必須です。");
            }
        }
    }
}
