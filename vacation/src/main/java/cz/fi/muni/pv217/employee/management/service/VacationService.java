package cz.fi.muni.pv217.employee.management.service;

import cz.fi.muni.pv217.employee.management.entity.Vacation;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

@ApplicationScoped
public class VacationService {

    @Transactional
    public Vacation createVacation(Vacation vacation) {
        vacation.persist();
        return vacation;
    }

    @Transactional
    public Vacation updateVacation(long id, Vacation changedVacation) {
        Vacation vacation = Vacation.findById(id);

        if (vacation == null) {
            return null;
        }

        vacation = changedVacation;
        vacation.id = id;

        vacation.persist();
        return vacation;
    }

    @Transactional
    public Vacation deleteVacation(long id) {
        Vacation vacation = Vacation.findById(id);

        if (vacation == null) {
            throw new NotFoundException("Cannot find vacation for id " + id);
        }

        boolean deleted = Vacation.deleteById(id);
        return deleted ? vacation : null;
    }
}
