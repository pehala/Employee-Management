package cz.fi.muni.pv217.employee.management.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;

@RegisterRestClient(configKey = "vacation-service")
public interface VacationRESTClient {

    @POST
    @Path("/snap/create")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    boolean hasVacation(@QueryParam("id") Long id, @QueryParam("date") LocalDate date);
}
