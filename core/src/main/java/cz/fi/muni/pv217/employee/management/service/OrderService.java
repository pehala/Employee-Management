package cz.fi.muni.pv217.employee.management.service;

import cz.fi.muni.pv217.employee.management.entity.Order;
import cz.fi.muni.pv217.employee.management.enums.OrderState;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

@ApplicationScoped
public class OrderService {

    @Transactional
    public Order createOrder(Order order) {
        order.persist();
        return order;
    }

    @Transactional
    public Order updateOrder(long id, JsonObject updateJson) {
        Order order = Order.findById(id);

        if (order == null) {
            return null;
        }

        updateJson.entrySet().forEach(entry -> {
            String value = entry.getValue().toString().replace("\"", "");
            switch (entry.getKey()) {
                case "id":
                    order.id = Long.valueOf(value);
                    break;
                case "name":
                    order.name = value;
                    break;
                case "surname":
                    order.surname = value;
                    break;
                case "company":
                    order.company = value;
                    break;
                case "mobile":
                    order.mobile = value;
                    break;
                case "state":
                    order.state = OrderState.valueOf(value);
                    break;
                case "info":
                    order.info = value;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown property provided for update order: " + entry.getKey());
            }
        });

        order.persist();
        return order;
    }

    @Transactional
    public Order deleteOrder(long id) {
        Order order = Order.findById(id);

        if (order == null) {
            throw new NotFoundException("Cannot find order for id " + id);
        }

        boolean deleted = Order.deleteById(id);
        return deleted ? order : null;
    }
}
