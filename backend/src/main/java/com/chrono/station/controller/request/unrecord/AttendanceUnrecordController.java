package com.chrono.station.controller.request.unrecord;

import com.chrono.station.application.auth.CurrentUserProvider;
import com.chrono.station.application.request.unrecord.UnrecordUseCase;
import com.chrono.station.dto.request.correction.CorrectionRequestDto;
import com.chrono.station.dto.request.unrecord.UnrecordRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/attendance-unrecord")
@RequiredArgsConstructor
public class AttendanceUnrecordController {

    private final UnrecordUseCase unrecordUseCase;

    @PostMapping("/{workDate}")
    public void apply(
            @PathVariable LocalDate workDate,
            @RequestBody UnrecordRequestDto dto){
        unrecordUseCase.unrecordRequest(workDate, dto);
    }
}
