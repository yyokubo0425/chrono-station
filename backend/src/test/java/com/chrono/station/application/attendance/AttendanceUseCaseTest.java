package com.chrono.station.application.attendance;

import com.chrono.station.domain.attendance.Attendance;
import com.chrono.station.domain.attendance.AttendanceRepository;
import com.chrono.station.domain.request.correction.AttendanceCorrectionRepository;
import com.chrono.station.domain.request.correction.AttendanceCorrectionService;
import com.chrono.station.dto.request.correction.CorrectionRequestDto;
import com.chrono.station.testutil.AttendanceTestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AttendanceUseCaseTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Mock
    AttendanceRepository attendanceRepository;

    @Mock
    AttendanceCorrectionRepository attendanceCorrectionRepository;

    @Mock
    AttendanceCorrectionService correctionService;

    @Test
    void 打刻修正申請が成功すること() throws Exception {

        Attendance attendance =
                AttendanceTestFactory.create(LocalDate.of(2025, 12, 12));

        when(attendanceRepository.findById(1L))
                .thenReturn(Optional.of(attendance));

        when(attendanceCorrectionRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(correctionService).dataRequest(any());

        CorrectionRequestDto dto = new CorrectionRequestDto(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                60,
                "テスト修正申請"
        );

        mockMvc.perform(
                        post("/api/attendance/{attendanceId}/correction", attendance.getAttendanceId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk());
    }
}
