package Golem.api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "encounters")
@RequiredArgsConstructor
public class Encounters {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "campaign_id")
  private Campaign campaignId;

  @ManyToMany
  @JoinTable(
      name = "encounter_monsters",
      joinColumns = @JoinColumn(name = "encounter_id"),
      inverseJoinColumns = @JoinColumn(name = "monster_id"))
  private List<Monsters> monsters;

  private Boolean isFinished;

  private List<Integer> initiative;

  private LocalDateTime dateCreated;
  private LocalDateTime lastUpdated;

  @ManyToMany
  @JoinTable(
      name = "encounter_characters",
      joinColumns = @JoinColumn(name = "encounter_id"),
      inverseJoinColumns = @JoinColumn(name = "character_id"))
  private List<Characters> characters;
}
