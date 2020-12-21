package cz.fi.muni.pv217.employee.management.service;

import cz.fi.muni.pv217.employee.management.client.VacationRESTClient;
import cz.fi.muni.pv217.employee.management.entity.Workday;
import cz.fi.muni.pv217.employee.management.exception.VacationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

@ApplicationScoped
public class WorkdayService {
    @Inject
    @RestClient
    VacationRESTClient vacationRESTClient;

    @Transactional
    public Workday createWorkday(Workday workday) throws VacationException {
        boolean onVacation = vacationRESTClient.hasVacation(workday.employee.id, workday.date);
        if (onVacation) {
            throw new VacationException(String.format("Employee %d on vacation!", workday.employee.id));
        }
        workday.persist();
        return workday;
    }

    @Transactional
    public Workday updateWorkday(long id, Workday changedWorkday) {
        Workday workday = Workday.findById(id);

        if (workday == null) {
            return null;
        }

        workday = changedWorkday;
        workday.id = id;

        workday.persist();
        return workday;
    }

    @Transactional
    public Workday deleteWorkday(long id) {
        Workday workday = Workday.findById(id);

        if (workday == null) {
            throw new NotFoundException("Cannot find workday for id " + id);
        }

        boolean deleted = Workday.deleteById(id);
        return deleted ? workday : null;
    }
}
