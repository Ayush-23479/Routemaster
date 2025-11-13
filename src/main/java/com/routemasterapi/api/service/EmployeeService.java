package com.routemasterapi.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.routemasterapi.api.entity.EmployeeEntity;
import com.routemasterapi.api.model.EmployeeIdRequest;
import com.routemasterapi.api.model.EmployeeRequestBody;
import com.routemasterapi.api.repositories.EmployeeRepositories;

@Service
public class EmployeeService {
	@Autowired
    private EmployeeRepositories employeeRepository;

    public EmployeeEntity createEmployee(EmployeeRequestBody employeeReqBody) {
        EmployeeEntity newEmployee = new EmployeeEntity();
        newEmployee.setFirstName(employeeReqBody.getFirstName());
        newEmployee.setLastName(employeeReqBody.getLastName());
        newEmployee.setDepartment(employeeReqBody.getDepartment());
        newEmployee.setManagerName(employeeReqBody.getManagerName());
        return employeeRepository.save(newEmployee);
    }

    public EmployeeEntity updateEmployee(EmployeeRequestBody employeeReqBody) {
        EmployeeEntity updatedEmployee = employeeRepository.findById(employeeReqBody.getEmpId()).orElse(null);
        if (updatedEmployee != null) {
            updatedEmployee.setFirstName(employeeReqBody.getFirstName());
            updatedEmployee.setLastName(employeeReqBody.getLastName());
            updatedEmployee.setDepartment(employeeReqBody.getDepartment());
            updatedEmployee.setManagerName(employeeReqBody.getManagerName());
            return employeeRepository.save(updatedEmployee);
        }
        return null; // Or throw an exception indicating employee not found
    }

    public Page<EmployeeEntity> listAllEmployeesFromDb(int pageNumber, int size) {
        Pageable pageable = PageRequest.of(pageNumber, size);
        return employeeRepository.findAllEmployees(pageable);
    }

    public String deleteEmployee(EmployeeIdRequest employeeIdReq) {
        int employeeId = employeeIdReq.getEmpId();
        if (employeeRepository.existsById(employeeId)) {
            employeeRepository.deleteById(employeeId);
            return "Employee record deleted successfully";
        } else {
            return "Employee record not found";
        }
    }
}
