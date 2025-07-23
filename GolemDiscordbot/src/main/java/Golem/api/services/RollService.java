package Golem.api.services;

import com.austinv11.servicer.Service;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RollService {

  public int rollDiceWithResult(int sides) {
    // Utilise ThreadLocalRandom pour plus de performance et de sécurité en multithread
    return ThreadLocalRandom.current().nextInt(1, sides + 1);
  }
}
