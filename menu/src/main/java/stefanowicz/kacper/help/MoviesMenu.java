package stefanowicz.kacper.help;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import stefanowicz.kacper.repository.enums.MoviesSort;
import stefanowicz.kacper.model.Movie;
import stefanowicz.kacper.service.MovieService;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.repository.impl.MovieRepositoryImpl;
import stefanowicz.kacper.util.UserDataService;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@RequiredArgsConstructor
public class MoviesMenu {
    private final MovieService movieService;

    private int printMenu(){
        System.out.println("1. Add new movie.");
        System.out.println("2. Delete movie.");
        System.out.println("3. Update movie.");
        System.out.println("4. Search for movie. ");
        System.out.println("5. List all movies.");
        System.out.println("6. More options.");
        System.out.println("0. Go back.");
        return UserDataService.getInt("Choose an option: ");
    }

    public void mainMenu(){
        int option;
        do{
            System.out.println(" --------------------- ");
            System.out.println(" -- MOVIES MENU --");
            System.out.println(" --------------------- ");
            try{
                option = printMenu();
                switch (option){
                    case 1 -> add();
                    case 2 -> deleteMenu();
                    case 3 -> update();
                    case 4 -> search();
                    case 5 -> list();
                    case 6 -> moreOptionsMenu();
                    case 0 -> {return;}
                    default -> System.out.println("No such option!!");
                }
            }
            catch (AppException e){
                System.out.println("---------------------------------------");
                System.out.println("------------------ EXCEPTION ----------");
                System.out.println(e.getMessage());
                System.out.println("---------------------------------------");
            }
        }while(true);
    }

    private Map<String, String> getSearchCriteria(){
        System.out.println("Movies info. Press enter to skip criterium.");
        return Map.of(
                "title", UserDataService.getString("Title: "),
                "genre", UserDataService.getString("Genre: "),
                "duration", UserDataService.getString("Duration: "));
    }

    private void search(){
        List<Movie> movies = movieService.searchForMovies(getSearchCriteria());
        System.out.println(toJson(movies));
    }

    private void update(){
        Movie movieToUpdate = chooseMovieFromList(movieService.getAllMovies(), "Choose movie to update");
        List<Field> fieldsToUpdate = UserDataService.getFieldsToUpdate(movieToUpdate.getClass(), "Choose fields to update");
        fieldsToUpdate.forEach(field -> {
            field.setAccessible(true);
           try{
               if (String.class.equals(field.getType())) {
                   field.set(movieToUpdate, UserDataService.getString("Enter " + field.getName() + ": "));
               }
               else if (Integer.class.equals(field.getType())) {
                   field.set(movieToUpdate, UserDataService.getInt("Enter " + field.getName() + ": "));
               }
               else if (BigDecimal.class.equals(field.getType())) {
                   field.set(movieToUpdate, UserDataService.getBigDecimal("Enter " + field.getName() + ": "));
               }
               else if(LocalDate.class.equals(field.getType())){
                   field.set(movieToUpdate, UserDataService.getDate("Enter " + field.getName() + ": "));
               }
           }
           catch (Exception e){
               throw new AppException("Could not update movie - " + e.getMessage());
           }
        });
        movieService.updateMovie(movieToUpdate);
    }

    private void add(){
        movieService.addMovie(UserDataService.getString("Enter movie file path: "));
    }

    private void list() {
        List<Movie> movies = movieService.getAllMovies();
        listMovies(movies);
    }

    /**
     * DELETE MENU
     */
    private void deleteMenu(){
        int option;
        try{
            option = printDeleteMenu();
            switch (option){
                case 1 -> movieService.deleteMovie(movieIdBySearchCriteria());
                case 2 -> movieService.deleteMovie(
                        chooseMovieFromList(movieService.getAllMovies(), "Choose movie to delete: ").getId());
                case 0 -> {return;}
                default -> System.out.println("No such option!!");
            }
        }
        catch (Exception e){
           throw new AppException(e.getMessage());
        }

    }

    private int printDeleteMenu(){
        System.out.println("1. Search for movies.");
        System.out.println("2. Show all movies. ");
        System.out.println("0. Go back.");
        return UserDataService.getInt("Choose an option: ");
    }

    private int movieIdBySearchCriteria(){
        return chooseMovieFromList(movieService.searchForMovies(getSearchCriteria()), "Choose movie to delete: ").getId();
    }

    /**
     * MORE OPTIONS MENU
     */
    private int printMoreOptions(){
        System.out.println("1. Show sorted movies.");
        System.out.println("2. Show movies grouped by genre.");
        System.out.println("3. Show movies in given price range.");
        System.out.println("4. Show movies that will be released in given date range.");
        System.out.println("5. Go back");
        System.out.println("0. Exit");
        return UserDataService.getInt("Choose an option: ");
    }

    private void moreOptionsMenu(){
        int option;
        do{
            try{
                option = printMoreOptions();
                switch (option){
                    case 1 -> sortMovies();
                    case 2 -> groupByGenre();
                    case 3 -> showInPriceRange();
                    case 4 -> showInDateRange();
                    case 5 -> {return;}
                    case 0 -> {
                        UserDataService.close();
                        System.out.println("Have a nice day");
                        System.exit(0);
                    }
                }
            }
            catch (Exception e){
                throw new AppException(e.getMessage());
            }
        }while(true);
    }

    private void groupByGenre(){
        var groupedMovies = movieService.moviesGroupedByGenre();
        System.out.println(toJson(groupedMovies));
    }

    private void sortMovies(){
        MoviesSort moviesSort = UserDataService.getSortBy(MoviesSort.class);
        boolean descOrder = UserDataService.getBoolean("Descending order: ");

        List<Movie> sortedMovies = movieService.sortMovies(moviesSort, descOrder);
        System.out.println(toJson(sortedMovies));
    }

    private void showInPriceRange(){
        List<Movie> movies = movieService.moviesInGivenPriceRange(UserDataService.getBigDecimal("From price: "), UserDataService.getBigDecimal("To price: "));
        System.out.println(toJson(movies));
    }

    private void showInDateRange(){
        List<Movie> movies = movieService.moviesInGivenDateRange(UserDataService.getDate("From date: "), UserDataService.getDate("To date: "));
        System.out.println(toJson(movies));
    }

    /**
     *
     * ANCILLARY METHODS
     */
    private void listMovies(List<Movie> movies){
        var counter = new AtomicInteger(1);
        movies.forEach(movie -> {
            System.out.println(counter + ". " + toJson(movie));
            counter.incrementAndGet();
        });
    }

    private Movie chooseMovieFromList(List<Movie> movies, String messageToShow){
        var counter = new AtomicInteger(1);
        movies.forEach(movie ->{
            System.out.println( counter + ". " + toJson(movie));
            counter.incrementAndGet();
        });
        int idx = UserDataService.getInt(messageToShow);
        if(idx <= 0){
            throw new AppException("Movies index has to be greater than 0");
        }
        return movies.get( idx - 1);
    }

    private static <T> String toJson(T t){
        try{
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateSerializer()).setPrettyPrinting().create();
            return gson.toJson(t);
        }
        catch (Exception e){
            throw new AppException("to json conversion exception in menu service");
        }
    }
}
