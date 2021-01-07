package cz.fi.muni.pv217.employee.management.service;

import cz.fi.muni.pv217.employee.management.pojo.Leave;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
@RegisterRestClient(configKey="vacation-service")
public interface VacationService {

    @GET
    @Path("api/vacation/employee/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    List<Leave> getLeaveInPeriod(@PathParam("id") long id,
                                 @QueryParam("from") String fromDate,
                                 @QueryParam("to") String toDate);
}
