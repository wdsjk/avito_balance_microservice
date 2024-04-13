package wdsjk.project.avitobalancemicroservice.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record DepositRequest(@NotBlank(message = "Can't be blank!") String userId,
                             BigDecimal amountOfMoney) {
}
