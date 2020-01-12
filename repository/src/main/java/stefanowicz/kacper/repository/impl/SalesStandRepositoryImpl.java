package stefanowicz.kacper.repository.impl;

import stefanowicz.kacper.model.SalesStand;
import stefanowicz.kacper.repository.SalesStandRepository;
import stefanowicz.kacper.repository.generic.AbstractCrudRepository;

import java.time.LocalDate;
import java.util.List;

public class SalesStandRepositoryImpl extends AbstractCrudRepository<SalesStand, Integer> implements SalesStandRepository {
    @Override
    public Integer numberOfBoughtMovies(Integer customerId) {

        final String SQL = "select count(sales_stand.customer_id) \n" +
                "from sales_stand\n" +
                "where sales_stand.customer_id = " + customerId + ";";

        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapTo(Integer.class).first());
    }

    @Override
    public List<SalesStand> getFilteredSalesStand(LocalDate fromDate, LocalDate toDate, Integer duration, String genre) {
        final String SQL = "select sales_stand.*\n" +
                "from sales_stand\n" +
                "inner join movie on movie.id = sales_stand.movie_id\n" +
                "where (date(sales_stand.start_date_time) between \"" +  fromDate.toString() + "\" and \"" + toDate.toString() + "\") \n" +
                "and movie.duration = " + duration + " " +
                "and movie.genre = \""  + genre + "\";";

        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(SalesStand.class).list());
    }

    @Override
    public List<SalesStand> getCustomerHistory(Integer customerId) {
        final String SQL = "select * from sales_stand where customer_id = :customerId";

        return jdbi.withHandle(handle -> handle.createQuery(SQL).bind("customerId", customerId).mapToBean(SalesStand.class).list());
    }
}
