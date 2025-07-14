package Golem.api.configurations;

import Golem.api.commands.ICommand;
import Golem.api.commands.implementation.helloCommand;
import Golem.api.commands.implementation.pingCommand;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfig {
  @Bean
  public List<ICommand> commands() {
    return List.of(new pingCommand(), new helloCommand());
  }
}
