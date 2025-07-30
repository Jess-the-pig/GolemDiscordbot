package Golem.api.rpg.dices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class RollResult {
  private final List<Long> rolls;
  private final Long total;

  public RollResult(List<Long> rolls) {
    this.rolls = Collections.unmodifiableList(new ArrayList<>(rolls));
    this.total = rolls.stream().mapToLong(Long::longValue).sum();
  }
}
