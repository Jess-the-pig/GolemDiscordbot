package Golem.api.entities;

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
@Getter
@Setter
@Table(name = "characters")
public class Characters {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String playerName;
  private String characterName;
  private String race;
  private String subrace;
  private String background;
  private String class_; // ok de garder _ ici
  private String subclass;
  private int level;
  private int experiencePoints;

  @Column(columnDefinition = "TEXT")
  private String featuresAndTraits;

  @Column(length = 1000)
  private String languages;

  @Column(length = 1000)
  private String personalityTraits;

  @Column(length = 2000)
  private String notes;

  private LocalDateTime dateCreated;
  private LocalDateTime lastUpdated;
}
