package domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

//@Getter
//@Setter
//@RequiredArgsConstructor
//@ToString
//@EqualsAndHashCode
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value="Goods", timeToLive=-1)
public class Goods implements Serializable {
    @Id
    private Long id;

    @NonNull
    @EqualsAndHashCode.Include
    @Indexed
    private String name;

    @NonNull
    @EqualsAndHashCode.Include
    private Integer price;

    @NonNull
    @EqualsAndHashCode.Include
    private Type type;

    public enum Type {
        TV, Camera
    }
}