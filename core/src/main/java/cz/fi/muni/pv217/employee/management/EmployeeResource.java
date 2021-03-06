package cz.fi.muni.pv217.employee.management;

import cz.fi.muni.pv217.employee.management.entity.Employee;
import cz.fi.muni.pv217.employee.management.service.EmployeeService;
import io.quarkus.panache.common.Parameters;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;



@Path("/api/employee")
@Authenticated
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeeResource {
    
    @Inject
    EmployeeService employeeService;

    @Inject
    SecurityIdentity identity;

    @POST
    @Path("/create")
    @RolesAllowed("admin")
    public Employee createEmployee(Employee employee) {
        return employeeService.createEmployee(employee);
    }

    @PUT
    @Path("/{id}/update")
    @RolesAllowed("admin")
    public Employee updateEmployee(@PathParam("id") long id, Employee employee) {
        return employeeService.updateEmployee(id, employee);
    }

    @DELETE
    @Path("/{id}/delete")
    @RolesAllowed("admin")
    public Response deleteEmployee(@PathParam("id") long id) {
        Employee employee;

        try {
            employee = employeeService.deleteEmployee(id);

            if (employee == null) {
                return Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Cannot delete employee with id " + id)
                        .build();
            }
        } catch (NotFoundException nfe) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(String.format("Employee with id %d not found.", id))
                    .build();
        }

        return Response.ok(employee).build();
    }

    @GET
    @RolesAllowed("admin")
    public List<Employee> getEmployees() {
        return Employee.listAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response getEmployee(@PathParam("id") long id) {
        Employee employee = Employee.findById(id);

        if (employee == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String.format("Employee for id %d not found.", id))
                    .build();
        }

        return Response.ok(employee).build();
    }

    @GET
    @Path("/search")
    @RolesAllowed("admin")
    public List<Employee> searchEmployees(@QueryParam("search") String search) {
        return Employee.list("name like :search or surname like :search or username like :search", Parameters.with("search", "%" + search + "%"));
    }

    @GET
    @Path("/me")
    public Response getMe() {
        Employee employee = Employee.find("username", identity.getPrincipal().getName()).firstResult();

        if (employee == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Employee for you not found.")
                    .build();
        }

        return Response.ok(employee).build();
    }
}
