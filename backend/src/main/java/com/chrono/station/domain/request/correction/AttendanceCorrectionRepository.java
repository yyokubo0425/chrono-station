package com.chrono.station.domain.request.correction;

import com.chrono.station.domain.attendance.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceCorrectionRepository extends JpaRepository<AttendanceCorrection, Long> {
    //打刻修正IDを取得
    Optional<AttendanceCorrection> findByCorrectionId(Long correctionId);

    //勤怠に申請中があるか取得
    Optional<AttendanceCorrection> findTopByAttendanceAndStatusOrderByCreatedAtDesc(
            Attendance attendance, CorrectionStatus status
    );

    //申請中のみをリスト化で取得
    List<AttendanceCorrection> findByStatusOrderByCreatedAtAsc(CorrectionStatus status);
}
