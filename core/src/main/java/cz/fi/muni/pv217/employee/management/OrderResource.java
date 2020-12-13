package cz.fi.muni.pv217.employee.management;

import cz.fi.muni.pv217.employee.management.entity.Order;
import cz.fi.muni.pv217.employee.management.service.OrderService;
import io.quarkus.panache.common.Parameters;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/order")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    OrderService orderService;
    
    @POST
    @Path("/create")
    //@RolesAllowed("Admin")
    public Order createOrder(Order order) {
        return orderService.createOrder(order);
    }

    @PUT
    @Path("/{id}/update")
    //@RolesAllowed("Admin")
    public Order updateOrder(@org.jboss.resteasy.annotations.jaxrs.PathParam long id, JsonObject jsonObject) {
        try {
            return orderService.updateOrder(id, jsonObject);
        } catch (IllegalArgumentException iae) {
            throw new ClientErrorException(iae.getMessage(), Response
                    .status(Response.Status.PRECONDITION_FAILED)
                    .entity(iae.getMessage())
                    .build());
        }
    }

    @DELETE
    @Path("/{id}/delete")
    //@RolesAllowed("Admin")
    public Response deleteOrder(@org.jboss.resteasy.annotations.jaxrs.PathParam long id) {
        Order order;

        try {
            order = orderService.deleteOrder(id);

            if (order == null) {
                return Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Cannot delete order with id " + id)
                        .build();
            }
        } catch (NotFoundException nfe) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(String.format("Order with id %d not found.", id))
                    .build();
        }

        return Response.ok(order).build();
    }

    @GET
    public List<Order> getOrders() {
        return Order.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getOrder(@org.jboss.resteasy.annotations.jaxrs.PathParam long id) {
        Order order = Order.findById(id);

        if (order == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String.format("Order for id %d not found.", id))
                    .build();
        }

        return Response.ok(order).build();
    }

    @GET
    @Path("/search")
    public List<Order> searchOrders(@QueryParam("search") String search) {
        return Order.list("name like :search or surname like :search or company like :search", Parameters.with("search", "%" + search + "%"));
    }
}
