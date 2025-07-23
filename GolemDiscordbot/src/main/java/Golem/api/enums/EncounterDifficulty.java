package Golem.api.enums;

public enum EncounterDifficulty {
  EASY(0.5),
  MEDIUM(1.0),
  HARD(1.5),
  DEADLY(2.0);

  private final double factor;

  EncounterDifficulty(double factor) {
    this.factor = factor;
  }

  public double getFactor() {
    return factor;
  }
}
