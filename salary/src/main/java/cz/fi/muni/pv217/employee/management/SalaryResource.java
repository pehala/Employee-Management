package cz.fi.muni.pv217.employee.management;


import cz.fi.muni.pv217.employee.management.entity.Salary;
import cz.fi.muni.pv217.employee.management.pojo.Employee;
import cz.fi.muni.pv217.employee.management.pojo.Leave;
import cz.fi.muni.pv217.employee.management.pojo.Workday;
import cz.fi.muni.pv217.employee.management.service.CoreService;
import cz.fi.muni.pv217.employee.management.service.LeaveService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import net.bytebuddy.asm.Advice;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
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
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Path("/api/salaries")
@Authenticated
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Salaries", description = "Enables management of the salaries")
public class SalaryResource {

    @Inject
    @RestClient
    CoreService coreService;

    @Inject
    @RestClient
    LeaveService leaveService;

    @Inject
    SecurityIdentity identity;

    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    @GET
    @Path("{id}")
    @Transactional
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "404",
                            description = "Salary does not exists"),
                    @APIResponse(
                            responseCode = "401",
                            description = "You don't have permissions to access said salary"),
                    @APIResponse(
                            responseCode = "200",
                            description = "Particular salary.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Salary.class)))
            })
    @Counted(absolute = true, name = "salaries.get")
    public Salary get(@PathParam("id") long id) {
        Optional<Salary> optionalSalary = Salary.findByIdOptional(id);
        Salary salary = optionalSalary.orElseThrow(() -> new WebApplicationException("Salary with id " + id + " does not exist.", 404));
        if (identity.hasRole("admin")|| identity.getPrincipal().getName().equals(salary.employee_name)) {
            return salary;
        }
        throw new WebApplicationException("You dont have permission to access this resource", 403);
    }

    @GET
    @RolesAllowed("admin")
    @Transactional
    @Counted(absolute = true, name = "salaries.list")
    public List<Salary> getAll(@Context SecurityContext sec) {
        return Salary.listAll();
    }

    @GET
    @Path("employee")
    @Transactional
    @Counted(absolute = true, name = "salaries.listByEmployee")
    public List<Salary> getByEmployee() {
        return Salary.list("employee_name", identity.getPrincipal().getName());
    }

    @POST
    @Path("employee/{id}")
    @RolesAllowed("admin")
    @Transactional
    @Timed(description = "How long it takes to create salary",absolute = true, name = "salaries.create")
    public Response calculateSalary(@PathParam("id") long id,
                                    @QueryParam("from") Optional<String> optionalFrom,
                                    @QueryParam("to") Optional<String> optionalTo) {
        LocalDate from = optionalFrom.map(LocalDate::parse).orElse(LocalDate.now().withDayOfMonth(1));
        LocalDate to = optionalTo.map(LocalDate::parse).orElse(LocalDate.now());
        Employee employee = coreService.getEmployee(id);

        List<Workday> days = coreService.getAllWorkdaysOfEmployeeInPeriod(
                id,
                from.format(formatter),
                to.format(formatter)
        );
        List<Leave> leaves = leaveService.getLeaveInPeriod(
                id,
                from.format(formatter),
                to.format(formatter)
        );

        double worked_pay = days.stream()
                .mapToDouble(workday -> workday.hours)
                .sum() * employee.hourlyWage;

        double leave_pay = leaves.stream()
                .mapToDouble(leave -> leave.paid ? leave.hours : 0).sum() * employee.hourlyWage;

        long worked_days = days.stream()
                .map(workday -> workday.date)
                .distinct()
                .count();

        long leave_days = leaves.stream()
                .map(leave -> leave.date)
                .distinct()
                .count();

        Salary salary = new Salary(employee.username, from, to, worked_days, leave_days, worked_pay + leave_pay);
        salary.persist();
        return Response.ok(salary).build();
    }

}
