package homework2.mapper.employer;

import homework2.domain.bank.Employer;
import homework2.domain.dto.employer.EmployerDtoRequest;
import homework2.mapper.DtoMapperFacade;
import org.springframework.stereotype.Service;

@Service
public class EmployerDtoMapperRequest extends DtoMapperFacade<Employer, EmployerDtoRequest> {

    public EmployerDtoMapperRequest() {
        super(Employer.class, EmployerDtoRequest.class);
    }
}
