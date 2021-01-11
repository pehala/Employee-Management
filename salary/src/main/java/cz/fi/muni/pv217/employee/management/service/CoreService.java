package cz.fi.muni.pv217.employee.management.service;

import cz.fi.muni.pv217.employee.management.pojo.Employee;
import cz.fi.muni.pv217.employee.management.pojo.Workday;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api")
@ApplicationScoped
@RegisterRestClient(configKey="core-service")
@Produces(MediaType.APPLICATION_JSON)
@RegisterClientHeaders
public interface CoreService {

    @GET
    @Path("/workday/employee/{id}")
    @Authenticated
    List<Workday> getAllWorkdaysOfEmployeeInPeriod(@PathParam("id") long id,
                                                   @QueryParam("from") String fromDate,
                                                   @QueryParam("to") String toDate);
    @GET
    @Path("/employee/{id}")
    @Authenticated
    Employee getEmployee(@PathParam("id") long id);
}
