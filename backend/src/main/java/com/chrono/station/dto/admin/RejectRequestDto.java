package com.chrono.station.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectRequestDto {
    private String rejectReason;

    public String getRejectReason(){
        return rejectReason;
    }
}
