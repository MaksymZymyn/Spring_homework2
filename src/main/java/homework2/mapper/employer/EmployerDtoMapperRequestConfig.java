package homework2.mapper.employer;

import homework2.domain.bank.Employer;
import homework2.domain.dto.employer.EmployerDtoRequest;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Configuration
public class EmployerDtoMapperRequestConfig {

    @Bean
    public ModelMapper employerDtoRequestMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE);

        mapper.createTypeMap(EmployerDtoRequest.class, Employer.class)
                .addMapping(EmployerDtoRequest::getName, Employer::setName)
                .addMapping(EmployerDtoRequest::getAddress, Employer::setAddress);

        return mapper;
    }
}
