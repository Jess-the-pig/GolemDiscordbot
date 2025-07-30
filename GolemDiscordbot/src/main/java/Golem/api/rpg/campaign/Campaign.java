package Golem.api.rpg.campaign;

import Golem.api.rpg.characters.Characters;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "campaigns")
public class Campaign {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private Long campaignId;

  private String playerCreator;

  private String dm;

  @OneToMany
  @JoinColumn(name = "campaign_id")
  private List<Characters> characters;

  private LocalDateTime dateCreated;
  private LocalDateTime lastUpdated;
}
