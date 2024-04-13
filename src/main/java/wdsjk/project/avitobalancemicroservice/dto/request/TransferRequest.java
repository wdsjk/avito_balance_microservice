package wdsjk.project.avitobalancemicroservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(@NotBlank(message = "Can't be blank!") String userFromId,
                              @NotBlank(message = "Can't be blank!") String userToId,
                              @Positive(message = "Can't be negative or equals to 0!") BigDecimal amountOfMoney) {
}
