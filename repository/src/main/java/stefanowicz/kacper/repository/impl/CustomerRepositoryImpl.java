package stefanowicz.kacper.repository.impl;

import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.model.Customer;
import stefanowicz.kacper.repository.CustomerRepository;
import stefanowicz.kacper.repository.generic.AbstractCrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomerRepositoryImpl extends AbstractCrudRepository<Customer, Integer> implements CustomerRepository {
    @Override
    public List<Customer> findByCriteria(Map<String, String> criteria) {
        final String SQL = "select * from customer where " + getCriteriaFromMap(criteria);
        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(Customer.class).list());
    }

    @Override
    public List<Customer> findyByName(String customerName) {
        final String SQL = "select * from customer where name = \"" + customerName + "\"";
        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(Customer.class).list());
    }

    @Override
    public List<Customer> customerInAgeRange(int fromAge, int toAge) {
        final String SQL = "select * from customer where age >= " + fromAge + " and age <= " + toAge;
        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(Customer.class).list());
    }

    @Override
    public List<Customer> customersWithValidLoyaltyCard() {
        final String SQL = "select customer.* from customer \n" +
                "inner join loyalty_card on loyalty_card.id = customer.loyalty_card_id\n" +
                "where customer.loyalty_card_id is not null  and loyalty_card.expiration_date >= now()" ;
        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(Customer.class).list());
    }

    @Override
    public Optional<Customer> findCustomer(String name, String surname, String email) {
        final String SQL = "select * from customer where name = \"" + name + "\" and surname = \"" + surname + "\" and email = \"" + email + "\"";
        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(Customer.class).findFirst());
    }

    @Override
    public List<Customer> customerThatNeverHadLoyaltyCard() {
        final String SQL = "select * from customer where customer.loyalty_card_id is null";
        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(Customer.class).list());
    }

    @Override
    public boolean deleteCustomerReferences(Integer id) {
        final String SQL =
                    "update sales_stand " +
                        "set sales_stand.customer_id = null " +
                        "where sales_stand.customer_id = :id;";
        final String SQL2 = "delete from user where customer_id = :id;";

        if(jdbi.withHandle(handle -> handle.createUpdate(SQL).bind("id",id).execute() < 0)){
            throw new AppException("Could not delete customer references from sales_stand table");
        }
        return jdbi.withHandle(handle -> handle.createUpdate(SQL2).bind("id",id).execute() >= 0);
    }

    private String getCriteriaFromMap(Map<String, String> criteria){
       return criteria
                .keySet()
                .stream()
                .filter(key -> !criteria.get(key).equals(""))
                .map(key -> key + "= \"" + criteria.get(key) +"\"")
                .collect(Collectors.joining(" and "));
    }
}
