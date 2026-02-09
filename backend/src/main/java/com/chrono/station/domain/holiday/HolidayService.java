package com.chrono.station.domain.holiday;

import java.time.LocalDate;

public interface HolidayService {
    boolean isHoliday(LocalDate date);
}
