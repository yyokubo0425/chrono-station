package com.chrono.station.controller.admin;

import com.chrono.station.application.admin.AttendanceApprovalUseCase;
import com.chrono.station.dto.admin.CorrectionRequestDetailDto;
import com.chrono.station.dto.admin.CorrectionRequestListDto;
import com.chrono.station.dto.admin.RejectRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/corrections")
@RequiredArgsConstructor
public class AttendanceApprovalController {

    private final AttendanceApprovalUseCase approvalUseCase;

    //修正申請を承認
    @PostMapping("/{correctionId}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long correctionId) {
        approvalUseCase.approve(correctionId);
        return ResponseEntity.ok().build();
    }

    //修正申請を却下
    @PostMapping("/api/{correctionId}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long correctionId, @RequestBody RejectRequestDto rejectRequestDto) {
        approvalUseCase.reject(correctionId, rejectRequestDto.getRejectReason());
        return ResponseEntity.ok().build();
    }

    //申請中一覧
    @GetMapping
    public List<CorrectionRequestListDto> getRequestedList() {
        return approvalUseCase.getAfterCorrections();
    }

    //申請中詳細
    @GetMapping("/{id}")
    public CorrectionRequestDetailDto getDetail(@PathVariable Long correctionId) {
        return approvalUseCase.getDetail(correctionId);
    }
}
