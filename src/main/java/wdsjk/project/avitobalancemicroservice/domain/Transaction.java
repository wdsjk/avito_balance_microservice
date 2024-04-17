package wdsjk.project.avitobalancemicroservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Table(name = "transactions")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Transaction {
    @Id
    private String id;

    @NotBlank(message = "Can't be blank!")
    private String userFromId;

    @NotBlank(message = "Can't be blank!")
    private String userToId;

    @NotNull(message = "Required")
    private BigDecimal amount;

    @NotNull(message = "Required")
    private Date date;

    private String comments;
}