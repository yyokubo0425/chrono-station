package com.chrono.station.application.auth;

import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public Long getEmployeeId() {
        return 1L;
    }
}

