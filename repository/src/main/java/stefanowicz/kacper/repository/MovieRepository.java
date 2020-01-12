package stefanowicz.kacper.repository;

import stefanowicz.kacper.repository.enums.MoviesSort;
import stefanowicz.kacper.model.Movie;
import stefanowicz.kacper.repository.generic.CrudRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MovieRepository extends CrudRepository<Movie, Integer> {
    List<Movie> getMoviesByCriteria(Map<String, String> criteria);
    Map<String, Long> genreGroupedByTicketsCount();
    List<Movie> ticketBoughtInDayOfRelease();
    List<Movie> getMoviesSortedByAvgTicketPrice();
    Optional<Movie> mostPopularMovie();
    Optional<Movie> leastPopularMovie();
    Map<Movie, Integer> getMoviesWithCustomersAvgAge();
    List<Movie> getSortedMovies(MoviesSort moviesSort, boolean descOrder);
    List<Movie> getMoviesInPriceRange(BigDecimal fromPrice, BigDecimal toPrice);
    List<Movie> getMoviesInDateRange(LocalDate dateFrom, LocalDate dateTo);
    List<Movie> getReleasedMovies();
    List<Movie> getMoviesComingOutInXDays(Integer x);
    boolean deleteMovieReferences(Integer id);
}
