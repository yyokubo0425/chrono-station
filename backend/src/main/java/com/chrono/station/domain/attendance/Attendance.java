package com.chrono.station.domain.attendance;

import com.chrono.station.domain.employee.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Attendance {

    //勤怠ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    //従業員ID(外部キー)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    //勤務日
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    //勤怠ステータス
    @Column(name = "working_status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkingStatus workingStatus;

    //勤務区分
    @Column(name = "work_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkType workType;

    //出勤時刻
    @Column(name = "work_start")
    private LocalDateTime workStart;

    //退勤時刻
    @Column(name = "work_end")
    private LocalDateTime workEnd;

    // 休憩開始時刻
    @Column(name = "break_start")
    private LocalDateTime breakStart;

    // 休憩終了時刻
    @Column(name = "break_end")
    private LocalDateTime breakEnd;

    // 集計用（派生データ）
    @Column(name = "break_minutes")
    private Integer breakMinutes;

    //労務時間(分)
    @Column(name = "total_work_minutes")
    private Integer totalWorkMinutes;

    //登録日
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    //更新日
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void beforeInsert() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
