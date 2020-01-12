package stefanowicz.kacper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
    private Integer id;
    private String name;
    private String surname;
    private Integer age;
    private String email;
    private Integer loyaltyCardId;
}
