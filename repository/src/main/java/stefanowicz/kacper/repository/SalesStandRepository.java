package stefanowicz.kacper.repository;

import stefanowicz.kacper.model.SalesStand;
import stefanowicz.kacper.repository.generic.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SalesStandRepository extends CrudRepository<SalesStand, Integer> {
    Integer numberOfBoughtMovies(Integer customerId);

    List<SalesStand> getFilteredSalesStand(LocalDate fromDate, LocalDate toDate, Integer duration, String genre);

    List<SalesStand> getCustomerHistory(Integer customerId);
}
