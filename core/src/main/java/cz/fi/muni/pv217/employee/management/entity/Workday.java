package cz.fi.muni.pv217.employee.management.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
public class Workday extends PanacheEntity {
    @NotNull(message = "Date cannot be null")
    @Column(columnDefinition = "DATE")
    public LocalDate date;

    public Double hours;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @NotNull(message = "Employee cannot be null")
    public Employee employee;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @NotNull(message = "Order cannot be null")
    public Order order;
}
