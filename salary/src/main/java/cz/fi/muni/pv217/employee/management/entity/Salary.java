package cz.fi.muni.pv217.employee.management.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
public class Salary extends PanacheEntity {
    public String employee_name;
    public LocalDate from_date;
    public LocalDate to_date;
    public long worked_days;
    public long leave_days;
    public double salary;

    public Salary() {
    }

    public Salary(String employee_name, LocalDate from, LocalDate to, long worked_days, long leave_days, double salary) {
        this.employee_name = employee_name;
        this.from_date = from;
        this.to_date = to;
        this.worked_days = worked_days;
        this.leave_days = leave_days;
        this.salary = salary;
    }
}
