package client;

import config.TemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import repository.GoodsRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = {TemplateConfig.class})
public class TestRepository {

    @Autowired
    private GoodsRepository userRepository;

    @Test
    public void test() {
        assertNotNull(userRepository);
//        Goods user = new Goods("Camera", X"ddochi@gmail.com", "male", 10);
//        userRepository.save(user);
//        log.info("user={}", user);
    }

}
