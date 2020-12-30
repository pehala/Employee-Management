package cz.fi.muni.pv217.employee.management;

import cz.fi.muni.pv217.employee.management.entity.Vacation;
import cz.fi.muni.pv217.employee.management.pojo.Employee;
import cz.fi.muni.pv217.employee.management.service.CoreService;

import cz.fi.muni.pv217.employee.management.service.VacationService;
import io.quarkus.panache.common.Parameters;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.*;
import java.time.LocalDate;

@Path("/api/vacation")
@Authenticated
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Vacation", description = "Enables management of the vacations")
public class VacationResource {

    @Inject
    @RestClient
    CoreService coreService;

    @Inject
    VacationService vacationService;

    @Inject
    SecurityIdentity identity;

    @POST
    @Path("/create")
    @RolesAllowed("admin")
    @Counted(absolute = true, name = "vacation.create")
    public Vacation create(Vacation vacation) {
        return vacationService.createVacation(vacation);
    }

    @PUT
    @Path("/{id}/update")
    @RolesAllowed("admin")
    @Counted(absolute = true, name = "vacation.update")
    public Vacation update(@PathParam("id") long id, Vacation vacation) {
        return vacationService.updateVacation(id, vacation);
    }

    @DELETE
    @Path("/{id}/delete")
    @RolesAllowed("admin")
    @Counted(absolute = true, name = "vacation.delete")
    public Vacation delete(@PathParam("id") long id) {
        return vacationService.deleteVacation(id);
    }

    @GET
    @Path("{id}")
    @APIResponses(
        value = {
            @APIResponse(
                responseCode = "404",
                description = "Vacation does not exists"),
            @APIResponse(
                responseCode = "401",
                description = "You don't have permissions to access said vacation"),
            @APIResponse(
                responseCode = "200",
                description = "Particular vacation.",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Vacation.class)))})
    @Counted(absolute = true, name = "vacation.get")
    public Vacation get(@PathParam("id") long id) {
        Optional<Vacation> optionalVacation = Vacation.findByIdOptional(id);
        Vacation vacation = optionalVacation.orElseThrow(() ->
                new WebApplicationException("Vacation with id " + id + " does not exist.", 404));
        Employee employee = coreService.getEmployee(vacation.employee_id);
        if (!identity.hasRole("admin") && !identity.getPrincipal().getName().equals(employee.username)) {
            throw new WebApplicationException("You don't have permission to access this resource", 403);
        }
        return vacation;
    }

    @GET
    @RolesAllowed("admin")
    @Counted(absolute = true, name = "vacation.getAll")
    public List<Vacation> getAll(@Context SecurityContext sec) {
        return Vacation.listAll();
    }

    @GET
    @Path("employee/{id}")
    @Counted(absolute = true, name = "vacation.getByEmployee")
    public List<Vacation> getByEmployee(@PathParam("id") long id,
                                        @QueryParam("from") Optional<String> optionalFrom,
                                        @QueryParam("to") Optional<String> optionalTo) {
        LocalDate from = optionalFrom.map(LocalDate::parse).orElse(LocalDate.now().withDayOfMonth(1));
        LocalDate to = optionalTo.map(LocalDate::parse).orElse(LocalDate.now());

        List<Vacation> vacations = Vacation.list("employee_id", id);
        List<Vacation> result = new LinkedList<>();

        for (Vacation elem : vacations) {
            if (elem.date.isAfter(from) && elem.date.isBefore(to)) {
                result.add(elem);
            }
        }
        return result;
    }

    @GET
    @Path("employee/{id}/date")
    @Counted(absolute = true, name = "vacation.hasEmployeeVacationOnDate")
    public boolean hasEmployeeVacationOnDate(@PathParam("id") long id,
                                             @QueryParam("date") String date) {
        LocalDate targetDate = LocalDate.parse(date);
        List<Vacation> vacations = Vacation.list("employee_id", id);

        for (Vacation elem : vacations) {
            if (elem.date.equals(targetDate)) {
                return true;
            }
        }
        return false;
    }
}
