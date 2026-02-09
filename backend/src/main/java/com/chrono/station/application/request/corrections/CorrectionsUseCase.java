package com.chrono.station.application.request.corrections;

import com.chrono.station.application.auth.CurrentUserProvider;
import com.chrono.station.domain.attendance.Attendance;
import com.chrono.station.domain.attendance.AttendanceRepository;
import com.chrono.station.domain.request.correction.AttendanceCorrection;
import com.chrono.station.domain.request.correction.AttendanceCorrectionFactory;
import com.chrono.station.domain.request.correction.AttendanceCorrectionRepository;
import com.chrono.station.domain.request.correction.AttendanceCorrectionService;
import com.chrono.station.dto.request.correction.AttendanceDailyDto;
import com.chrono.station.dto.request.correction.CorrectionRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CorrectionsUseCase {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceCorrectionRepository correctionRepository;
    private final AttendanceCorrectionService correctionService;
    private final CurrentUserProvider currentUserProvider;

    //勤怠を１件取得
    @Transactional(readOnly = true)
    public AttendanceDailyDto getForCorrection(
            Long attendanceId,
            Long employeeId
    ){
        Attendance attendance = attendanceRepository.findByAttendanceId(attendanceId)
                .orElseThrow(() -> new IllegalArgumentException("勤怠が存在しません。"));

        //勤怠情報が本人かチェック
        if (!attendance.getEmployee().getEmployeeId().equals(employeeId)) {
            throw new AccessDeniedException("他人の勤怠にはアクセスできません。");
        }

        return AttendanceDailyDto.from(attendance);
    }


    //打刻修正申請
    @Transactional
    public void requestCorrection(
            Long attendanceId, CorrectionRequestDto dto
    ) {

        //勤怠を取得
        Attendance attendance = attendanceRepository
                .findById(attendanceId)
                .orElseThrow(() -> new IllegalArgumentException("勤怠が存在しません。"));

        //勤怠情報が本人かチェック
        Long employeeId = currentUserProvider.getEmployeeId();
        if (!attendance.getEmployee().getEmployeeId().equals(employeeId)) {
            throw new AccessDeniedException("他人の勤怠にはアクセスできません。");
        }

        //修正申請ができるか判定
        correctionService.dataRequest(attendance);

        //修正内容が入力されてるか判定
        if(dto.reason() == null || dto.reason().isBlank()){
            throw new IllegalArgumentException("修正理由が必須です。");
        }

        if(dto.requestedWorkStart() == null
        && dto.requestedWorkEnd() == null
        && dto.requestedBreakMinutes() == null){
            throw new IllegalArgumentException("修正内容が入力されてません。");
        }

        //「hh:mm」→「yyyy-mm-dd-hh-mmに変換」
        LocalDate workDate = attendance.getWorkDate();
        LocalDateTime requestedWorkStart =
                dto.requestedWorkStart()!= null ? LocalDateTime.of(workDate,dto.requestedWorkStart()) : null;
        LocalDateTime requestedWorkEnd =
                dto.requestedWorkEnd()!= null ? LocalDateTime.of(workDate,dto.requestedWorkEnd()) : null;

        //出勤時刻＜退勤時刻判定
        if(requestedWorkStart != null && requestedWorkEnd != null){
            if(!requestedWorkStart.isBefore(requestedWorkEnd)){
                throw new IllegalArgumentException("出勤時刻は退勤時刻より前である必要があります。");
            }
        }

        //休憩時間が0分以上か判定
        if(dto.requestedBreakMinutes() != null && dto.requestedBreakMinutes() < 0){
            throw new IllegalArgumentException("休憩時間は0分以上で入力してください。");
        }

        //修正申請データ生成
        AttendanceCorrection correction = AttendanceCorrectionFactory.create(
                attendance,
                requestedWorkStart,
                requestedWorkEnd,
                dto.requestedBreakMinutes(),
                dto.reason()
        );

        correctionRepository.save(correction);
    }
}
