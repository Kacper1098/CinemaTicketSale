package stefanowicz.kacper.repository.impl;

import stefanowicz.kacper.model.User;
import stefanowicz.kacper.repository.UserRepository;
import stefanowicz.kacper.repository.generic.AbstractCrudRepository;

import java.util.Optional;

public class UserRepositoryImpl extends AbstractCrudRepository<User, Integer> implements UserRepository {
    @Override
    public Optional<User> getUserByUserName(String userName) {
        String SQL = "select * from user where user_name = :userName";

        return jdbi.withHandle(handle -> handle.createQuery(SQL).bind("userName", userName).mapToBean(User.class).findFirst());
    }

    @Override
    public Optional<User> getUserByCustomerID(Integer customerId) {
        String SQL = "select * from user where customer_id = :customerId";

        return jdbi.withHandle(handle -> handle.createQuery(SQL).bind("customerId", customerId).mapToBean(User.class).findFirst());
    }

}
