package Golem.api.rpg.npcs;

import Golem.api.common.entity.Combatant;
import Golem.api.common.interfaces.TimeStampedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "npcs")
@Getter
@Setter
public class Npcs implements Combatant, TimeStampedEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String char_id;
  private String name;
  private int base_hp;
  private int stats_1;
  private int stats_2;
  private int stats_3;
  private int stats_4;
  private int stats_5;
  private int stats_6;
  private String background;
  private String race;
  private String class_starting;
  private int class_starting_level;
  private String subclass_starting;
  private String class_other;
  private String subclass_other;
  private int total_level;
  private String feats;
  private String inventory;
  private int notes_len;
  private Long userid;
  private String username;

  @Column(name = "date_created", nullable = false)
  private LocalDateTime dateCreated;

  @Column(name = "last_updated", nullable = false)
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
