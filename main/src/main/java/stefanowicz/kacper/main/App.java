package stefanowicz.kacper.main;

import stefanowicz.kacper.help.LoginService;
import stefanowicz.kacper.menu.MainMenu;
import stefanowicz.kacper.repository.*;
import stefanowicz.kacper.repository.impl.*;
import stefanowicz.kacper.service.*;


public class App {
    public static void main(String[] args) {
        MovieRepository movieRepository = new MovieRepositoryImpl();
        CustomerRepository customerRepository = new CustomerRepositoryImpl();
        LoyaltyCardRepository loyaltyCardRepository = new LoyaltyCardRepositoryImpl();
        SalesStandRepository salesStandRepository = new SalesStandRepositoryImpl();
        UserRepository userRepository = new UserRepositoryImpl();

        MovieService movieService = new MovieService(movieRepository);
        CustomerService customerService = new CustomerService(customerRepository);
        SalesStandService salesStandService = new SalesStandService(
                salesStandRepository,
                loyaltyCardRepository,
                customerRepository,
                movieRepository
        );
        StatisticService statisticService = new StatisticService();
        TicketHistoryService ticketHistoryService = new TicketHistoryService(
                salesStandRepository,
                customerRepository,
                movieRepository
        );
        LoginService loginService = new LoginService(customerRepository, userRepository);
        UserService userService = new UserService(userRepository);

        MainMenu menu = new MainMenu(
                movieService,
                customerService,
                salesStandService,
                statisticService,
                ticketHistoryService,
                loginService,
                userService
        );
        menu.mainMenu();
        /*OldMainMenu menu = new OldMainMenu(
                movieService,
                customerService,
                salesStandService,
                statisticService,
                ticketHistoryService
                );
        menu.mainMenu();*/
    }
}


