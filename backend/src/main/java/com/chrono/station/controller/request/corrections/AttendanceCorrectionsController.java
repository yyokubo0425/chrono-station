package com.chrono.station.controller.request.corrections;

import com.chrono.station.application.auth.CurrentUserProvider;
import com.chrono.station.application.request.corrections.CorrectionsUseCase;
import com.chrono.station.dto.request.correction.AttendanceDailyDto;
import com.chrono.station.dto.request.correction.CorrectionRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/attendance-corrections")
@RequiredArgsConstructor
public class AttendanceCorrectionsController {

    private final CorrectionsUseCase correctionsUseCase;
    private final CurrentUserProvider currentUserProvider;

    //申請一覧
//    @GetMapping("")

    //申請詳細
//    @GetMapping("{id}")


    //勤怠1件取得(打刻修正申請画面用)
    @GetMapping("/{attendanceId}")
    public AttendanceDailyDto attendanceCorrection(
            @PathVariable Long attendanceId
    ){
        Long employeeId = currentUserProvider.getEmployeeId();
        return correctionsUseCase.getForCorrection(attendanceId,employeeId);
    }

    //修正内容申請
    @PostMapping("/{attendanceId}/correction")
    public void requestCorrection(
            @PathVariable Long attendanceId, @RequestBody CorrectionRequestDto dto
    ) {
        correctionsUseCase.requestCorrection(attendanceId, dto);
    }
}
