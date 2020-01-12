package stefanowicz.kacper.help;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.model.Customer;
import stefanowicz.kacper.model.Movie;
import stefanowicz.kacper.model.User;
import stefanowicz.kacper.service.CustomerService;
import stefanowicz.kacper.service.MovieService;
import stefanowicz.kacper.service.SalesStandService;
import stefanowicz.kacper.util.UserDataService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class TicketSaleMenu {
    private final CustomerService customerService;
    private final MovieService movieService;
    private final SalesStandService salesStandService;
    private int forOtherUser(){
        System.out.println("1. Buy ticket for myself. ");
        System.out.println("2. Buy ticket for another customer. ");
        System.out.println("0. Go back. ");

        return UserDataService.getInt("Choose an option: ");
    }
    public void newTicket(boolean isAdmin, User user) {
        Customer customer = customerService.getCustomer(user.getCustomerId());
        if(isAdmin){
            try{
                switch (forOtherUser()){
                    case 1 ->
                            customer = customerService.getCustomer(user.getCustomerId());
                    case 2 -> {
                        System.out.println("Enter customer details");
                        customer = customerService.getCustomerFromDb(
                                UserDataService.getString("Name: "),
                                UserDataService.getString("Surname: "),
                                UserDataService.getString("Email: "));
                    }
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("No such option");
                }
            }
            catch (AppException e) {
                throw new AppException(e.getMessage());
            }
        }

        Optional<Movie> chosenMovie = showAvailableMovies();
        if(chosenMovie.isEmpty()){
            return;
        }
        Optional<LocalDateTime> startTime = selectStartTime(chosenMovie.get());
        if(startTime.isEmpty()){
            return;
        }

        if(salesStandService.addNewSaleStand(customer,chosenMovie.get(), startTime.get())){
            System.out.println("--- TICKET BOUGHT SUCCESSFULLY ---");
            System.out.println("--- EMAIL WITH DETAILS WAS SENT TO " + customer.getEmail() + " ---");
        }
        else{
            throw new AppException("Unexpected error occured when purchasing a ticket ");
        }
    }

    private List<LocalDateTime> getAvailableShowingTimes(LocalDateTime localDateTime){
        List<LocalDateTime> dateTimes = new ArrayList<>();
        LocalDateTime fullHourTime = LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), localDateTime.getHour() + 1, 0);
        while (fullHourTime.getHour() <= 22 && fullHourTime.getMinute() <= 30){
            dateTimes.add(fullHourTime);
            fullHourTime = fullHourTime.plusMinutes(30);
        }

        return dateTimes;
    }

    private Optional<LocalDateTime> selectStartTime(Movie movie){
        System.out.println("-- MOVIE DETAILS --");
        System.out.println(toJson(movie));

        var counter = new AtomicInteger(0);
        List<LocalDateTime> availableShowingTimes = getAvailableShowingTimes(LocalDateTime.now());
        availableShowingTimes.forEach(time -> System.out.println(counter.incrementAndGet() + ". " + time.getHour() + ":" + (time.getMinute() == 0 ? "00" : time.getMinute())));
        int choice = UserDataService.getInt("Choose movie start time: ");

        if(choice < 0 || choice > availableShowingTimes.size()){
            throw new AppException("There is no option with given value!!");
        }
        else if(choice == 0){
            return Optional.empty();
        }

        return Optional.of(availableShowingTimes.get(choice - 1));
    }

    private Optional<Movie> showAvailableMovies(){
        List<Movie> movies = movieService.getAvailableMovies();
        List<Movie> comingSoonMovies = movieService.getComingSoonMovies();
        var counter = new AtomicInteger(0);

        System.out.println("---- AVAILABLE MOVIES ---");
        movies
                .forEach(movie -> System.out.println(counter.incrementAndGet() + ". " + movie.getTitle()));

        System.out.println("0. Go back");
        System.out.println("\n---- COMING SOON ----");
        comingSoonMovies.forEach(movie -> System.out.println(movie.getTitle() + " -> " + movie.getReleaseDate()));

        int choice = UserDataService.getInt("Choose movie: ");
        if(choice < 0 ||choice > movies.size()){
            throw new AppException("There is no such movie with given value");
        }
        else if(choice == 0){
            return Optional.empty();
        }

        return Optional.of(movies.get(choice - 1));
    }

    private static <T> String toJson(T t){
        try{
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                    .setPrettyPrinting().create();
            return gson.toJson(t);
        }
        catch (Exception e){
            throw new AppException("to json conversion exception in menu service");
        }
    }
}
