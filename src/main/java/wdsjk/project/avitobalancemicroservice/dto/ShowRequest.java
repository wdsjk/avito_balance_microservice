package wdsjk.project.avitobalancemicroservice.dto;

import jakarta.validation.constraints.NotBlank;

public record ShowRequest(@NotBlank(message = "Can't be blank!") String userId) {
}
