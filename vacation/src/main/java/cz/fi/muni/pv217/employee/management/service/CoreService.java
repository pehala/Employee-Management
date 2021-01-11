package cz.fi.muni.pv217.employee.management.service;

import cz.fi.muni.pv217.employee.management.pojo.Employee;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api")
@ApplicationScoped
@RegisterRestClient(configKey="core-service")
@Produces(MediaType.APPLICATION_JSON)
@RegisterClientHeaders
public interface CoreService {

    @GET
    @Path("/employee/{id}")
    Employee getEmployee(@PathParam("id") long id);
}
