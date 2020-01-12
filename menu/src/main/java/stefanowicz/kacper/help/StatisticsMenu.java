package stefanowicz.kacper.help;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.model.Customer;
import stefanowicz.kacper.model.Movie;
import stefanowicz.kacper.service.StatisticService;
import stefanowicz.kacper.util.UserDataService;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class StatisticsMenu {
    private final StatisticService statisticService;

    public void mainMenu(){
        int option;
        do {
            try{
                option = printStatisticsMenu();
                switch (option){
                    case 1 -> groupedGenres();
                    case 2 -> neverHadLoyltyCard();
                    case 3 -> boughtInDayOfRelease();
                    case 4 -> averageTicketPrice();
                    case 5 -> mostAndLeastPopularMovie();
                    case 6 -> moviesGroupedByAvgAge();
                    case 7 -> {return;}
                    case 0 -> {
                        System.out.println("Have a nice day");
                        System.exit(0);
                    }
                    default -> System.out.println("There is no such option!!");
                }
            }
            catch (Exception e){
                throw new AppException("Statistics menu exception " + e.getMessage());
            }
        }while(true);
    }

    private void groupedGenres(){
        var genres = statisticService.genresGroupedByTicketsCount();
        System.out.println(toJson(genres));
    }

    private void neverHadLoyltyCard(){
        List<Customer> customers = statisticService.customersThatNeverHadLoyltyCard();
        System.out.println(toJson(customers));
    }

    private void boughtInDayOfRelease(){
        List<Movie> movies = statisticService.ticketBoughtInDayOfRelease();
        System.out.println(toJson(movies));
    }

    private void averageTicketPrice(){
        List<Movie> movies = statisticService.moviesSortedByAvgTicketPrice();
        System.out.println(toJson(movies));
    }

    private void mostAndLeastPopularMovie(){
        Movie mostPopular = statisticService.mostPopularMovie();
        Movie leastPopular = statisticService.leastPopularMovie();
        System.out.println("--- MOST POPULAR MOVIE ---");
        System.out.println(toJson(mostPopular));
        System.out.println("--- LEAST POPULAR MOVIE ---");
        System.out.println(toJson(leastPopular));
    }

    private void moviesGroupedByAvgAge(){
        var map = statisticService.moviesWithCustomersAvgAge();
        map.forEach((movie, integer) -> System.out.println(toJson(movie) + " -> " + integer));
    }

    private int printStatisticsMenu(){
        System.out.println("1. Genre with quantity of bought tickets.");
        System.out.println("2. Customer that never had loyalty card.");
        System.out.println("3. Movies on which ticket was bought in day of release.");
        System.out.println("4. Movies sorted by average ticket price.");
        System.out.println("5. Most and least popular movie.");
        System.out.println("6. Movies grouped by average age of customers that bought ticket to that movie.");
        System.out.println("7. Go back");
        System.out.println("0. Exit");
        return UserDataService.getInt("Choose an option: ");
    }

    private <T> String toJson(T t){
        if(t == null){
            throw new AppException("ToJson exception - element is null");
        }
        try{
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                    .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                    .setPrettyPrinting()
                    .create();
            return gson.toJson(t);
        }
        catch (Exception e){
            throw new AppException("To json exception - " + e.getMessage());
        }
    }
}
