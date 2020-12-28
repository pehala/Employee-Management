package cz.fi.muni.pv217.employee.management.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {
    public String username;
    public double hourlyWage;
}