package homework2.mapper.account;

import homework2.domain.bank.*;
import homework2.domain.dto.account.AccountDtoRequest;
import homework2.mapper.DtoMapperFacade;
import org.springframework.stereotype.Service;

@Service
public class AccountDtoMapperRequest extends DtoMapperFacade<Account, AccountDtoRequest> {
    public AccountDtoMapperRequest() {
        super(Account.class, AccountDtoRequest.class);
    }
}
