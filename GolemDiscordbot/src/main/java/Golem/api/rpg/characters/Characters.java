package Golem.api.rpg.characters;

import Golem.api.common.entity.Combatant;
import Golem.api.rpg.campaign.Campaign;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "characters")
public class Characters implements Combatant {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String playerName;
  private String characterName;
  private String race;

  @Column(name = "class")
  private String class_; // ok de garder _ ici

  private String background;
  private int level;
  private int experiencePoints;

  @Column(columnDefinition = "TEXT")
  private String featuresAndTraits;

  @Column(length = 1000)
  private String languages;

  @Column(length = 1000)
  private String personalityTraits;

  @ManyToOne
  @JoinColumn(name = "campaign_id")
  private Campaign campaign;

  private LocalDateTime dateCreated;
  private LocalDateTime lastUpdated;

  @Override
  public String getCombatantName() {
    return characterName;
  }
}
