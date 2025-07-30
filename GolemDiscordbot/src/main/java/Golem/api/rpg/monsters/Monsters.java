package Golem.api.rpg.monsters;

import Golem.api.common.entity.Combatant;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "monsters")
@Getter
@Setter
public class Monsters implements Combatant {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String url;
  // TODO: adapter les valeurs dans le dnd_monsters.csv
  private String cr;
  private String type;
  private String size;
  private int ac;
  private int hp;
  private String speed;
  private String align;
  private boolean legendary;
  private String source;
  private int strScore;
  private int dexScore;
  private int conScore;
  private int intScore;
  private int wisScore;
  private int chaScore;

  @Override
  public String getCombatantName() {
    return name;
  }
}
