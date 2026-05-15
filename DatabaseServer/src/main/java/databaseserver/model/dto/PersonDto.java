package databaseserver.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PersonDto {
    private Long id;

    @NotBlank(message = "'name' must not be empty.")
    @Size(min = 2, max = 30)
    private String name;

    @Past
    @NotNull(message = "'birthDate' must not be empty.")
    private LocalDate birthDay;

    @NotBlank(message = "'city' must not be empty.")
    @Size(min = 2, max = 30)
    private String city;
}