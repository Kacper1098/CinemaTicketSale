package stefanowicz.kacper.help;

import lombok.RequiredArgsConstructor;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.model.Customer;
import stefanowicz.kacper.model.Movie;
import stefanowicz.kacper.model.SalesStand;
import stefanowicz.kacper.model.User;
import stefanowicz.kacper.service.*;
import stefanowicz.kacper.util.UserDataService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class UserMenu {
    private final CustomerService customerService;
    private final MovieService movieService;
    private final SalesStandService salesStandService;
    private final TicketHistoryService ticketHistoryService;
    private final LoginService loginService;

    private int printMenu() {
        System.out.println("1. New ticket.");
        System.out.println("2. Show available movies.");
        System.out.println("3. Search for movie.");
        System.out.println("4. Show my purchase history.");
        System.out.println("0. Exit.");
        return UserDataService.getInt("Choose an option: ");
    }

    public void mainMenu(User user){
        int option;
        do{
            System.out.println(" --------------------- ");
            System.out.println(" ----- MAIN MENU -----");
            System.out.println(" --------------------- ");
            try{
                option = printMenu();
                switch (option){
                    case 1 -> {
                        TicketSaleMenu ticketSaleMenu = new TicketSaleMenu(
                                customerService,
                                movieService,
                                salesStandService
                        );
                        ticketSaleMenu.newTicket(user.getIsAdmin(), user);
                    }
                    case 2 -> showMovies();
                    case 3 -> searchMovie();
                    case 4 -> showHistory(user);
                    case 0 -> {
                        UserDataService.close();
                        System.out.println("Have a nice day");
                        System.exit(0);
                    }
                    default -> System.out.println("No such option");
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

    private void showMovies(){
        Movie movie = chooseMovieFromList(movieService.getAllMovies(), "Choose movie to see more details: ");
        System.out.println(UserDataService.toJson(movie));
    }


    private Movie chooseMovieFromList(List<Movie> movies, String messageToShow){
        var counter = new AtomicInteger(1);
        movies.forEach(movie ->{
            System.out.println(
                    counter + ". " + movie.getTitle()
                            + " -> "
                            + (movie.getReleaseDate().isAfter(LocalDate.now()) ? "Coming Soon!" : "Available now!"));
            counter.incrementAndGet();
        });
        int idx = UserDataService.getInt(messageToShow);
        if(idx <= 0){
            throw new AppException("Customers index has to be greater than 0");
        }
        return movies.get( idx - 1);
    }

    private void searchMovie(){
        List<Movie> movies = movieService.searchForMovies(Map.of("title", UserDataService.getString("Enter movie title: ")));
        movies.forEach(movie -> System.out.println(UserDataService.toJson(movie)));
    }

    private void showHistory(User user){
        List<SalesStand> salesStands = ticketHistoryService.historyForUser(user);
        salesStands.forEach(salesStand -> System.out.println(UserDataService.toJson(salesStand)));
        ticketHistoryService.sendAsEmail(salesStands, loginService.getCustomerByUser(user).getEmail());
        System.out.println("Purchase history has been sent to your email!");
    }
}
