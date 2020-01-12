package stefanowicz.kacper.help;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import stefanowicz.kacper.model.User;
import stefanowicz.kacper.service.CustomerService;
import stefanowicz.kacper.enums.CustomersSort;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.model.Customer;
import stefanowicz.kacper.service.UserService;
import stefanowicz.kacper.util.UserDataService;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class CustomersMenu {
    private final CustomerService customerService;
    private final UserService userService;

    private int printMenu(){
        System.out.println("1. Delete customer.");
        System.out.println("2. Update customer.");
        System.out.println("3. Search for customer by ID. ");
        System.out.println("4. List all customers.");
        System.out.println("5. Give admin permissions. ");
        System.out.println("6. More options.");
        System.out.println("0. Go back.");
        return UserDataService.getInt("Choose an option: ");
    }

    public void mainMenu(){
        int option;
        do{
            System.out.println(" --------------------- ");
            System.out.println(" -- CUSTOMERS MENU --");
            System.out.println(" --------------------- ");
            try{
                option = printMenu();
                switch (option){
                    case 1 -> deleteMenu();
                    case 2 -> updateCustomer();
                    case 3 -> searchCustomerById();
                    case 4 -> listCustomers();
                    case 5 -> giveAdmin();
                    case 6 -> moreOptionsMenu();
                    case 0 -> {return;}
                    default -> System.out.println("No such option!!");
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

/*
    ADD CUSTOMER
    private void addCustomer(){
        System.out.println("Enter customer's data.");
        customerService.addCustomer(
                Customer
                        .builder()
                        .name(UserDataService.getString("Name: "))
                        .surname(UserDataService.getString("Surname: "))
                        .age(UserDataService.getInt("Age: "))
                        .email(UserDataService.getString("Email: "))
                        .build());
    }*/

    private void listCustomers(){
        var counter = new AtomicInteger(1);
        List<Customer> customers = customerService.getAllCustomers();
        customers.forEach(customer ->{
            System.out.println( counter + ". " + toJson(customer));
            counter.incrementAndGet();
        });
    }


    /**
     * CUSTOMERS DELETE MENU
     */
    private void deleteMenu(){
        int option;
        try{
            option = printDeleteMenu();
            switch (option){
                case 1 -> customerService.deleteCustomer(customerIdBySearchCriteria());
                case 2 -> customerService.deleteCustomer(
                        chooseCustomerFromList(customerService.getAllCustomers(), "Choose customer to delete: ").getId());
                case 0 -> {return;}
                default -> System.out.println("No such option!!");
            }
        }
        catch (Exception e){
            throw new AppException(e.getMessage());
        }

    }

    private int customerIdBySearchCriteria(){
        return chooseCustomerFromList(customerService.searchForCustomers(getSearchCriteria()), "Choose customer to delete: ").getId();
    }

    private Map<String, String> getSearchCriteria(){
        System.out.println("Customers info. Press enter to skip criterium.");
        return Map.of(
                "name", UserDataService.getString("Name: ").toLowerCase(),
                "surname", UserDataService.getString("Surname: ").toLowerCase(),
                "age", UserDataService.getString("Age: ")
        );
    }

    private int printDeleteMenu(){
        System.out.println("1. Search customers.");
        System.out.println("2. Show all customers. ");
        System.out.println("0. Go back.");
        return UserDataService.getInt("Choose an option: ");
    }

    /**
     *  UPDATE CUSTOMER
     */
        private void updateCustomer(){
            Customer customerToUpdate = chooseCustomerFromList(customerService.getAllCustomers(), "Choose customer to update: ");
            List<Field> fieldsToUpdate = UserDataService.getFieldsToUpdate(customerToUpdate.getClass(), "Choose fields to update");

            fieldsToUpdate.forEach(field -> {
                field.setAccessible(true);
                try{
                    if (String.class.equals(field.getType())) {
                        field.set(customerToUpdate, UserDataService.getString("Enter " + field.getName() + ": "));
                    }
                    else if (Integer.class.equals(field.getType()) || int.class.equals(field.getType())) {
                        field.set(customerToUpdate, UserDataService.getInt("Enter " + field.getName() + ": "));
                    }
                }
                catch (Exception e){
                    throw new AppException("Could not update movie - " + e.getMessage());
                }
            });
            customerService.updateCustomer(customerToUpdate);
        }

    /**
     * MORE OPTIONS FOR CUSTOMERS
     */
    private int printMoreOptionsMenu(){
        System.out.println("1. Show sorted customers. ");
        System.out.println("2. Show customers with given name. ");
        System.out.println("3. Show customers in given age group. ");
        System.out.println("4. Show customers with valid loyalty card. ");
        System.out.println("5. Go back.");
        System.out.println("0. Exit. ");
        return UserDataService.getInt("Choose an option: " );
    }

    private void moreOptionsMenu(){
        int option;
        do{
            try{
                option = printMoreOptionsMenu();
                switch (option){
                    case 1 -> sortCustomers();
                    case 2 -> findByName();
                    case 3 -> getFromAgeRange();
                    case 4 -> getWithValidLoyaltyCard();
                    case 5 -> {return;}
                    case 0 -> {
                        UserDataService.close();
                        System.out.println("Have a nice day");
                        System.exit(0);
                    }
                    default -> System.out.println("No such option!!!");
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

    private void getWithValidLoyaltyCard(){
        List<Customer> customers = customerService.getCustomersWithValidLoyaltyCard();
        System.out.println(toJson(customers));
    }

    private void getFromAgeRange(){
        List<Customer> customers = customerService.findInAgeRange(UserDataService.getInt("From age: "), UserDataService.getInt("To age: "));
        System.out.println(toJson(customers));
    }

    private void findByName(){
        List<Customer> customers = customerService.findCustomersByName(UserDataService.getString("Enter name: "));
        System.out.println(toJson(customers));
    }

    private void sortCustomers(){
        CustomersSort customersSort = UserDataService.getSortBy(CustomersSort.class);
        boolean descending = UserDataService.getBoolean("Descending order");
        List<Customer> sortedCustomers = customerService.sortCustomers(customersSort, descending);
        System.out.println(toJson(sortedCustomers));
    }

    private void searchCustomerById(){
        Customer customer = customerService.getCustomer(UserDataService.getInt("Enter customers ID: " ));
        System.out.println(toJson(customer));
    }

    /**
     *
     * ANCILLARY METHODS
     */
    private Customer chooseCustomerFromList(List<Customer> customers, String messageToShow){
        var counter = new AtomicInteger(1);
        customers.forEach(customer ->{
            System.out.println( counter + ". " + toJson(customer));
            counter.incrementAndGet();
        });
        int idx = UserDataService.getInt(messageToShow);
        if(idx <= 0){
            throw new AppException("Customers index has to be greater than 0");
        }
        return customers.get( idx - 1);
    }


    private void giveAdmin(){
        Customer customer  = chooseCustomerFromList(customerService.getAllCustomers(), "Choose customer: ");
        User user = userService.findUserByCustomerID(customer.getId());
        if(user.getIsAdmin()){
            throw new AppException("This user already has admin permissions!");
        }
        else{
            user.setIsAdmin(true);
            if(!userService.updateUser(user)){
               throw new AppException("Error occured while givin admin permissions to user with id: " + user.getId());
            }
            else{
                System.out.println("Admin permissions for user: " + UserDataService.toJson(user) + " has been granted succesfully");
            }
        }
    }

    private static <T> String toJson(T t){
        try{
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateSerializer()).create();
            return gson.toJson(t);
        }
        catch (Exception e){
            throw new AppException("to json conversion exception in menu service");
        }
    }
}
