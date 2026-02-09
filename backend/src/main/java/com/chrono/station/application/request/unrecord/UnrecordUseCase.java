package com.chrono.station.application.request.unrecord;

import com.chrono.station.application.auth.CurrentUserProvider;
import com.chrono.station.domain.attendance.AttendanceRepository;
import com.chrono.station.domain.request.correction.AttendanceCorrection;
import com.chrono.station.domain.request.correction.AttendanceCorrectionRepository;
import com.chrono.station.domain.request.unrecord.AttendanceUnrecordService;
import com.chrono.station.dto.request.unrecord.UnrecordRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UnrecordUseCase {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceCorrectionRepository correctionRepository;
    private final AttendanceUnrecordService attendanceUnrecordService;
    private final CurrentUserProvider currentUserProvider;

    //打刻漏れ申請
    @Transactional
    public void unrecordRequest(LocalDate workDate, UnrecordRequestDto dto) {

        Long employeeId = currentUserProvider.getEmployeeId();

        //勤怠の存在チェック
        if (attendanceRepository.existsByEmployee_EmployeeIdAndWorkDate(employeeId, workDate)) {
            throw new IllegalStateException("既に勤怠が存在します。");
        }

        //Entity生成
        AttendanceCorrection correction = AttendanceCorrection.createTimeUnrecord(
                employeeId,
                workDate,
                dto.requestedWorkStart(),
                dto.requestedWorkEnd(),
                dto.requestedBreakMinutes(),
                dto.reason()
        );

        //domain打刻漏れチェック
        attendanceUnrecordService.validateForApply(correction);

        correctionRepository.save(correction);
    }
}
