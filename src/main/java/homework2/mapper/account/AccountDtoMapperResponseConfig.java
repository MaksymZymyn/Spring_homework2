package homework2.mapper.account;

import homework2.domain.bank.Account;
import homework2.domain.dto.account.AccountDtoResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.*;
import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Configuration
public class AccountDtoMapperResponseConfig {

    @Bean
    public ModelMapper accountDtoResponseMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE);

        mapper.createTypeMap(Account.class, AccountDtoResponse.class)
                .addMapping(Account::getId, AccountDtoResponse::setId)
                .addMapping(Account::getNumber, AccountDtoResponse::setNumber)
                .addMapping(Account::getCurrency, AccountDtoResponse::setCurrency)
                .addMapping(Account::getBalance, AccountDtoResponse::setBalance)
                .addMapping(src -> src.getCustomer().getName(), AccountDtoResponse::setCustomerName);

        return mapper;
    }
}
