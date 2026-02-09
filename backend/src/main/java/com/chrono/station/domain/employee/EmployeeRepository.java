package com.chrono.station.domain.employee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    //従業員取得
    Optional<Employee> findById(Long employeeId);
}
