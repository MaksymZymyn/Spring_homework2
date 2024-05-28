package homework2.domain.dto.customer;

import lombok.*;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDtoResponse {
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private Set<UUID> accountNumbers;
    private Set<String> employerNames;
}
