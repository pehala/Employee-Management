package cz.fi.muni.pv217.employee.management;

import cz.fi.muni.pv217.employee.management.entity.DayWork;
import cz.fi.muni.pv217.employee.management.exception.VacationException;
import cz.fi.muni.pv217.employee.management.service.DayWorkService;
import io.quarkus.panache.common.Parameters;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;

@Path("/work")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DayWorkResource {

    @Inject
    DayWorkService dayWorkService;

    @POST
    @Path("/create")
    public DayWork createDayWork(DayWork dayWork) throws VacationException {
        return dayWorkService.createDayWork(dayWork);
    }

    @PUT
    @Path("/{id}/update")
    public DayWork updateDayWork(@org.jboss.resteasy.annotations.jaxrs.PathParam long id, DayWork dayWork) {
        return dayWorkService.updateDayWork(id, dayWork);
    }

    @DELETE
    @Path("/{id}/delete")
    //@RolesAllowed("Admin")
    public Response deleteDayWork(@org.jboss.resteasy.annotations.jaxrs.PathParam long id) {
        DayWork dayWork;

        try {
            dayWork = dayWorkService.deleteDayWork(id);

            if (dayWork == null) {
                return Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Cannot delete dayWork with id " + id)
                        .build();
            }
        } catch (NotFoundException nfe) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(String.format("DayWork with id %d not found.", id))
                    .build();
        }

        return Response.ok(dayWork).build();
    }

    @GET
    public List<DayWork> getDayWorks() {
        return DayWork.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getDayWork(@org.jboss.resteasy.annotations.jaxrs.PathParam long id) {
        DayWork dayWork = DayWork.findById(id);

        if (dayWork == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String.format("DayWork for id %d not found.", id))
                    .build();
        }

        return Response.ok(dayWork).build();
    }

    // I tried call it with command `http :8080/work/employee/1?from=2010-01-01&to=2013-03-03` and todate was null. I don't understand why. Can you help me?
    @GET
    @Path("/employee/{id}")
    public List<DayWork> getAllDayWorksOfEmployeeInPeriod(@org.jboss.resteasy.annotations.jaxrs.PathParam long id, @QueryParam("from") String fromDate, @QueryParam("to") String toDate) {
        System.out.println(fromDate);
        System.out.println(toDate);

        return  DayWork.list("employee.id=:id AND date BETWEEN :from AND :to",
                Parameters.with("id", id).and("from", LocalDate.parse(fromDate)).and("to", LocalDate.parse(toDate)));
    }

    @GET
    @Path("/order/{id}")
    public List<DayWork> getAllDayWorksForOrder(@org.jboss.resteasy.annotations.jaxrs.PathParam long id) {
        return DayWork.list("order.id", id);
    }
}
