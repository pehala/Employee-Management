package cz.fi.muni.pv217.employee.management.service;

import cz.fi.muni.pv217.employee.management.client.VacationRESTClient;
import cz.fi.muni.pv217.employee.management.entity.DayWork;
import cz.fi.muni.pv217.employee.management.exception.VacationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

@ApplicationScoped
public class DayWorkService {
    @Inject
    @RestClient
    VacationRESTClient vacationRESTClient;

    @Transactional
    public DayWork createDayWork(DayWork dayWork) throws VacationException {
        boolean onVacation = vacationRESTClient.hasVacation(dayWork.employee.id, dayWork.date);
        if (onVacation) {
            throw new VacationException(String.format("Employee %d on vacation!", dayWork.employee.id));
        }
        dayWork.persist();
        return dayWork;
    }

    @Transactional
    public DayWork updateDayWork(long id, DayWork changedDayWork) {
        DayWork dayWork = DayWork.findById(id);

        if (dayWork == null) {
            return null;
        }

        dayWork = changedDayWork;
        dayWork.id = id;

        dayWork.persist();
        return dayWork;
    }

    @Transactional
    public DayWork deleteDayWork(long id) {
        DayWork dayWork = DayWork.findById(id);

        if (dayWork == null) {
            throw new NotFoundException("Cannot find dayWork for id " + id);
        }

        boolean deleted = DayWork.deleteById(id);
        return deleted ? dayWork : null;
    }
}
