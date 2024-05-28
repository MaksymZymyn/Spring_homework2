package homework2.mapper.customer;

import homework2.domain.bank.Customer;
import homework2.domain.dto.customer.CustomerDtoRequest;
import homework2.mapper.DtoMapperFacade;
import org.springframework.stereotype.Service;

@Service
public class CustomerDtoMapperRequest extends DtoMapperFacade<Customer, CustomerDtoRequest> {

    public CustomerDtoMapperRequest() {
        super(Customer.class, CustomerDtoRequest.class);
    }
}
