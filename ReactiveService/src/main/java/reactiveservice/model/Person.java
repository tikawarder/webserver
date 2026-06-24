package reactiveservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

// R2DBC entity — NOT JPA.
//
//   JPA                         R2DBC
//   @Entity                 →   @Table  (Spring Data Relational)
//   @GeneratedValue(IDENTITY)   omit — SERIAL in SQL handles it; null id = INSERT, non-null = UPDATE
//   @Column(name="...")     →   field name maps automatically: camelCase → snake_case
//                               so birthDay → birth_day in the persons table
//   No lazy loading, no EntityManager, no persistence context — just plain objects.
@Table("persons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    private Long id;

    private String name;

    private LocalDate birthDay;

    private String city;
}
