package homework2.domain.dto.employer;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployerDtoResponse {
    private Long id;
    private String name;
    private String address;
    private List<String> customersNames;
}
