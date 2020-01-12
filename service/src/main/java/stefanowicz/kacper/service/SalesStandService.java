package stefanowicz.kacper.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.help.EmailService;
import stefanowicz.kacper.help.LocalDateSerializer;
import stefanowicz.kacper.help.LocalDateTimeSerializer;
import stefanowicz.kacper.model.Customer;
import stefanowicz.kacper.model.LoyaltyCard;
import stefanowicz.kacper.model.Movie;
import stefanowicz.kacper.model.SalesStand;
import stefanowicz.kacper.repository.CustomerRepository;
import stefanowicz.kacper.repository.LoyaltyCardRepository;
import stefanowicz.kacper.repository.MovieRepository;
import stefanowicz.kacper.repository.SalesStandRepository;
import stefanowicz.kacper.util.Config;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static j2html.TagCreator.*;

@RequiredArgsConstructor
public class SalesStandService {
    private final SalesStandRepository salesStandRepository;
    private final LoyaltyCardRepository loyaltyCardRepository;
    private final CustomerRepository customerRepository;
    private final MovieRepository movieRepository;

    public boolean addNewSaleStand(Customer customer, Movie movie, LocalDateTime startTime) {

        if (movie == null) {
            throw new AppException("SaleStandService exception - add salestand method - movie argument is null");
        }
        if(customer == null) {
            throw new AppException("SaleStandService exception - add salestand method - customer argument is null");
        }
        if(startTime == null){
            throw new AppException("SaleStandService exception - add salestand method - startTime argument is null");
        }

        return createSalesStand(customer, movie, startTime)
                .map(this::sendAsEmail)
                .orElseThrow(() -> new AppException("Could not send email with sale stand details."));
    }

    /**
     * @param customer
     * @param movie
     * @param startTime
     * @return Created sales stand for given arguments.
     */
    private Optional<SalesStand> createSalesStand(Customer customer, Movie movie, LocalDateTime startTime) {
        SalesStand salesStand = SalesStand
                .builder()
                .customerId(customer.getId())
                .movieId(movie.getId())
                .startDateTime(startTime)
                .build();
        Optional<LoyaltyCard> createdCard;

        if (customer.getLoyaltyCardId() == null) {
            salesStand.setDiscount(BigDecimal.ZERO);
            createdCard = createFirstLoyaltyCard(customer);
        }
        else {
            if (getCustomerLoyaltyCard(customer).getExpirationDate().isBefore(LocalDate.now())) {
                System.out.println("---- YOUR LOYALTY CARD IS OUT OF DATE ----");
                salesStand.setDiscount(BigDecimal.ZERO);
                createdCard = createFirstLoyaltyCard(customer);
            }
            else {
                salesStand.setDiscount(getCustomerLoyaltyCard(customer).getDiscount());

                createdCard = createNextLoyaltyCard(customer);

                if(createdCard.isPresent()){
                    createdCard.get().setCurrentMoviesNumber(createdCard.get().getCurrentMoviesNumber() + 1);
                    loyaltyCardRepository.update(createdCard.get());
                }
            }
        }

        if(createdCard.isPresent()){
            customer.setLoyaltyCardId(createdCard.get().getId());
            customerRepository.update(customer);
        }
        return salesStandRepository.add(salesStand);
    }

    /**
     *
     * @param customer
     * @return Loyalty card for customer given as argument.
     */
    private LoyaltyCard getCustomerLoyaltyCard(Customer customer){
        if(customer == null){
            throw new AppException("SalesStandService getCustomerLoyaltyCard method exception - customer given as argument is null");
        }
        return loyaltyCardRepository
                .findOne(customer.getLoyaltyCardId())
                .orElseThrow(() -> new AppException("Could not find loyalty card with given ID"));
    }

