package cz.fi.muni.pv217.employee.management.entity;

import cz.fi.muni.pv217.employee.management.enums.OrderState;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "orders")
public class Order extends PanacheEntity {
    public String name;

    public String surname;

    public String company;

    @NotNull(message = "Mobile cannot be null")
    @Pattern(message = "Mobile should be valid", regexp = "\\d{10}")
    public String mobile;

    @NotNull(message = "State cannot be null")
    @Enumerated(EnumType.STRING)
    public OrderState state;

    @NotNull(message = "Info cannot be null")
    public String info;
}
