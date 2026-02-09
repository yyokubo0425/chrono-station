package com.chrono.station.domain.attendance;

//現在の勤務状況
public enum WorkingStatus {
    NOT_STARTED,     //未出勤
    WORKING,         //出勤中
    BREAK,           //休憩中
    FINISHED,        //退勤済み
}