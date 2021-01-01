package cz.fi.muni.pv217.employee.management.service;

import cz.fi.muni.pv217.employee.management.entity.Employee;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

@ApplicationScoped
public class EmployeeService {

    @Transactional
    public Employee createEmployee(Employee employee) {
        employee.persist();
        return employee;
    }

    @Transactional
    public Employee updateEmployee(long id, Employee changedEmployee) {
        Employee employee = Employee.findById(id);

        if (employee == null) {
            return null;
        }

        employee.name = changedEmployee.name;
        employee.surname = changedEmployee.surname;
        employee.dateOfBirth = changedEmployee.dateOfBirth;
        employee.insuranceCompany = changedEmployee.insuranceCompany;
        employee.mobile = changedEmployee.mobile;
        employee.address = changedEmployee.address;
        employee.startContract = changedEmployee.startContract;
        employee.endContract = changedEmployee.endContract;
        employee.hourlyWage = changedEmployee.hourlyWage;

        employee.persist();
        return employee;
    }

    @Transactional
    public Employee deleteEmployee(long id) {
        Employee employee = Employee.findById(id);

        if (employee == null) {
            throw new NotFoundException("Cannot find employee for id " + id);
        }

        boolean deleted = Employee.deleteById(id);
        return deleted ? employee : null;
    }
}
