package stefanowicz.kacper.validator;

import stefanowicz.kacper.model.Customer;
import stefanowicz.kacper.validator.generic.AbstractValidator;

import javax.mail.internet.InternetAddress;
import java.util.Map;

public class CustomerValidator extends AbstractValidator<Customer> {
    @Override
    public Map<String, String> validate(Customer customer) {
        errors.clear();

        if(customer == null){
            errors.put("customerObject", "Customer object is not valid, it cannot be null");
            return errors;
        }

        if(!isCustomerNameValid(customer)){
            errors.put("customerName", "Customer name is not valid, it has to consists of letters only");
        }

        if(!isCustomerSurNameValid(customer)){
            errors.put("customerSurname", "Customer surname is not valid, it has to consists of letters and of characters like .' and space");
        }

        if(!isCustomerAgeValid(customer)){
            errors.put("customerAge", "Customer age is not valid, it has to be equal to or greater than 16");
        }

        try{
            InternetAddress address = new InternetAddress(customer.getEmail());
            address.validate();
        }
        catch (Exception e){
            errors.put("customerEmail", "Customer email is not valid");
        }

        return errors;
    }

    private boolean isCustomerNameValid(Customer customer){
        return customer.getName() != null && customer.getName().matches("[A-ZŁŻŚ][A-Za-z]+");
    }

    private boolean isCustomerSurNameValid(Customer customer){
        return customer.getSurname() != null && customer.getSurname().matches("[A-ZŁŻŚ][A-Za-z .']+");
    }

    private boolean isCustomerAgeValid(Customer customer){
        return customer.getAge() >= 16;
    }

}
