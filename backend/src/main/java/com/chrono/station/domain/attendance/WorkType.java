package com.chrono.station.domain.attendance;

//勤務区分
public enum WorkType {
    NONE("―"),                 //ー
    NORMAL("通常"),             //通常
    HOLIDAY_WORK("休出"),       //休日出勤
    ABSENT("欠勤"),             //欠勤
    PAID_LEAVE("有給");         //有給

    private final String label;

    WorkType(String label){
        this.label = label;
    }

    public String getLabel(){
        return label;
    }
}
