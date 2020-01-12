package stefanowicz.kacper.repository;

import stefanowicz.kacper.model.User;
import stefanowicz.kacper.repository.generic.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> getUserByUserName(String userName);
    Optional<User> getUserByCustomerID(Integer customerId);
}
