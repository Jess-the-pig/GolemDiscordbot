package Golem.api.entities;

import Golem.api.entities.interfaces.Combatant;
import Golem.api.services.RollService;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
  @JoinColumn(name = "id")
  private Campaign campaignId;

  @ManyToOne
  @JoinColumn(name = "id")
  private List<Monsters> monsters;

  private Boolean isFinished;

  private List<Integer> initiative;

  private LocalDateTime dateCreated;
  private LocalDateTime lastUpdated;

  private final RollService rollService;

  private List<Combatant> combatant;
}
