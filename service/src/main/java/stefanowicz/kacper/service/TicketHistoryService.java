package stefanowicz.kacper.service;

import lombok.RequiredArgsConstructor;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.help.EmailService;
import stefanowicz.kacper.model.Customer;
import stefanowicz.kacper.model.Movie;
import stefanowicz.kacper.model.SalesStand;
import stefanowicz.kacper.model.User;
import stefanowicz.kacper.repository.CustomerRepository;
import stefanowicz.kacper.repository.MovieRepository;
import stefanowicz.kacper.repository.SalesStandRepository;
import stefanowicz.kacper.util.UserDataService;

import java.util.List;

import static j2html.TagCreator.*;

@RequiredArgsConstructor
public class TicketHistoryService {
    private final SalesStandRepository salesStandRepository;
    private final CustomerRepository customerRepository;
    private final MovieRepository movieRepository;

    /**
     *
     * @return List of salesStand for given filters.
     */
    public boolean filteredHistory(){
        List<SalesStand> history = salesStandRepository
                .getFilteredSalesStand(
                        UserDataService.getDate("Date from: "),
                        UserDataService.getDate("Date to: "),
                        UserDataService.getInt("Duration: "),
                        UserDataService.getString("Genre: ")
                );
        if (history.isEmpty()){
            throw new AppException("Could find not any sale stand with given filters");
        }
        String recipient = UserDataService.getString("Enter email to which to send history: ");
        if(sendAsEmail(history, recipient)){
            System.out.println("Email with purchased ticket history was sent to -> " + recipient);
            return true;
        }
        else{
            throw new AppException("Error while sending email to " + recipient);
        }
    }

    /**
     *
     * @return List of all salesStands.
     */
    public boolean fullHistory(){
        List<SalesStand> history = salesStandRepository.findAll();
        if(history.isEmpty()){
            throw new AppException("Could not find any sales stands in database.");
        }
        String recipient = UserDataService.getString("Enter email to which to send history: ");
        if(sendAsEmail(history, recipient)){
            System.out.println("Email with purchased ticket history was sent to -> " + recipient);
            return true;
        }
        else{
            throw new AppException("Error while sending email to " + recipient);
        }
    }

    /**
     *
     * @param user
     * @return History of sales stand for user given as argument
     */
    public List<SalesStand> historyForUser(User user){
        if(user == null){
            throw new AppException("User given as argument is null!");
        }
        List<SalesStand> history = salesStandRepository.getCustomerHistory(user.getCustomerId());

        if(history.isEmpty()){
            throw new AppException("History for user : " + user + " is empty");
        }
        return history;
    }

    /**
     *
     * @param salesStands List of salestand to send
     * @param recipient Recipient email address
     * @return True if message was sent successfully, false otherwise
     */
    public boolean sendAsEmail(List<SalesStand> salesStands, String recipient){
        EmailService emailService = new EmailService();
        return emailService.send(recipient, "Purchased ticket history.", convertSalesStandsToHtml(salesStands));
    }

    /**
     *
     * @param salesStands List of salestand to convert into html
     * @return List of salestand converted to html format.
     */
    private String convertSalesStandsToHtml(List<SalesStand> salesStands){
        return table().with(
                thead(
                        tr().with(
                                th("ID"),
                                th("CustomerID"),
                                th("Name"),
                                th("Surname"),
                                th("Email"),
                                th("Age"),
                                th("MovieID"),
                                th("Title"),
                                th("Genre"),
                                th("Price"),
                                th("Duration"),
                                th("ReleaseDate"),
                                th("StartDateTime"),
                                th("Discount")
                        )
                ),
                tbody(
                        each(salesStands, salesStand -> tr(
                                td(salesStand.getId().toString()),
                                td(salesStand.getCustomerId() != null ? salesStand.getCustomerId().toString() : ""),
                                td(salesStand.getCustomerId() != null
                                        ? getCustomer(salesStand.getCustomerId()).getName()
                                        : ""),
                                td(salesStand.getCustomerId() != null
                                        ? getCustomer(salesStand.getCustomerId()).getSurname()
                                        : ""),
                                td(salesStand.getCustomerId() != null
                                        ? getCustomer(salesStand.getCustomerId()).getEmail()
                                        : ""),
                                td(salesStand.getCustomerId() != null
                                        ? getCustomer(salesStand.getCustomerId()).getAge().toString()
                                        : ""),
                                td(salesStand.getMovieId() != null ? salesStand.getMovieId().toString() : ""),
                                td(salesStand.getMovieId() != null
                                        ? getMovie(salesStand.getMovieId()).getTitle()
                                        : ""),
                                td(salesStand.getMovieId() != null
                                        ? getMovie(salesStand.getMovieId()).getGenre()
                                        : ""),
                                td(salesStand.getMovieId() != null
                                        ? getMovie(salesStand.getMovieId()).getPrice().toString()
                                        : ""),
                                td(salesStand.getMovieId() != null
                                        ? getMovie(salesStand.getMovieId()).getDuration().toString()
                                        : ""),
                                td(salesStand.getMovieId() != null
                                        ? getMovie(salesStand.getMovieId()).getReleaseDate().toString()
                                        : ""),
                                td(salesStand.getStartDateTime().toString()),
                                td(salesStand.getDiscount().toString() + "%"))
                        ))
                ).render();
    }

    private Customer getCustomer(Integer id){
        return customerRepository.findOne(id).orElseThrow(() -> new AppException("Could not find customer with given ID"));
    }

    private Movie getMovie(Integer id) {
        return movieRepository.findOne(id).orElseThrow(() -> new AppException("Could not find movie with given ID"));
    }
}
