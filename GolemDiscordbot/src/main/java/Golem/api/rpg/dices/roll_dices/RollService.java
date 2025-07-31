package Golem.api.rpg.dices.roll_dices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

@Service
public class RollService {

  public RollResult rollMultipleDice(Long sides, Long times) {
    List<Long> rolls = new ArrayList<>(times.intValue()); // ⚠️ ArrayList attend un int
    for (long i = 0L; i < times; i++) {
      rolls.add(rollDiceWithResult(sides));
    }
    return new RollResult(rolls);
  }

  public Long rollDiceWithResult(Long sides) {
    return ThreadLocalRandom.current().nextLong(1L, sides + 1L);
  }
}
