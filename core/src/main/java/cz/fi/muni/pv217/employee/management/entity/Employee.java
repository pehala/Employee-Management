package cz.fi.muni.pv217.employee.management.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Employee  extends PanacheEntity {
    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    public String username;

    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    public String name;

    @NotNull(message = "Surname cannot be null")
    @NotEmpty(message = "Surname cannot be empty")
    public String surname;

    @NotNull(message = "DateOfBirth cannot be null")
    public LocalDate dateOfBirth;

    @NotNull(message = "Insurance Company cannot be null")
    @NotEmpty(message = "Insurance Company cannot be empty")
    public String insuranceCompany;

    @NotNull(message = "Mobile cannot be null")
    @Pattern(message = "Mobile should be valid", regexp = "\\d{10}")
    public String mobile;

    @NotNull(message = "Address cannot be null")
    @NotEmpty(message = "Address cannot be empty")
    public String address;

    @NotNull(message = "Start contract date cannot be null")
    public LocalDate startContract;

    public LocalDate endContract;

    @NotNull(message = "Hourly wage cannot be null")
    public BigDecimal hourlyWage;

    public Employee() {
    }

    public Employee(String username, String name, String surname, LocalDate dateOfBirth, String insuranceCompany,
                    String mobile, String address, LocalDate startContract, BigDecimal hourlyWage) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.insuranceCompany = insuranceCompany;
        this.mobile = mobile;
        this.address = address;
        this.startContract = startContract;
        this.hourlyWage = hourlyWage;
    }
}
