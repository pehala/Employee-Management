package cz.fi.muni.pv217.employee.management.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Leave {
    public LocalDate date;
    public double hours;
    public boolean paid;
}
