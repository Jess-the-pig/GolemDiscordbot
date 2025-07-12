package Golem.api.configurations;

import Golem.api.commands.ICommand;
import Golem.api.commands.helloCommand;
import Golem.api.commands.pingCommand;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CommandConfig {
    @Bean
    public List<ICommand> commands() {
        return List.of(new pingCommand(), new helloCommand());
    }
}
