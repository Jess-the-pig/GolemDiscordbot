package Golem.api.rpg.monsters;

import Golem.api.common.entity.Combatant;
import Golem.api.common.interfaces.TimeStampedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "monsters")
@Getter
@Setter
public class Monsters implements Combatant, TimeStampedEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userid;
  private String username;
  private String playerName;

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

  private LocalDateTime dateCreated;
  private LocalDateTime lastUpdated;

  @Override
  public String getCombatantName() {
    return name;
  }

  @Override
  public void setDateCreated(LocalDateTime date) {
    this.dateCreated = date;
  }

  @Override
  public void setLastUpdated(LocalDateTime date) {
    this.lastUpdated = date;
  }
}
