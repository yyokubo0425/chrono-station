package com.chrono.station.domain.attendance;

import com.chrono.station.domain.employee.Employee;
import com.chrono.station.domain.employee.EmployeeRepository;
import com.chrono.station.domain.holiday.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final HolidayService holidayService;

    LocalDate today = LocalDate.now();

    //出勤打刻
    public Attendance clockIn(Long employeeId) {

        //従業員データ取得
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("従業員が存在しません。"));

        //今日の勤怠レコードを取得(nullならOptional.empty)
        Optional<Attendance> existingRecord =
                attendanceRepository.findByEmployeeAndWorkDate(employee, today);

        //既にレコードが存在かつ出勤済みの場合エラー
        if (existingRecord.isPresent() && existingRecord.get().getWorkingStatus() != WorkingStatus.NOT_STARTED) {
            throw new IllegalStateException("すでに出勤打刻されています。");
        }

        //レコードが存在しない場合はレコードを新規作成
        Attendance attendance = existingRecord.orElse(
                Attendance.builder()
                        .employee(employee)
                        .workDate(today)
                        .workingStatus(WorkingStatus.NOT_STARTED)
                        .build()
        );

        //出勤情報をセット
        if(holidayService.isHoliday(today)){
            attendance.setWorkType(WorkType.HOLIDAY_WORK);
        }else{
            attendance.setWorkType(WorkType.NORMAL);
        }
        attendance.setWorkStart(LocalDateTime.now().withNano(0));
        attendance.setWorkingStatus(WorkingStatus.WORKING);

        //DBに保存
        return attendanceRepository.save(attendance);
    }

    //退勤打刻
    public Attendance clockOut(Long employeeId) {

        //従業員データ取得
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("従業員が存在しません。"));


        //今日の勤怠レコードを取得(nullならエラー)
        Attendance attendance = attendanceRepository
                .findByEmployeeAndWorkDate(employee, today)
                .orElseThrow(() -> new IllegalStateException("出勤記録が存在しません。先に出勤打刻してください。"));

        //勤務中以外退勤できない
        if(attendance.getWorkingStatus() != WorkingStatus.WORKING){
            throw new IllegalStateException("退勤できない状態です。");
        }

        //退勤時刻セット
        attendance.setWorkEnd(LocalDateTime.now().withNano(0));
        attendance.setWorkingStatus(WorkingStatus.FINISHED);

        //労務時間の計算
        long workedMinutes = Duration.between(attendance.getWorkStart(), attendance.getWorkEnd()).toMinutes();

        int breakMinutes = attendance.getBreakMinutes() == null ? 0 : attendance.getBreakMinutes();

        attendance.setTotalWorkMinutes((int) workedMinutes - breakMinutes);

        //DBに保存
        return attendanceRepository.save(attendance);
    }

    //休憩打刻
    public Attendance breakToggle(Long employeeId){

        //従業員データ取得
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("従業員が存在しません。"));

        Attendance attendance = attendanceRepository
                .findByEmployeeAndWorkDate(employee, today)
                .orElseThrow(() -> new IllegalStateException("本日の勤怠が存在しません。"));

        //出勤中→休憩開始
        if(attendance.getWorkingStatus() == WorkingStatus.WORKING){

            attendance.setBreakStart(LocalDateTime.now().withNano(0));
            attendance.setWorkingStatus(WorkingStatus.BREAK);

            return attendanceRepository.save(attendance);
        }

        //休憩中→休憩終了
        if(attendance.getWorkingStatus() == WorkingStatus.BREAK){

            LocalDateTime breakStart = attendance.getBreakStart();
            LocalDateTime breakEnd = LocalDateTime.now().withNano(0);

            if(breakStart == null){
                throw new IllegalStateException("休憩開始時刻が存在しません。");
            }

            long minutes = Duration
                    .between(breakStart, breakEnd)
                    .toMinutes();

            int total = attendance.getBreakMinutes() != null
                    ? attendance.getBreakMinutes()
                    : 0;

            attendance.setBreakEnd(breakEnd);
            attendance.setBreakMinutes(total + (int)minutes);
            attendance.setWorkingStatus(WorkingStatus.WORKING);

            return attendanceRepository.save(attendance);
        }

        throw new IllegalStateException("現在の状態では休憩操作できません。");
    }

    //今日の勤怠を取得
    public Attendance getTodayAttendance(Long employeeId) {

        //従業員データ取得
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("従業員が存在しません。"));

        return attendanceRepository
                .findByEmployeeAndWorkDate(employee, today)
                .orElse(null);

    }
}
