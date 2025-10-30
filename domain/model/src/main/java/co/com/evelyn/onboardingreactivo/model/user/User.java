package co.com.evelyn.onboardingreactivo.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String avatar;

    public User withoutId() {
        return this.toBuilder().id(null).build();
    }
}
