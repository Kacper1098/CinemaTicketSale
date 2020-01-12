package stefanowicz.kacper.service;


import lombok.RequiredArgsConstructor;
import stefanowicz.kacper.enums.CustomersSort;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.model.Customer;
import stefanowicz.kacper.model.User;
import stefanowicz.kacper.repository.CustomerRepository;
import stefanowicz.kacper.validator.CustomerValidator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    /**
     *
     * @param customer Customer to validate
     * @return True if customer data is valid, false otherwise.
     */
    public boolean validateCustomer(Customer customer){
        var customerValidator = new CustomerValidator();
        var errors = customerValidator.validate(customer);
        if(customerValidator.hasErrors()){
            System.out.println("-------------------------------------");
            System.out.println("--- Validation error for customer ---");
            System.out.println("-------------------------------------");
            errors.forEach((k, v) -> System.out.println(k + ": " + v));
            return false;
        }
        return true;
    }
    /**
     *
     * @param customer Customer to add
     * @return Returns true if customer was added successfully.
     */
    public boolean addCustomer(Customer customer){
        if (!validateCustomer(customer)){
            return false;
        }
        customerRepository.add(customer);
        System.out.println("--- CUSTOMER ADDED SUCCESSFULLY ---");
        return true;
    }

    /**
     *
     * @param id Id of customer to delete
     * @return Returns true if customer was deleted successfully.
     */
    public boolean deleteCustomer(int id){
        if(id <= 0){
            throw new AppException("DeleteCustomer method exception - id has to be greater than 0");
        }
        if(customerRepository.deleteCustomerReferences(id)){
            customerRepository.deleteOne(id);
            System.out.println("--- CUSTOMER DELETED SUCCESSFULLY ---");
            return true;
        }
        else{
            throw new AppException("Could not delete customer with id = " + id);
        }
    }

    /**
     *
     * @param criteria Customer search criteria
     * @return List of customers that matches given criteria.
     */
    public List<Customer> searchForCustomers(Map<String, String> criteria){
        if(criteria.isEmpty()){
            throw new AppException("Search criteria are empty!!");
        }
        List<Customer> customers = customerRepository.findByCriteria(criteria);
        if(customers.isEmpty()){
            throw new AppException("There are no customers that matches given criteria.");
        }
        return customers;
    }

    /**
     *
     * @return List of all customers.
     */
    public List<Customer> getAllCustomers(){
        List<Customer> customers = customerRepository.findAll();
        if(customers.isEmpty()){
            throw new AppException("There aren't any customers in database");
        }
        return customers;
    }

    /**
     *
     * @param customer Customer to update
     * @return ID if customers data was updated successfully
     */
    public int updateCustomer(Customer customer){
        if(!validateCustomer(customer)){
            throw new AppException("New customer data is not valid.");
        }

        return customerRepository.update(customer).orElseThrow(() -> new AppException("Could not update customer")).getId();
    }

    /**
     *
     * @param id Customers id
     * @return Customer with given id as argument.
     */
    public Customer getCustomer(int id){
        if(id <= 0){
            throw new AppException("Illegal ID value, it has to be greater than zero");
        }
        return customerRepository.findOne(id).orElseThrow(() -> new AppException("Could not find customer with given ID"));
    }

    /**
     *
     * @param customersSort Argument to sort by
     * @param descendingOrder Sorting order
     * @return List of cars sorted by given customersSort argument and in order, dependent on descendingOrder argument.
     */
    public List<Customer> sortCustomers(CustomersSort customersSort, boolean descendingOrder){
        if(customersSort == null){
            throw new AppException("CustomersSort argument is null");
        }

        List<Customer> customers = customerRepository.findAll();

        if(customers.isEmpty()){
            throw new AppException("There aren't any customers in database");
        }

        Stream<Customer> customerStream = switch (customersSort){
            case AGE -> customers
                    .stream()
                    .sorted(Comparator.comparing(Customer::getAge));
            case ID -> customers
                    .stream()
                    .sorted(Comparator.comparing(Customer::getId));
            case NAME -> customers
                    .stream()
                    .sorted(Comparator.comparing(Customer::getName));
            case SURNAME -> customers
                    .stream()
                    .sorted(Comparator.comparing(Customer::getSurname));
        };

        customers = customerStream.collect(Collectors.toList());

        if(descendingOrder){
            Collections.reverse(customers);
        }
        return customers;
    }

    /**
     *
     * @param customerName
     * @return List of customers with given name.
     */
    public List<Customer> findCustomersByName(String customerName){
        if(customerName == null){
            throw new AppException("Find customer by name method - customer name is null");
        }
        List<Customer> customers = customerRepository.findyByName(customerName);
        if(customers.isEmpty()){
            throw new AppException("Could not find customer with given name");
        }
        return customers;
    }


    /**
     *
     * @param fromAge
     * @param toAge
     * @return List of customers which are in given age range.
     */
    public List<Customer> findInAgeRange(int fromAge, int toAge){
        if(fromAge <= 0 || toAge <= 0){
            throw new AppException("Age has to be greater than zero");
        }
        if(fromAge > toAge){
            throw new AppException("From age has to be less than to age");
        }

        List<Customer> customers = customerRepository.customerInAgeRange(fromAge, toAge);

        if(customers.isEmpty()){
            throw new AppException("Could not find any customers in given age range");
        }
        return customers;
    }

    /**
     *
     * @return List of customers with valid loyalty card.
     */
    public List<Customer> getCustomersWithValidLoyaltyCard(){
        List<Customer> customers = customerRepository.customersWithValidLoyaltyCard();
        if(customers.isEmpty()){
            throw new AppException("There are no customers with valid loyalty card");
        }
        return customers;
    }

    /**
     *
     * @param name Customers name
     * @param surname Customers surname
     * @param email Customers email
     * @return Customer that matches values given as arguments, throws exception otherwise.
     */
    public Customer getCustomerFromDb(String name, String surname, String email){
        if(name == null || surname == null || email == null){
            throw new AppException("Arguments cannot be null");
        }
        return customerRepository.findCustomer(name, surname,email).orElseThrow(() -> new AppException("Could not find customer with given arguments"));
    }
}
