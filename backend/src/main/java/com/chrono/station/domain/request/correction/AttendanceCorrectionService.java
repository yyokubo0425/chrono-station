package com.chrono.station.domain.request.correction;

import com.chrono.station.domain.attendance.Attendance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendanceCorrectionService {

    private final AttendanceCorrectionRepository attendanceCorrectionRepository;

    //修正申請の判定
    public void dataRequest(Attendance attendance){
        attendanceCorrectionRepository
                .findTopByAttendanceAndStatusOrderByCreatedAtDesc(
                        attendance, CorrectionStatus.REQUESTED
                )
                .ifPresent(correction -> {
                    throw new IllegalStateException("すでに修正申請中の勤怠です。");
                });
    }
}
