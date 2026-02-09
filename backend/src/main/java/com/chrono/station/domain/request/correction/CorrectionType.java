package com.chrono.station.domain.request.correction;

public enum CorrectionType {

    TIME_RECORD("打刻修正"),
    TIME_UNRECORD("打刻漏れ");

    private final String label;

    CorrectionType(String label) {
        this.label = label;
    }

    public String getLabel(){
        return label;
    }
}
