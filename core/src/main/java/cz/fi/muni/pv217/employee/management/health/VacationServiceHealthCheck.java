package cz.fi.muni.pv217.employee.management.health;

import cz.fi.muni.pv217.employee.management.service.VacationService;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Readiness
@ApplicationScoped
public class VacationServiceHealthCheck implements HealthCheck {
    @Inject
    @RestClient
    VacationService vacationService;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.builder()
                .name("Core service available");

        try {
            vacationService.callHealth();
            builder.up();
        } catch (Exception e) {
            builder.down()
                    .withData("exception message", e.getMessage());
        }

        return builder.build();
    }
}
