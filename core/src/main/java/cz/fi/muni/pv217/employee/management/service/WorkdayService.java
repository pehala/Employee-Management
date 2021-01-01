package cz.fi.muni.pv217.employee.management.service;

import cz.fi.muni.pv217.employee.management.client.VacationRESTClient;
import cz.fi.muni.pv217.employee.management.entity.Workday;
import cz.fi.muni.pv217.employee.management.exception.VacationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class WorkdayService {
    @Inject
    @RestClient
    VacationRESTClient vacationRESTClient;

    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    @Transactional
    public Workday createWorkday(Workday workday) throws VacationException {
        boolean onVacation = vacationRESTClient.hasEmployeeVacationOnDate(workday.employee.id, workday.date.format(formatter));
        if (onVacation) {
            throw new VacationException(String.format("Employee %d on vacation!", workday.employee.id));
        }
        workday.persist();
        return workday;
    }

    @Transactional
    public Workday updateWorkday(long id, Workday changedWorkday) throws VacationException {
        Workday workday = Workday.findById(id);

        if (workday == null) {
            throw new NotFoundException("Cannot find workday for id " + id);
        }

        boolean onVacation = vacationRESTClient.hasEmployeeVacationOnDate(workday.employee.id, changedWorkday.date.format(formatter));
        if (onVacation) {
            throw new VacationException(String.format("Employee %d on vacation!", workday.employee.id));
        }

        workday.date = changedWorkday.date;
        workday.hours = changedWorkday.hours;
        workday.employee = changedWorkday.employee;
        workday.order = changedWorkday.order;

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
