package cz.fi.muni.pv217.employee.management.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
public class Vacation extends PanacheEntity {
    public long employee_id;
    public LocalDate date;
    public double hours;
    public boolean paid;

    public Vacation() {}

    public Vacation(long employee_id, LocalDate date, double hours, boolean paid) {
        this.employee_id = employee_id;
        this.date = date;
        this.hours = hours;
        this.paid = paid;
    }
}
