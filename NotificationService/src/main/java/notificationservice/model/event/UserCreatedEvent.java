package notificationservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreatedEvent {
    private Long id;
    private String name;
    private LocalDate birthDay;
    private String city;
    private String message;
}
