package stefanowicz.kacper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesStand {
    private Integer id;
    private Integer customerId;
    private Integer movieId;
    private LocalDateTime startDateTime;
    private BigDecimal discount;
}
