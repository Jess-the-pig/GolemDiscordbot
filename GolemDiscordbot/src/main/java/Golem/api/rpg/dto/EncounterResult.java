package Golem.api.rpg.dto;

import Golem.api.rpg.monsters.Monsters;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EncounterResult {
  private final List<Monsters> monsters;
  private final Monsters boss;
}
