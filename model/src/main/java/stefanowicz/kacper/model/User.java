package stefanowicz.kacper.model;

import jdk.jfr.BooleanFlag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Integer id;
    private String userName;
    private String password;
    private String salt;
    private Boolean isAdmin;
    private Integer customerId;
}
