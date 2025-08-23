package Golem.api.rpg.encounters.create_encounter.set_difficulty;

import org.springframework.stereotype.Component;

@Component
public class ParseCr {
  public double parse(String cr) {
    if (cr.contains("/")) {
      String[] parts = cr.split("/");
      return Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
    }
    return Double.parseDouble(cr);
  }
}
