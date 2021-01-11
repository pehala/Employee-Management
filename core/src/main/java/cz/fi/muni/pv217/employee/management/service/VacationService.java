package cz.fi.muni.pv217.employee.management.service;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@RegisterRestClient(configKey = "vacation-service")
@RegisterClientHeaders
public interface VacationService {

    @GET
    @Path("/api/vacation/employee/{id}/date")
    @Produces(MediaType.APPLICATION_JSON)
    boolean hasEmployeeVacationOnDate(@PathParam("id") long id, @QueryParam("date") String date);

    @GET
    @Path("/health")
    void callHealth();
}
