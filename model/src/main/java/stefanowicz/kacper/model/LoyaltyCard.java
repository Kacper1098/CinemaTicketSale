package stefanowicz.kacper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoyaltyCard {
    private Integer id;
    private LocalDate expirationDate;
    private BigDecimal discount;
    private Integer moviesNumber;
    private Integer currentMoviesNumber;
}