    /**
     *
     * @param customer
     * @return First customers loyalty card  if required conditions are fulfilled.
     */
    private Optional<LoyaltyCard> createFirstLoyaltyCard(Customer customer){
        if(salesStandRepository.numberOfBoughtMovies(customer.getId()) >= Config.boughtMoviesToGetFirstLoyaltyCard){
            System.out.println("--- THIS IS YOUR NEW LOYALTY CARD ---");
            LoyaltyCard loyaltyCard = LoyaltyCard
                    .builder()
                    .currentMoviesNumber(0)
                    .expirationDate(LocalDate.now().plusDays(Config.loyaltyCardPeriod))
                    .discount(BigDecimal.valueOf(Config.startingDiscount))
                    .moviesNumber(Config.startingMoviesNumber)
                    .build();
            System.out.println(toJson(loyaltyCard));

            return loyaltyCardRepository.add(loyaltyCard);
        }

        return Optional.empty();
    }

    /**
     *
     * @param customer
     * @return Next loyalty card for customer if required conditions are fulfilled.
     */
    private Optional<LoyaltyCard> createNextLoyaltyCard(Customer customer){
        LoyaltyCard oldLoyaltyCard = loyaltyCardRepository
                .findOne(customer.getLoyaltyCardId())
                .orElseThrow(() -> new AppException("Could not find LoyaltyCard with given ID"));

        if(oldLoyaltyCard.getCurrentMoviesNumber().equals(oldLoyaltyCard.getMoviesNumber())){
            LoyaltyCard newLoyaltyCard = LoyaltyCard
                    .builder()
                    .expirationDate(LocalDate.now().plusDays(Config.loyaltyCardPeriod))
                    .currentMoviesNumber(0)
                    .build();
            if(oldLoyaltyCard.getDiscount().equals(BigDecimal.valueOf(Config.maxLoyaltyCardDiscount))){
                System.out.println("--- YOU HAVE THE BIGGEST DISCOUNT ON LOYALTY CARD ---");

                newLoyaltyCard.setCurrentMoviesNumber(oldLoyaltyCard.getCurrentMoviesNumber());
                newLoyaltyCard.setDiscount(oldLoyaltyCard.getDiscount());

                System.out.println(toJson(newLoyaltyCard));
                return loyaltyCardRepository.add(newLoyaltyCard);
            }
            else{
                System.out.println("--- THIS IS YOUR NEW LOYALTY CARD ---");
                newLoyaltyCard.setDiscount(oldLoyaltyCard.getDiscount().add(BigDecimal.valueOf(5)));
                newLoyaltyCard.setMoviesNumber(oldLoyaltyCard.getMoviesNumber() + 1);

                System.out.println(toJson(newLoyaltyCard));
                return loyaltyCardRepository.add(newLoyaltyCard);
            }
        }
        return Optional.of(oldLoyaltyCard);
    }

    /**
     *
     * @param salesStand
     * @return True if email with given salestand was sent successfully, false otherwise
     */
    private boolean sendAsEmail(SalesStand salesStand){
        Customer customer = customerRepository.findOne(salesStand.getCustomerId()).orElseThrow(() -> new AppException("Could not find customer with given ID"));
        EmailService emailService = new EmailService();
        return emailService.send(customer.getEmail(), "Your ticket", convertSalesStandToHtml(salesStand));
    }

    /**
     *
     * @param salesStand
     * @return Converted salesstand in format ready to send as email
     */
    private String convertSalesStandToHtml(SalesStand salesStand){
        Movie movie = movieRepository.findOne(salesStand.getMovieId()).orElseThrow(() -> new AppException("Could not find movie with given ID"));
        return div(
               h2("Your ticket to \"" + movie.getTitle() + "\""),
                p("Start date: " + salesStand.getStartDateTime().toLocalDate()),
                p("Start time: " + salesStand.getStartDateTime().toLocalTime()),
                p("Price: " + calculatePrice( movie,  salesStand.getDiscount())),
                p("Duration: " + movie.getDuration())
        ).render();
    }

    /**
     *
     * @param movie
     * @param discount
     * @return Price of ticket to given movie after discount
     */
    private BigDecimal calculatePrice(Movie movie, BigDecimal discount){
        if(discount.equals(BigDecimal.ZERO)){
            return movie.getPrice();
        }
        return movie.getPrice().subtract(movie.getPrice().multiply(discount.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))).setScale(2, RoundingMode.HALF_UP);
    }

    private static <T> String toJson(T t) {
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                    .setPrettyPrinting().create();
            return gson.toJson(t);
        }
        catch (Exception e) {
            throw new AppException("to json conversion exception in menu service");
        }

    }



}
