package com.chrono.station.admin;

import com.chrono.station.application.admin.AttendanceApprovalUseCase;
import com.chrono.station.domain.attendance.Attendance;
import com.chrono.station.domain.attendance.AttendanceRepository;
import com.chrono.station.domain.attendance.WorkType;
import com.chrono.station.domain.attendance.WorkingStatus;
import com.chrono.station.domain.request.correction.AttendanceCorrection;
import com.chrono.station.domain.request.correction.AttendanceCorrectionFactory;
import com.chrono.station.domain.request.correction.AttendanceCorrectionRepository;
import com.chrono.station.domain.request.correction.CorrectionStatus;
import com.chrono.station.testutil.EmployeeTestFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class AttendanceCorrectionApprovalUseCaseIT {

    @Autowired
    AttendanceApprovalUseCase approvalUseCase;

    @Autowired
    AttendanceCorrectionRepository correctionRepository;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Test
    void 申請中の修正申請を承認できること() {

        //勤怠作成
        Attendance attendance = Attendance.builder()
                .employee(EmployeeTestFactory.create())
                .workDate(LocalDate.of(2025, 1, 10))
                .workStart(LocalDateTime.of(2025, 1, 10, 9, 0))
                .workEnd(LocalDateTime.of(2025, 1, 10, 18, 0))
                .breakMinutes(60)
                .workingStatus(WorkingStatus.FINISHED)
                .workType(WorkType.NORMAL)
                .build();
        attendance = attendanceRepository.save(attendance);

        //修正申請作成
        AttendanceCorrection correction = AttendanceCorrectionFactory.create(
                attendance,
                LocalDateTime.of(2025, 1, 10, 9, 0),
                LocalDateTime.of(2025, 1, 10, 18, 0),
                60,
                "打刻修正"
        );
        correction = correctionRepository.save(correction);

        approvalUseCase.approve(correction.getCorrectionId());

        AttendanceCorrection saved =
                correctionRepository.findByCorrectionId(correction.getCorrectionId())
                        .orElseThrow();

        assertEquals(CorrectionStatus.APPROVED, saved.getStatus());
    }

    @Test
    void 承認済みの修正申請は再承認できないこと() {

        //勤怠作成
        Attendance attendance = Attendance.builder()
                .employee(EmployeeTestFactory.create())
                .workDate(LocalDate.of(2025, 1, 10))
                .workStart(LocalDateTime.of(2025, 1, 10, 9, 0))
                .workEnd(LocalDateTime.of(2025, 1, 10, 18, 0))
                .breakMinutes(60)
                .workingStatus(WorkingStatus.FINISHED)
                .workType(WorkType.NORMAL)
                .build();
        attendance = attendanceRepository.save(attendance);

        AttendanceCorrection correction = AttendanceCorrectionFactory.create(
                attendance,
                LocalDateTime.now(),
                LocalDateTime.now(),
                60,
                "修正"
        );
        correction.approve();  //先に承認
        correction = correctionRepository.save(correction);

        final Long correctionId = correction.getCorrectionId();
        assertThrows(IllegalStateException.class,
                () -> approvalUseCase.approve(correctionId));

    }

    @Test
    void 申請中の申請修正を却下できること() {

        Attendance attendance = attendanceRepository.save(
                Attendance.builder()
                        .employee(EmployeeTestFactory.create())
                        .workDate(LocalDate.of(2025, 12, 31))
                        .workingStatus(WorkingStatus.FINISHED)
                        .workType(WorkType.NORMAL)
                        .build()
        );

        AttendanceCorrection correction = AttendanceCorrectionFactory.create(
                attendance,
                LocalDateTime.now(),
                LocalDateTime.now(),
                30,
                "修正"
        );
        correction = correctionRepository.save(correction);

        approvalUseCase.reject(correction.getCorrectionId(), "理由不十分");

        AttendanceCorrection saved = correctionRepository.findByCorrectionId(
                        correction.getCorrectionId())
                .orElseThrow();

        assertEquals(CorrectionStatus.REJECTED, saved.getStatus());
        assertEquals("理由不十分", saved.getRejectReason());
    }

    @Test
    void 却下理由がnullの場合はエラーになること() {

        Attendance attendance = attendanceRepository.save(
                Attendance.builder()
                        .employee(EmployeeTestFactory.create())
                        .workDate(LocalDate.of(2025, 12, 31))
                        .workingStatus(WorkingStatus.FINISHED)
                        .workType(WorkType.NORMAL)
                        .build()
        );

        AttendanceCorrection correction = correctionRepository.save(
                AttendanceCorrectionFactory.create(
                        attendance,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        30,
                        "修正"
                ));

        assertThrows(IllegalArgumentException.class,
                () -> approvalUseCase.reject(correction.getCorrectionId(), ""));
    }
}
