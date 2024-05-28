package homework2.domain.dto.account;

import homework2.domain.bank.Currency;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDtoResponse {
    private Long id;
    private UUID number;
    private Currency currency;
    private double balance;
    private String customerName;
}
