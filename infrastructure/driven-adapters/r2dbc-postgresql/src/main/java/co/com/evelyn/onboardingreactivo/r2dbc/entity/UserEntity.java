package co.com.evelyn.onboardingreactivo.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String avatar;
}
