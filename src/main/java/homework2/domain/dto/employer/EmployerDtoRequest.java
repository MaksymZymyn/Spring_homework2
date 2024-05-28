package homework2.domain.dto.employer;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployerDtoRequest {
    @NotBlank(message = "Employer name must not be blank")
    @Size(max = 100, message = "Employer name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Address must not be blank")
    @Size(min = 5, max = 100, message = "Address must be between 5 and 100 characters")
    private String address;
}
