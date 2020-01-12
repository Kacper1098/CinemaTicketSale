package stefanowicz.kacper.help;

import lombok.RequiredArgsConstructor;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.repository.impl.CustomerRepositoryImpl;
import stefanowicz.kacper.repository.impl.MovieRepositoryImpl;
import stefanowicz.kacper.repository.impl.SalesStandRepositoryImpl;
import stefanowicz.kacper.service.TicketHistoryService;
import stefanowicz.kacper.util.UserDataService;

@RequiredArgsConstructor
public class TicketHistoryMenu {

    private final TicketHistoryService ticketHistoryService;

    private int printMenu(){
        System.out.println("1. Filter history");
        System.out.println("2. Full history");
        System.out.println("3. Go back");
        System.out.println("0. Exit");
        return UserDataService.getInt("Choose an option: ");
    }

    public void mainMenu(){
        int option;
        do{
            try{
                option = printMenu();
                switch (option){
                    case 1 -> ticketHistoryService.filteredHistory();
                    case 2 -> ticketHistoryService.fullHistory();
                    case 3 -> {return;}
                    case 0 -> {
                        UserDataService.close();
                        System.out.println("Have a nice day");
                        System.exit(0);
                    }
                    default -> System.out.println("There is no such option!!");
                }
            }
            catch (Exception e){
                throw new AppException(e.getMessage());
            }
        }while(true);
    }

}
