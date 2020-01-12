package stefanowicz.kacper.service;

import lombok.RequiredArgsConstructor;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.model.User;
import stefanowicz.kacper.repository.UserRepository;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findUserByCustomerID(Integer customerId){
        if(customerId == null){
            throw new AppException("Customer id cannot be null");
        }

        return userRepository.getUserByCustomerID(customerId).orElseThrow(() -> new AppException("Could not find user with customer id = " + customerId));
    }
    public boolean updateUser(User user){
        if(user == null){
            throw new AppException("User to update cannot be null");
        }
        return userRepository.update(user).isPresent();
    }
}
