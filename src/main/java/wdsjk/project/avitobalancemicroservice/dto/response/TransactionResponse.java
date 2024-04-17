package wdsjk.project.avitobalancemicroservice.dto.response;

import java.math.BigDecimal;
import java.util.Date;

public record TransactionResponse(
        String userFromId,
        String userToId,
        BigDecimal amount,
        Date date,
        String comments
) {
}
