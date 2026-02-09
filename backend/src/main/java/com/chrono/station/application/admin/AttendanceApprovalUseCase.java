package com.chrono.station.application.admin;

import com.chrono.station.domain.request.correction.AttendanceCorrection;
import com.chrono.station.domain.request.correction.AttendanceCorrectionRepository;
import com.chrono.station.domain.request.correction.CorrectionStatus;
import com.chrono.station.dto.admin.CorrectionRequestDetailDto;
import com.chrono.station.dto.admin.CorrectionRequestListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceApprovalUseCase {

    private final AttendanceCorrectionRepository correctionRepository;

    //承認
    @Transactional
    public void approve(Long correctionId){

        AttendanceCorrection correction = correctionRepository.findByCorrectionId(correctionId)
                .orElseThrow(() -> new IllegalArgumentException("修正申請が存在しません。"));

        //申請中以外は承認できない。
        if(correction.getStatus() != CorrectionStatus.REQUESTED){
            throw new IllegalStateException("申請中以外は承認できません。");
        }

        correction.approve();
    }

    //却下
    @Transactional
    public void reject(Long correctionId, String rejectReason){

        AttendanceCorrection correction = correctionRepository.findByCorrectionId(correctionId)
                .orElseThrow(() -> new IllegalArgumentException("修正申請が存在しません。"));

    if(correction.getStatus() != CorrectionStatus.REQUESTED){
        throw new IllegalStateException("申請中以外は却下できません。");
    }

    correction.reject(rejectReason);
    }

    //申請中一覧
    @Transactional(readOnly = true)
    public List<CorrectionRequestListDto> getAfterCorrections(){

        return correctionRepository
                .findByStatusOrderByCreatedAtAsc(CorrectionStatus.REQUESTED)
                .stream()
                .map(CorrectionRequestListDto::from)
                .toList();
    }

    //申請中詳細
    @Transactional(readOnly = true)
    public CorrectionRequestDetailDto getDetail(Long correctionId){

        AttendanceCorrection correction = correctionRepository.findByCorrectionId(correctionId)
                .orElseThrow(() -> new IllegalArgumentException("申請が存在しません。"));

        return CorrectionRequestDetailDto.from(correction);
    }
}
