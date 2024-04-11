package wdsjk.project.avitobalancemicroservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record WithdrawRequest(@NotBlank(message = "Can't be blank!") String userId,
                              @Positive(message = "Can't be negative or equals to 0!") double amountOfMoney) {
}
