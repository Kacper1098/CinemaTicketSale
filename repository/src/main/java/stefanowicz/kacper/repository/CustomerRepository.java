package stefanowicz.kacper.repository;

import stefanowicz.kacper.model.Customer;
import stefanowicz.kacper.repository.generic.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {
    List<Customer> findByCriteria(Map<String, String> criteria);
    List<Customer> findyByName(String customerName);
    List<Customer> customerInAgeRange(int fromAge, int toAge);
    List<Customer> customersWithValidLoyaltyCard();
    Optional<Customer> findCustomer(String name, String surname, String email);
    List<Customer> customerThatNeverHadLoyaltyCard();
    boolean deleteCustomerReferences(Integer id);

}
