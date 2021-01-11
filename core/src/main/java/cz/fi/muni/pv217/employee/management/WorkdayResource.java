package cz.fi.muni.pv217.employee.management;

import cz.fi.muni.pv217.employee.management.entity.Workday;
import cz.fi.muni.pv217.employee.management.exception.VacationException;
import cz.fi.muni.pv217.employee.management.service.WorkdayService;
import io.quarkus.panache.common.Parameters;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;

@Path("/api/workday")
@Authenticated
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkdayResource {

    @Inject
    WorkdayService workdayService;

    @POST
    @Path("/create")
    @Counted(name = "createdWorkdays", description = "How many workdays have been created.")
    public Response createWorkday(Workday workday) {
        try {
            return Response.ok(workdayService.createWorkday(workday)).build();
        } catch (VacationException ex) {
            return Response
                    .status(Response.Status.PRECONDITION_FAILED)
                    .entity(ex.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}/update")
    public Response updateWorkday(@PathParam("id") long id, Workday workday) {
        try {
            return Response.ok(workdayService.updateWorkday(id, workday)).build();
        } catch (NotFoundException ex) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(ex.getMessage())
                    .build();
        } catch (VacationException ex) {
            return Response
                    .status(Response.Status.PRECONDITION_FAILED)
                    .entity(ex.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}/delete")
    @RolesAllowed("admin")
    public Response deleteWorkday(@PathParam("id") long id) {
        Workday workday;

        try {
            workday = workdayService.deleteWorkday(id);

            if (workday == null) {
                return Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Cannot delete workday with id " + id)
                        .build();
            }
        } catch (NotFoundException nfe) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(String.format("Workday with id %d not found.", id))
                    .build();
        }

        return Response.ok(workday).build();
    }

    @GET
    @Counted(name = "showWorkdays")
    public List<Workday> getWorkdays() {
        return Workday.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getWorkday(@PathParam("id") long id) {
        Workday workday = Workday.findById(id);

        if (workday == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String.format("Workday for id %d not found.", id))
                    .build();
        }

        return Response.ok(workday).build();
    }

    @GET
    @Path("/employee/{id}/date")
    public List<Workday> getAllWorkdaysOfEmployeeInRange(@PathParam("id") long id, @QueryParam("from") String fromDate, @QueryParam("to") String toDate) {
        LocalDate from = (fromDate == null) ? LocalDate.MIN : LocalDate.parse(fromDate);
        LocalDate to = (toDate == null) ? LocalDate.now() : LocalDate.parse(toDate);

        return  Workday.list("employee.id=:id AND date BETWEEN :from AND :to",
                Parameters.with("id", id).and("from", from).and("to", to));
    }

    @GET
    @Path("/order/{id}")
    public List<Workday> getAllWorkdaysForOrder(@PathParam("id") long id) {
        return Workday.list("order.id", id);
    }
}
