package cz.fi.muni.pv217.employee.management.service;

import cz.fi.muni.pv217.employee.management.pojo.Employee;
import cz.fi.muni.pv217.employee.management.pojo.Workday;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.List;

@Path("/api")
@RegisterRestClient(configKey="core-api")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public interface CoreService {

    @GET
    @Path("/workday/employee/{id}")
    List<Workday> getAllWorkdaysOfEmployeeInPeriod(@PathParam("id") long id,
                                                   @QueryParam("from") String fromDate,
                                                   @QueryParam("to") String toDate);
    @GET
    @Path("/employee/{id}")
    Employee getEmployee(@PathParam("id") long id);
}
