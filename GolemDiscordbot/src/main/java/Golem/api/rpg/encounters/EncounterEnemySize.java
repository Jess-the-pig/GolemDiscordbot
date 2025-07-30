package Golem.api.rpg.encounters;

import java.util.concurrent.ThreadLocalRandom;

public enum EncounterEnemySize {
  CHAMPION(1, 1),
  ESCOUADE(3, 5),
  DETACHEMENT(6, 10),
  HORDE(11, 20),
  NUEE(21, 50),
  ARMEE(51, 100);

  private final int min;
  private final int max;

  EncounterEnemySize(int min, int max) {
    this.min = min;
    this.max = max;
  }

  public int getRandomSize() {
    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }

  public int getMin() {
    return min;
  }

  public int getMax() {
    return max;
  }
}
