package com.chrono.station.application.request;

import com.chrono.station.application.auth.CurrentUserProvider;
import com.chrono.station.application.request.unrecord.UnrecordUseCase;
import com.chrono.station.domain.attendance.AttendanceRepository;
import com.chrono.station.domain.request.correction.AttendanceCorrection;
import com.chrono.station.domain.request.correction.AttendanceCorrectionRepository;
import com.chrono.station.domain.request.unrecord.AttendanceUnrecordService;
import com.chrono.station.dto.request.unrecord.UnrecordRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UnrecordUseCaseTest {

    @Mock
    AttendanceRepository attendanceRepository;

    @Mock
    AttendanceCorrectionRepository correctionRepository;

    @Mock
    AttendanceUnrecordService attendanceUnrecordService;

    @Mock
    CurrentUserProvider currentUserProvider;

    @InjectMocks
    UnrecordUseCase unrecordUseCase;

    @Test
    void 勤怠が存在しない場合_打刻漏れ申請が保存されること(){

        Long employeeId = 1L;
        LocalDate workDate = LocalDate.of(2026,1,1);

        UnrecordRequestDto dto = new UnrecordRequestDto(
          LocalTime.of(9,0),
          LocalTime.of(18,0),
          60,
          "打刻漏れのため申請します。"
        );

        when(currentUserProvider.getEmployeeId())
                .thenReturn(employeeId);

        when(attendanceRepository
                .existsByEmployee_EmployeeIdAndWorkDate(employeeId,workDate))
                .thenReturn(false);

        unrecordUseCase.unrecordRequest(workDate,dto);

        //引数がAttendanceCorrection型になるための変数を用意
        ArgumentCaptor<AttendanceCorrection> captor =
                ArgumentCaptor.forClass(AttendanceCorrection.class);

        //引数に渡された値を検証するための準備
        verify(correctionRepository).save(captor.capture());

        AttendanceCorrection saved = captor.getValue();

        assertEquals(LocalDateTime.of(workDate, LocalTime.of(9,0)),
                saved.getAfterWorkStart());
    }

    @Test
    void 勤怠が存在する場合_例外が投げられること(){

        Long employeeId = 1L;
        LocalDate workDate = LocalDate.of(2025,1,1);

        UnrecordRequestDto dto = new UnrecordRequestDto(
                LocalTime.of(9,0),
                LocalTime.of(18,0),
                60,
                "打刻漏れのため申請します。"
        );

        when(currentUserProvider.getEmployeeId())
                .thenReturn(employeeId);

        when(attendanceRepository
                .existsByEmployee_EmployeeIdAndWorkDate(employeeId, workDate))
                .thenReturn(true);

        //例外が投げられることの検証
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> unrecordUseCase.unrecordRequest(workDate, dto)
        );

        //例外メッセージの検証
        assertEquals("既に勤怠が存在します。", exception.getMessage());

        //保存処理が呼ばれないこと
        verify(correctionRepository, never()).save(any(AttendanceCorrection.class));
    }
}