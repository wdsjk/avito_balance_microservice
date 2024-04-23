package wdsjk.project.avitobalancemicroservice.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Date;

@Builder
public record TransactionResponse(
        String userFromId,
        String userToId,
        BigDecimal amount,
        Date date,
        String comments
) {
}
