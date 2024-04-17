package wdsjk.project.avitobalancemicroservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ShowAndTransactionRequest(@NotBlank(message = "Can't be blank!") String userId) {
}
