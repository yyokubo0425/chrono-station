package com.chrono.station.domain.holiday;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class WeekendHolidayService implements HolidayService  {

    //休日判定
    @Override
    public boolean isHoliday(LocalDate date){
        DayOfWeek dow = date.getDayOfWeek();
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
    }
}
