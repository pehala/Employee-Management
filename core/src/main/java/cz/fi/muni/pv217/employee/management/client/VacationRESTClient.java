package cz.fi.muni.pv217.employee.management.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/vacation")
@RegisterRestClient(configKey = "vacation-service")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public interface VacationRESTClient {

    @GET
    @Path("/employee/{id}/date")
    boolean hasEmployeeVacationOnDate(@PathParam("id") long id, @QueryParam("date") String date);
}
