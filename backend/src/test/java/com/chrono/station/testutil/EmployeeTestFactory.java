package com.chrono.station.testutil;

import com.chrono.station.domain.employee.Employee;

public class EmployeeTestFactory {

    public static Employee create() {
        return Employee.builder()
                .employeeId(1L)
                .employeeName("山田太郎")
                .build();
    }
}
