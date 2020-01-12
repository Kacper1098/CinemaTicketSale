package stefanowicz.kacper.help;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.Opt;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.model.Customer;
import stefanowicz.kacper.model.User;
import stefanowicz.kacper.repository.CustomerRepository;
import stefanowicz.kacper.repository.UserRepository;
import stefanowicz.kacper.util.Config;
import stefanowicz.kacper.util.UserDataService;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.Console;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.logging.ConsoleHandler;


@RequiredArgsConstructor
public class LoginService {
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    private String generateSalt(final int length){
        if(length < 1){
            throw new AppException("Error while generatin salt: length must be greater than 0");
        }
        byte[] salt = new byte[length];
        secureRandom.nextBytes(salt);

        return Base64.getEncoder().encodeToString(salt);
    }

    private String hashPassword(char[] password, String salt){

        byte[] bytes = salt.getBytes();

        PBEKeySpec spec = new PBEKeySpec(password, bytes, Config.ITERATIONS, Config.KEY_LENGTH);

        Arrays.fill(password, Character.MIN_VALUE);

        try{
            SecretKeyFactory fac = SecretKeyFactory.getInstance(Config.ALGORITHM);
            byte[] securePassword = fac.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(securePassword);
        }
        catch (Exception e){
            throw new AppException("Error occured while hashing password!");
        }
        finally{
            spec.clearPassword();
        }
    }

    private boolean verifyPassword(char[] password, String key, String salt){
        String optEncrypted = hashPassword(password, salt);

        return optEncrypted.equals(key);
    }


    public Customer createNewUser(){
        Console console = System.console();
        String salt = generateSalt(Config.SALT_LENGTH);
        User newUser =  User.builder()
                    .userName(UserDataService.getString("Username: "))
                    .password(hashPassword(
                           console != null
                                   ?
                                   console.readPassword("Password: ")
                                   :
                                   UserDataService.getString("Password: ").toCharArray()
                            , salt))
                    .salt(salt)
                    .isAdmin(false)
                    .build();
        if(userRepository.getUserByUserName(newUser.getUserName()).isPresent()){
            throw new AppException("This username already exists!");
        }
        else{
            Customer customer = Customer.builder()
                    .name(UserDataService.getString("First name: "))
                    .surname(UserDataService.getString("Last name: "))
                    .email(UserDataService.getString("Email: "))
                    .age(UserDataService.getInt("Age: "))
                    .build();
            customer = customerRepository.add(customer).orElseThrow(() -> new AppException("Could not get customer from database"));
            newUser.setCustomerId(customer.getId());
            userRepository.add(newUser).orElseThrow(() -> new AppException("Could not get user from database"));

            return customer;
        }
    }

    /**
     *
     * @param userName Customer's username
     * @param password Customer's password
     * @return Returns customer if passed data was correct, exception otherwise.
     */
    public User getSigningUser(String userName, char[] password){
        Optional<User> userOptional = userRepository.getUserByUserName(userName);
        if(userOptional.isEmpty()){
            throw new AppException("This userName doesn't exist!!");
        }
        User user = userOptional.get();

        if(!verifyPassword(password, user.getPassword(), user.getSalt())){
            throw new AppException("Wrong password!!");
        }
        else{
            return user;
        }
    }

    public Customer getCustomerByUser(User user){
        if(user == null){
            throw new AppException("Wrong user argument!");
        }
        return customerRepository.findOne(user.getCustomerId()).orElseThrow(
                () -> new AppException("Could not find customer with id: " + user.getCustomerId()));
    }
}
