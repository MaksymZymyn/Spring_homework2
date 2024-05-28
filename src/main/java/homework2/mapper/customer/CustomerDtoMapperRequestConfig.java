package homework2.mapper.customer;

import homework2.domain.bank.Customer;
import homework2.domain.dto.customer.CustomerDtoRequest;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.*;
import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Configuration
public class CustomerDtoMapperRequestConfig {

    @Bean
    public ModelMapper customerDtoRequestMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE);

        mapper.createTypeMap(CustomerDtoRequest.class, Customer.class)
                .addMapping(CustomerDtoRequest::getName, Customer::setName)
                .addMapping(CustomerDtoRequest::getEmail, Customer::setEmail)
                .addMapping(CustomerDtoRequest::getAge, Customer::setAge);

        return mapper;
    }
}
