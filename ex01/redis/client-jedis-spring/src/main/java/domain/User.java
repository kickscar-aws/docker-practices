package domain;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class User implements Serializable {
    private String name;
    private String email;
    private String gender;
    private int age;
}