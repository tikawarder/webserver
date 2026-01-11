package databaseserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Person {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@NotBlank(message = "'name' must not be empty.")
	@Size(min = 2, max = 30)
	private String name;
	@Column(name = "birthDay")
	@Past
	@NotNull(message = "'birthDate' must not be empty.")
	private LocalDate birthDay;
	@NotBlank(message = "'city' must not be empty.")
	@Size(min = 2, max = 30)
	private String city;
}