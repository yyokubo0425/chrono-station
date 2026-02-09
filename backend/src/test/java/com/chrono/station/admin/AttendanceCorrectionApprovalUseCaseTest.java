package com.chrono.station.admin;

import com.chrono.station.application.admin.AttendanceApprovalUseCase;
import com.chrono.station.domain.request.correction.AttendanceCorrection;
import com.chrono.station.domain.request.correction.AttendanceCorrectionRepository;
import com.chrono.station.domain.request.correction.CorrectionStatus;
import com.chrono.station.dto.admin.CorrectionRequestDetailDto;
import com.chrono.station.dto.admin.CorrectionRequestListDto;
import com.chrono.station.testutil.EmployeeTestFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AttendanceCorrectionApprovalUseCaseTest {

    @Mock
    AttendanceCorrectionRepository correctionRepository;

    @InjectMocks
    AttendanceApprovalUseCase approvalUseCase;

    @Test
    void 申請中の修正申請が古い順で取得されること() {

        LocalDateTime older = LocalDateTime.of(2026, 1, 1, 9, 0);
        LocalDateTime newer = LocalDateTime.of(2026, 2, 1, 9, 0);

        AttendanceCorrection oldCorrection = AttendanceCorrection.builder()
                .correctionId(1L)
                .employeeId(EmployeeTestFactory.create().getEmployeeId())
                .workDate(LocalDate.of(2025, 11, 30))
                .status(CorrectionStatus.REQUESTED)
                .createdAt(older)
                .build();

        AttendanceCorrection newCorrection = AttendanceCorrection.builder()
                .correctionId(2L)
                .employeeId(EmployeeTestFactory.create().getEmployeeId())
                .workDate(LocalDate.of(2025, 12, 1))
                .status(CorrectionStatus.REQUESTED)
                .createdAt(newer)
                .build();

        when(correctionRepository.findByStatusOrderByCreatedAtAsc(CorrectionStatus.REQUESTED))
                .thenReturn(List.of(oldCorrection, newCorrection));

        List<CorrectionRequestListDto> result = approvalUseCase.getAfterCorrections();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).correctionId());
        assertEquals(2L, result.get(1).correctionId());
        assertEquals(CorrectionStatus.REQUESTED, result.get(0).status());
    }

    @Test
    void 指定した修正申請IDの詳細が取得できる(){
        Long correctionId = 1L;

        AttendanceCorrection correction =AttendanceCorrection.builder()
                .correctionId(correctionId)
                .employeeId(EmployeeTestFactory.create().getEmployeeId())
                .workDate(LocalDate.of(2026,1,1))
                .beforeWorkStart(LocalDateTime.of(2026,1,1,9,0))
                .afterWorkStart(LocalDateTime.of(2026,1,1,10,0))
                .beforeWorkEnd(LocalDateTime.of(2026,1,1,18,0))
                .afterWorkEnd(LocalDateTime.of(2026,1,1,19,0))
                .reason("打刻漏れ")
                .status(CorrectionStatus.REQUESTED)
                .createdAt(LocalDateTime.of(2026,1,1,8,0))
                .build();

        when(correctionRepository.findByCorrectionId(correctionId))
                .thenReturn(Optional.of(correction));

        CorrectionRequestDetailDto result = approvalUseCase.getDetail(correctionId);

        assertEquals(correctionId, result.correctionId());
        assertEquals(1L, result);
        assertEquals(LocalDate.of(2026,1,1), result.workDate());
        assertEquals(LocalDateTime.of(2026,1,1,10,0), result.afterWorkStart());
        assertEquals(LocalDateTime.of(2026,1,1,19,0), result.afterWorkEnd());
        assertEquals("打刻漏れ", result.reason());
        assertEquals(CorrectionStatus.REQUESTED, result.status());
    }


}
