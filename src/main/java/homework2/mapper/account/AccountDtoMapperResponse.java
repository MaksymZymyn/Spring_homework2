package homework2.mapper.account;

import homework2.domain.bank.Account;
import homework2.domain.bank.Customer;
import homework2.domain.dto.account.AccountDtoResponse;
import homework2.mapper.DtoMapperFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountDtoMapperResponse extends DtoMapperFacade<Account, AccountDtoResponse> {

    public AccountDtoMapperResponse() {
        super(Account.class, AccountDtoResponse.class);
    }

    protected void decorateDto(AccountDtoResponse dto, Account entity) {
        Customer customer = entity.getCustomer();
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }
        dto.setCustomerName(customer.getName());
    }
}
