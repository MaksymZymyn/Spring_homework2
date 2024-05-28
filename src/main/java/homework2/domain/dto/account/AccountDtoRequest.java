package homework2.domain.dto.account;

import homework2.domain.bank.Currency;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDtoRequest {

    @NotNull(message = "Currency must be provided")
    private Currency currency;

    @Min(value = 1, message = "Balance must be positive")
    @Digits(integer = 12, message = "Balance must be a valid monetary amount with up to 12 integer digits", fraction = 0)
    private double balance;
}
