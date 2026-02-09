package com.chrono.station.domain.request.correction;

import com.chrono.station.domain.attendance.Attendance;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "attendance_correction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AttendanceCorrection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "correction_id")
    private Long correctionId;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id")
    private Attendance attendance;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    //元の打刻
    @Column(name = "before_work_start")
    private LocalDateTime beforeWorkStart;

    @Column(name = "before_work_end")
    private LocalDateTime beforeWorkEnd;

    @Column(name = "before_break_minutes")
    private Integer beforeBreakMinutes;

    // 修正後の打刻（申請内容）
    @Column(name = "after_work_start")
    private LocalDateTime afterWorkStart;

    @Column(name = "after_work_end")
    private LocalDateTime afterWorkEnd;

    @Column(name = "after_break_minutes")
    private Integer afterBreakMinutes;

    //修正理由
    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    //却下理由
    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    // 状態
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private CorrectionType type;

    //ステータス
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CorrectionStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    //承認済み
    public void approve() {
        this.status = CorrectionStatus.APPROVED;
        this.updatedAt = LocalDateTime.now();
    }

    //不承認
    public void reject(String rejectReason) {
        if (rejectReason == null || rejectReason.isBlank()) {
            throw new IllegalArgumentException("却下理由は必須です");
        }
        this.status = CorrectionStatus.REJECTED;
        this.rejectReason = rejectReason;
        this.updatedAt = LocalDateTime.now();
    }

    //打刻漏れ初期状態
    public static AttendanceCorrection createTimeUnrecord(
            Long employeeId,
            LocalDate workDate,
            LocalTime requestedWorkStart,
            LocalTime requestedWorkEnd,
            Integer requestedBreakMinutes,
            String reason
    ){
        return AttendanceCorrection.builder()
                .employeeId(employeeId)
                .workDate(workDate)
                .beforeWorkStart(null)
                .beforeWorkEnd(null)
                .beforeBreakMinutes(null)
                .afterWorkStart(requestedWorkStart != null
                ? LocalDateTime.of(workDate,requestedWorkStart)
                        : null)
                .afterWorkEnd(requestedWorkEnd != null
                ? LocalDateTime.of(workDate, requestedWorkEnd)
                        : null)
                .afterBreakMinutes(requestedBreakMinutes)
                .reason(reason)
                .type(CorrectionType.TIME_UNRECORD)
                .status(CorrectionStatus.REQUESTED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
