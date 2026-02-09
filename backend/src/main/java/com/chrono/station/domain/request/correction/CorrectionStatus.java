package com.chrono.station.domain.request.correction;

public enum CorrectionStatus {
    REQUESTED("申請中"),
    APPROVED("承認済"),
    REJECTED("不承認"),
    CANCELLED("取消");

    private final String label;

    CorrectionStatus(String label){
        this.label = label;
    }

    public String getLabel(){
        return label;
    }
}
