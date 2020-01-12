package stefanowicz.kacper.validator;

import stefanowicz.kacper.model.Movie;
import stefanowicz.kacper.validator.generic.AbstractValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class MovieValidator extends AbstractValidator<Movie> {
    @Override
    public Map<String, String> validate(Movie movie) {
        errors.clear();

        if(movie == null){
            errors.put("movieObject", "Movie object is not valid, it cannot be null");
            return errors;
        }

        if(!isMovieTitleValid(movie)){
            errors.put("movieTitle", "Movie title is not valid, it has to consists of letters and whitespaces only");
        }

        if(!isMovieGenreValid(movie)){
            errors.put("movieGenre", "Movie genre is not valid, it has to consists of letters and whitespaces only");
        }

        if(!isMoviePriceValid(movie)){
            errors.put("moviePrice", "Movie price is not valid, it has to be greater than zero.");
        }

        if(!isMovieDurationValid(movie)){
            errors.put("movieDuration", "Movie duration is not valid, it has to be greater than zero");
        }

        if(!isMovieReleaseDateValid(movie)){
            errors.put("movieReleaseDate", "Movie release date is not valid, it cannot be from the past");
        }

        return errors;
    }

    private boolean isMovieTitleValid(Movie movie){
        return movie.getTitle() != null && movie.getTitle().matches("[A-Z][A-Za-z_:,.\\s]+");
    }

    private boolean isMovieGenreValid(Movie movie){
        return  movie.getGenre() != null && movie.getGenre().matches("[A-Z][A-Za-z -]+");
    }

    private boolean isMoviePriceValid(Movie movie){
        return movie.getPrice() != null && movie.getPrice().compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean isMovieDurationValid(Movie movie){
        return movie.getDuration() > 0;
    }

    private boolean isMovieReleaseDateValid(Movie movie){
        return movie.getReleaseDate() != null && (movie.getReleaseDate().isEqual(LocalDate.now()) || movie.getReleaseDate().isAfter(LocalDate.now()));
    }
}
