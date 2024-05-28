package homework2.mapper.customer;

import homework2.domain.bank.*;
import homework2.domain.dto.customer.CustomerDtoResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Configuration
public class CustomerDtoMapperResponseConfig {

    @Bean
    public ModelMapper customerDtoResponseMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE);

        mapper.createTypeMap(Customer.class, CustomerDtoResponse.class)
                .addMapping(Customer::getId, CustomerDtoResponse::setId)
                .addMapping(Customer::getName, CustomerDtoResponse::setName)
                .addMapping(Customer::getEmail, CustomerDtoResponse::setEmail)
                .addMapping(Customer::getAge, CustomerDtoResponse::setAge)
                .addMapping(src -> {
                    if (src.getAccounts() != null) {
                        return src.getAccounts().stream().map(Account::getNumber).collect(Collectors.toList());
                    }
                    return Collections.emptyList();
                }, CustomerDtoResponse::setAccountNumbers)
                .addMapping(src -> {
                    if (src.getEmployers() != null) {
                        return src.getEmployers().stream().map(Employer::getName).collect(Collectors.toSet());
                    }
                    return Collections.emptySet();
                }, CustomerDtoResponse::setEmployerNames);

        return mapper;
    }
}
