package stefanowicz.kacper.repository.impl;

import org.jdbi.v3.core.generic.GenericType;
import stefanowicz.kacper.repository.enums.MoviesSort;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.model.Movie;
import stefanowicz.kacper.repository.MovieRepository;
import stefanowicz.kacper.repository.generic.AbstractCrudRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MovieRepositoryImpl extends AbstractCrudRepository<Movie, Integer> implements MovieRepository {
    @Override
    public List<Movie> getMoviesByCriteria(Map<String, String> criteria) {
        final String SQL = "select * from movie where " + getCriteriaFromMap(criteria);
        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(Movie.class).list());
    }

    @Override
    public Map<String, Long> genreGroupedByTicketsCount() {
        final String SQL = "select movie.genre, count(movie.id) as bought_tickets\n" +
                "from movie\n" +
                "inner join sales_stand on sales_stand.movie_id = movie.id\n" +
                "group by genre \n" +
                "order by bought_tickets desc";
        return jdbi.withHandle(handle ->
                handle.createQuery(SQL).mapToMap().list())
                .stream()
                .collect(Collectors.toMap(
                        m -> m.get("genre").toString(),
                        m -> (Long) m.get("bought_tickets")
                ));
    }

    @Override
    public List<Movie> ticketBoughtInDayOfRelease() {
        final String SQL = "select movie.*\n" +
                "from movie\n" +
                "inner join sales_stand on movie.id = sales_stand.movie_id\n" +
                "where date(sales_stand.start_date_time) = movie.release_date\n" +
                "group by movie.id";
        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(Movie.class).list());
    }

    @Override
    public List<Movie> getMoviesSortedByAvgTicketPrice() {
        final String SQL = "select movie.*\n" +
                "from movie\n" +
                "inner join sales_stand on sales_stand.movie_id = movie.id\n" +
                "group by movie.id\n" +
                "order by avg(movie.price - movie.price * sales_stand.discount/100) desc";

        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(Movie.class).list());
    }

    @Override
    public Optional<Movie> mostPopularMovie() {
        final String SQL = "select movie.*\n" +
                "from movie\n" +
                "inner join sales_stand on sales_stand.movie_id = movie.id\n" +
                "group by movie.id\n" +
                "order by count(movie.id) desc\n" +
                "limit 1";

        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(Movie.class).findFirst());
    }

    @Override
    public Optional<Movie> leastPopularMovie() {
        final String SQL = "select movie.*\n" +
                "from movie\n" +
                "inner join sales_stand on sales_stand.movie_id = movie.id\n" +
                "group by movie.id\n" +
                "order by count(movie.id) asc\n" +
                "limit 1";

        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(Movie.class).findFirst());
    }

    @Override
    public Map<Movie, Integer> getMoviesWithCustomersAvgAge() {
        final String SQL = "select movie.*, round(avg(customer.age)) as average_age\n" +
                "from movie\n" +
                "inner join sales_stand on sales_stand.movie_id = movie.id\n" +
                "inner join customer on sales_stand.customer_id = customer.id\n" +
                "group by movie.id";

        Map<Integer, Integer> map = jdbi.withHandle(handle -> handle
                .createQuery(SQL)
                .setMapKeyColumn("id")
                .setMapValueColumn("average_age")
                .collectInto(new GenericType<>() {}));

        return map
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> findOne(entry.getKey()).orElseThrow(() -> new AppException("Could not find movie with given id")),
                        Map.Entry::getValue
                ));
    }

    @Override
    public List<Movie> getSortedMovies(MoviesSort moviesSort, boolean descOrder) {
        final StringBuilder SQL = new StringBuilder("select * " +
                                    "from movie "+
                                    "order by ");
        switch (moviesSort){
            case GENRE -> SQL.append("genre");
            case DURATION -> SQL.append("duration");
            case RELEASE_DATE -> SQL.append("release_date");
            case TITLE -> SQL.append("title");
            case PRICE -> SQL.append("price");
        }
        if(descOrder){
            SQL.append(" desc");
        }
        return jdbi.withHandle(handle -> handle.createQuery(SQL.toString()).mapToBean(Movie.class).list());
    }

    @Override
    public List<Movie> getMoviesInPriceRange(BigDecimal fromPrice, BigDecimal toPrice) {
        final String SQL = "select * from movie where price between " + fromPrice.toString() + " and  " + toPrice.toString();
        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(Movie.class).list());
    }

    @Override
    public List<Movie> getMoviesInDateRange(LocalDate dateFrom, LocalDate dateTo) {
        final String SQL = "select * from movie where release_date between \""+ dateFrom + "\" and \"" + dateTo + "\"";
        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(Movie.class).list());
    }

    @Override
    public List<Movie> getReleasedMovies() {
        final String SQL = "select * from movie where release_date <= \"" + LocalDate.now() + "\"";
        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(Movie.class).list());
    }

    @Override
    public List<Movie> getMoviesComingOutInXDays(Integer x) {
        final String SQL = "select * from movie where release_date between \""
                + LocalDate.now()
                + "\" and \""
                + LocalDate.now().plusDays(x) + "\"";
        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(Movie.class).list());
    }

    @Override
    public boolean deleteMovieReferences(Integer id) {
        final String SQL =
                "update sales_stand " +
                        "set sales_stand.movie_id = null " +
                        "where sales_stand.movie_id = :id";
        return jdbi.withHandle(handle ->
                handle.createUpdate(SQL)
                        .bind("id",id)
                        .execute() >= 0);
    }

    private String getCriteriaFromMap(Map<String, String> criteria){
        return criteria
                .keySet()
                .stream()
                .filter(key -> !criteria.get(key).equals(""))
                .map(key -> key + "= \"" + criteria.get(key) +"\"")
                .collect(Collectors.joining(" and "));
    }
}
