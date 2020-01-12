package stefanowicz.kacper.service;

import lombok.RequiredArgsConstructor;
import stefanowicz.kacper.converter.MovieJsonConverter;
import stefanowicz.kacper.repository.enums.MoviesSort;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.model.Movie;
import stefanowicz.kacper.repository.MovieRepository;
import stefanowicz.kacper.util.Config;
import stefanowicz.kacper.validator.MovieValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;

    /**
     *
     * @param movie Movie to validate
     * @return True if movie has valid format, false otherwise.
     */
    private boolean validateMovie(Movie movie){
        var movieValidator = new MovieValidator();
        var errors = movieValidator.validate(movie);
        if(movieValidator.hasErrors()){
            System.out.println("-------------------------------------");
            System.out.println("--- Validation error for movie ---");
            System.out.println("-------------------------------------");
            errors.forEach((k, v) -> System.out.println(k + ": " + v));
            return false;
        }
        return true;
    }

    /**
     *
     * @param movieFileName Filename where movie data is stored.
     * @return True if movie was added successfully, false otherwise.
     */
    public boolean addMovie(String movieFileName){
        if(movieFileName == null){
            throw new AppException("AddMovie method - movieFileName is null");
        }
        var movieConverter = new MovieJsonConverter(movieFileName);

        Movie movie =  movieConverter.fromJson().orElseThrow(() -> new AppException("Could not convert movie from json file"));

        if (!validateMovie(movie)) {
            return false;
        }
        movieRepository.add(movie);
        System.out.println("--- MOVIE ADDED SUCCESSFULLY ---");
        return true;
    }

    /**
     *
     * @param id ID of movie to delete.
     * @return True if movie was deleted successfully, false otherwise.
     */
    public boolean deleteMovie(int id){
        if(id <= 0){
            throw new AppException("DeleteMovie method exception - id has to be greater than 0");
        }
        if(movieRepository.deleteMovieReferences(id)){
            movieRepository.deleteOne(id);
            System.out.println("--- MOVIE DELETED SUCCESSFULLY ---");
            return true;
        }
        else{
            throw new AppException("Could not delete movie with id = " + id);
        }
    }


    /**
     *
     * @return List of all movies.
     */
    public List<Movie> getAllMovies(){
        List<Movie> movies = movieRepository.findAll();
        if(movies.isEmpty()){
            throw new AppException("There aren't any movies in data base");
        }
        return movies;
    }

    /**
     *
     * @param criteria Movie search criteria
     * @return List of movies that matches given criteria.
     */
    public List<Movie> searchForMovies(Map<String, String> criteria){
        if(criteria.isEmpty()){
            throw new AppException("Search criteria are empty!!");
        }
        List<Movie> movies = movieRepository.getMoviesByCriteria(criteria);
        if(movies.isEmpty()){
            throw new AppException("There are no movies with given criteria");
        }
        return movies;
    }

    /**
     *
     * @param movie New movie data
     * @return True if movie was updated successfully, false otherwise.
     */
    public boolean updateMovie(Movie movie){
        movieRepository.update(movie).orElseThrow(() -> new AppException("Could not update movie"));
        return true;
    }

    /**
     *
     * @param moviesSort Field to sort by
     * @param descOrder Sorting order
     * @return List of movies sorted by given argument and in order, dependent on descOrder argument.
     */
    public List<Movie> sortMovies(MoviesSort moviesSort, boolean descOrder){
        if(moviesSort == null) {
            throw new AppException("Movies sort by argument is null");
        }

        return movieRepository.getSortedMovies(moviesSort, descOrder);
    }

    /**
     *
     * @return Map with genre as key and list of movies in this genre.
     */
    public Map<String, List<Movie>> moviesGroupedByGenre(){
         return movieRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Movie::getGenre));
    }

    /**
     *
     * @param fromPrice
     * @param toPrice
     * @return List of movies that prices are in given range.
     */
    public List<Movie> moviesInGivenPriceRange(BigDecimal fromPrice, BigDecimal toPrice){
        if(fromPrice == null || toPrice == null){
            throw new AppException("Price arguments cannot be null");
        }
        if(fromPrice.compareTo(toPrice) > 0){
            throw new AppException("From price cannot be greater than to price");
        }

        return movieRepository.getMoviesInPriceRange(fromPrice, toPrice);
    }


    /**
     *
     * @param fromDate
     * @param toDate
     * @return List of movies that will be released in given date range.
     */
    public List<Movie> moviesInGivenDateRange(LocalDate fromDate, LocalDate toDate){
        if(fromDate == null || toDate == null){
            throw new AppException("Date arguments cannot be null");
        }
        if(fromDate.isAfter(toDate)){
            throw new AppException("From date cannot be greater than to date");
        }

        return movieRepository.getMoviesInDateRange(fromDate, toDate);
    }

    /**
     *
     * @return List of movies that have been released.
     */
    public List<Movie> getAvailableMovies(){
        return movieRepository.getReleasedMovies();
    }

    /**
     *
     * @return List of movies that will be released in X days.
     */
    public List<Movie> getComingSoonMovies(){
        return movieRepository.getMoviesComingOutInXDays(Config.upcomingMoviesDays);
    }
}
