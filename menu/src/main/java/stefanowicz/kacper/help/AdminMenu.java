package stefanowicz.kacper.help;

import lombok.RequiredArgsConstructor;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.model.User;
import stefanowicz.kacper.service.*;
import stefanowicz.kacper.util.UserDataService;

@RequiredArgsConstructor
public class AdminMenu {
    private final MovieService movieService;
    private final CustomerService customerService;
    private final SalesStandService salesStandService;
    private final StatisticService statisticService;
    private final TicketHistoryService ticketHistoryService;
    private final UserService userService;

    private int printMenu() {
        System.out.println("1. Manage customers.");
        System.out.println("2. Manage movies.");
        System.out.println("3. Purchased ticket history.");
        System.out.println("4. Statistics.");
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
                        CustomersMenu customersMenu = new CustomersMenu(customerService, userService);
                        customersMenu.mainMenu();
                    }
                    case 2 -> {
                        MoviesMenu moviesMenu =  new MoviesMenu(movieService);
                        moviesMenu.mainMenu();
                    }
                    case 3 -> {
                        TicketHistoryMenu ticketHistoryMenu = new TicketHistoryMenu(ticketHistoryService);
                        ticketHistoryMenu.mainMenu();
                    }
                    case 4 ->{
                        StatisticsMenu statisticsMenu = new StatisticsMenu(statisticService);
                        statisticsMenu.mainMenu();
                    }
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
}
