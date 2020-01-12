package stefanowicz.kacper.menu;

import lombok.RequiredArgsConstructor;
import stefanowicz.kacper.help.AdminMenu;
import stefanowicz.kacper.help.LoginService;
import stefanowicz.kacper.help.UserMenu;
import stefanowicz.kacper.model.Customer;
import stefanowicz.kacper.model.User;
import stefanowicz.kacper.service.*;
import stefanowicz.kacper.util.UserDataService;

import java.io.Console;

@RequiredArgsConstructor
public class MainMenu {
    private final MovieService movieService;
    private final CustomerService customerService;
    private final SalesStandService salesStandService;
    private final StatisticService statisticService;
    private final TicketHistoryService ticketHistoryService;
    private final LoginService loginService;
    private final UserService userService;

    private int printMenu(){
        System.out.println("1. Sign in.");
        System.out.println("2. Create an account.");
        System.out.println("0. Exit.");

        return UserDataService.getInt("Choose an option: ");
    }
    public void mainMenu() {
        int option;
        do {
            System.out.println(" --------------------- ");
            System.out.println(" ----- MAIN MENU -----");
            System.out.println(" --------------------- ");
            try {
                option = printMenu();
                switch (option) {
                    case 1 -> {
                        signUser();
                    }
                    case 2 -> {
                        registerUser();
                    }
                    case 0 -> {
                        UserDataService.close();
                        System.out.println("Have a nice day");
                        return;
                    }
                    default -> System.out.println("No such option");
                }
            } catch (Exception e) {
                System.out.println("---------------------------------------");
                System.out.println("------------------ EXCEPTION ----------");
                System.out.println(e.getMessage());
                System.out.println("---------------------------------------");
            }
        } while (true);
    }

    private void registerUser(){
        Customer customer = loginService.createNewUser();

        System.out.println(UserDataService.toJson(customer));
    }

    private void signUser(){
        Console console = System.console();
        User user =  loginService.getSigningUser(UserDataService.getString("Enter username: "),
                console != null ? console.readPassword() : UserDataService.getString("Enter password: " ).toCharArray());
       // System.out.println( UserDataService.toJson(user));

        if(user.getIsAdmin()){
            AdminMenu adminMenu = new AdminMenu(
                    movieService,
                    customerService,
                    salesStandService,
                    statisticService,
                    ticketHistoryService,
                    userService
            );
            adminMenu.mainMenu(user);
        }
        else{
            UserMenu userMenu = new UserMenu(
                    customerService,
                    movieService,
                    salesStandService,
                    ticketHistoryService,
                    loginService
            );
            userMenu.mainMenu(user);
        }
    }
}
