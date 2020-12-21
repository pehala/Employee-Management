package cz.fi.muni.pv217.employee.management;

import cz.fi.muni.pv217.employee.management.entity.Workday;
import cz.fi.muni.pv217.employee.management.exception.VacationException;
import cz.fi.muni.pv217.employee.management.service.WorkdayService;
import io.quarkus.panache.common.Parameters;
import org.eclipse.microprofile.metrics.annotation.Counted;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/api/workday")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkdayResource {

    @Inject
    WorkdayService workdayService;

    @POST
    @Path("/create")
    @RolesAllowed({"admin","user"})
    @Counted(name = "createdWorkdays", description = "How many workdays have been created.")
    public Workday createWorkday(Workday workday) throws VacationException {
        return workdayService.createWorkday(workday);
    }

    @PUT
    @Path("/{id}/update")
    @RolesAllowed({"admin","user"})
    public Workday updateWorkday(@PathParam long id, Workday workday) {
        return workdayService.updateWorkday(id, workday);
    }

    @DELETE
    @Path("/{id}/delete")
    @RolesAllowed("admin")
    public Response deleteWorkday(@PathParam long id) {
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
    @RolesAllowed("user")
    public List<Workday> getWorkdays() {
        return Workday.listAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("user")
    public Response getWorkday(@PathParam long id) {
        Workday workday = Workday.findById(id);

        if (workday == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String.format("Workday for id %d not found.", id))
                    .build();
        }

        return Response.ok(workday).build();
    }

    // I tried call it with command `http :8080/api/workday/employee/1?from=2010-01-01&to=2013-03-03` and todate was null. I don't understand why. Can you help me?
    @GET
    @Path("/employee/{id}")
    @RolesAllowed("user")
    public List<Workday> getAllWorkdaysOfEmployeeFromDate(@PathParam long id, @NotNull @QueryParam("from") String fromDate) {

        return  Workday.list("employee.id=:id AND date BETWEEN :from AND :to",
                Parameters.with("id", id).and("from", LocalDate.parse(fromDate)).and("to", LocalDate.now()));
    }

    @GET
    @Path("/order/{id}")
    @RolesAllowed("user")
    public List<Workday> getAllWorkdaysForOrder(@PathParam long id) {
        return Workday.list("order.id", id);
    }
}
