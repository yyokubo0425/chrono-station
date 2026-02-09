package com.chrono.station.domain.attendance;

import com.chrono.station.domain.employee.EmployeeRepository;
import com.chrono.station.domain.holiday.HolidayService;
import com.chrono.station.testutil.EmployeeTestFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class AttendanceServiceTest {

    @Test
    void 初回の出勤打刻が正常に保存される() {

        //Mockを作る
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        AttendanceRepository attendanceRepository = mock(AttendanceRepository.class);
        HolidayService holidayService = mock(HolidayService.class);

        //Serviceにモック注入
        AttendanceService service = new AttendanceService(employeeRepository,attendanceRepository, holidayService);

        Long employeeId = 10L;
        LocalDate today = LocalDate.now();

        //今日の勤怠が「存在しない」ことを表現
        when(attendanceRepository.findByEmployeeAndWorkDate(EmployeeTestFactory.create(), today))
                .thenReturn(Optional.empty());

        //save()がnullを返さないように設定
        when(attendanceRepository.save(any(Attendance.class)))
                .then(invocation -> invocation.getArgument(0));

        //テスト対象のメソッド
        Attendance attendance = service.clockIn(employeeId);

        //出勤時刻がセットされていることの確認
        assertNotNull(attendance.getWorkStart());

        //ステータスがWORKINGになっていることを確認
        assertEquals(WorkingStatus.WORKING, attendance.getWorkingStatus());

        //DBに保存するメソッドが１回だけ呼ばれたことを確認
        verify(attendanceRepository, times(1)).save(attendance);
    }

    @Test
    void 退勤打刻が正常に行われる() {

        //Mockを作る
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        AttendanceRepository attendanceRepository = mock(AttendanceRepository.class);
        HolidayService holidayService = mock(HolidayService.class);

        //Serviceにモック注入
        AttendanceService service = new AttendanceService(employeeRepository, attendanceRepository, holidayService);

        Long employeeId = 10L;
        LocalDate today = LocalDate.now();

        //疑似的な「出勤済みレコード」準備
        Attendance existing = Attendance.builder()
                .attendanceId(1L)
                .employee(EmployeeTestFactory.create())
                .workDate(today)
                .workStart(LocalDateTime.now().minusHours(8)) //8時間前に出勤
                .workingStatus(WorkingStatus.WORKING)
                .breakMinutes(60) //休憩時間1時間
                .build();

        //今日の勤怠が「存在する」と返却させる
        when(attendanceRepository.findByEmployeeAndWorkDate(EmployeeTestFactory.create(), today))
                .thenReturn(Optional.of(existing));

        //save()の戻り値をそのまま返す
        when(attendanceRepository.save(any(Attendance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //テスト対象メソッド
        Attendance attendance = service.clockOut(employeeId);

        //退勤時刻がセットされていること
        assertNotNull(attendance.getWorkEnd());

        //勤怠ステータスFINISHEDに変更されていること
        assertEquals(WorkingStatus.FINISHED, attendance.getWorkingStatus());

        //ダミーデータを基に労務時間が正しく計算されていること(8h - 1h = 7h = 420m)
        assertEquals(420, attendance.getTotalWorkMinutes());

        //saveが1回呼ばれたことを確認
        verify(attendanceRepository, times(1)).save(attendance);
    }
}
