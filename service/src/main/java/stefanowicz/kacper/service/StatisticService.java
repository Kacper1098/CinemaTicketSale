package stefanowicz.kacper.service;

import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.model.Customer;
import stefanowicz.kacper.model.Movie;
import stefanowicz.kacper.repository.CustomerRepository;
import stefanowicz.kacper.repository.MovieRepository;
import stefanowicz.kacper.repository.impl.CustomerRepositoryImpl;
import stefanowicz.kacper.repository.impl.MovieRepositoryImpl;

import java.util.List;
import java.util.Map;

public class StatisticService {
    private final MovieRepository movieRepository = new MovieRepositoryImpl();
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl();

    /*1. Podac liste gatunkow filmow posortowana wedlug ilosc biletow ktore na ten
         gatunek zakupiono - sortowanie malejaco*/
    /**
     *
     * @return Map with genre as key and quantity of bought tickets of this genre as value.
     */
    public Map<String, Long> genresGroupedByTicketsCount(){
        return movieRepository.genreGroupedByTicketsCount();
    }

    /*2. Podac imiona i nazwiska klientow, ktorzy obecnie nie otrzymali karty lojalnosciowej*/
    /**
     *
     * @return List of customers that never had loyalty card
     */
    public List<Customer> customersThatNeverHadLoyltyCard(){
        List<Customer> customers = customerRepository.customerThatNeverHadLoyaltyCard();
        if(customers.isEmpty()){
            throw new AppException("There are no customers without loyalty card");
        }
        return customers;
    }

    /*3. Podac nazwy tych filmow, dla ktorych bilet zakupiono co najmniej jeden raz
    w tym samym dniu co dzien premiery filmu*/
    /**
     *
     * @return List of movies on which ticket was bought, at least one time, in the day of release.
     */
    public List<Movie> ticketBoughtInDayOfRelease(){
        List<Movie> movies = movieRepository.ticketBoughtInDayOfRelease();
        if(movies.isEmpty()){
            throw new AppException("There are no movies on which ticket was bought in day of release");
        }
        return movies;
    }

    /*4. Podac liste filmow, ktore sa posortowane malejaco po sredniej cenie za bilet
         ( uwzgledniamy znizki )*/

    /**
     *
     * @return List of movies sorted by average ticket price, sorted in descending order.
     */
    public List<Movie> moviesSortedByAvgTicketPrice(){
        List<Movie> movies = movieRepository.getMoviesSortedByAvgTicketPrice();
        if(movies.isEmpty()){
            throw new AppException("There are no movies in database");
        }
        return movies;
    }


    /*5. Podac film, ktory byl najpopularniejszy oraz najmniej popularny*/

    /**
     *
     * @return Movie that is the most popular.
     */
    public Movie mostPopularMovie(){
        return movieRepository.mostPopularMovie().orElseThrow(() -> new AppException("Could not find most popular movie"));
    }

    /**
     *
     * @return Movie that is the least popular.
     */
    public Movie leastPopularMovie(){
        return movieRepository.leastPopularMovie().orElseThrow(() -> new AppException("Could not find least popular movie"));
    }


    /*6. Pogrupowac filmy przydzielajac kazdemu sredni wiek osob, ktore kupily na nie
        bilety*/
    /**
     *
     * @return Map with movie as key and customers average age that have bought ticket on that movie.
     */
    public Map<Movie, Integer> moviesWithCustomersAvgAge(){
        return movieRepository.getMoviesWithCustomersAvgAge();
    }
}
