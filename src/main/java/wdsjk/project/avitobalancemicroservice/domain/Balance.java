package wdsjk.project.avitobalancemicroservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Table(name = "balances")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Balance {
    @Id
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    @Min(value = 0)
    private BigDecimal amountOfMoney;
}
