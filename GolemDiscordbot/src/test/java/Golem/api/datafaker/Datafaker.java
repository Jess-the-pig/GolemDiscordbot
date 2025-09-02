package Golem.api.datafaker;

import net.datafaker.Faker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Datafaker {

    private Faker faker;

    @Bean
    public Faker Faker() {
        return faker;
    }
}
