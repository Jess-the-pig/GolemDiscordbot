package Golem.api.rpg.campaign;

import Golem.api.common.interfaces.TimeStampedEntity;
import Golem.api.rpg.npcs.Npcs;
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

/**
 * Entité représentant l'association entre une campagne et un NPC.
 *
 * <p>Cette table de liaison permet de rattacher des personnages non-joueurs (NPCs) à une campagne
 * RPG spécifique. Chaque enregistrement relie un NPC unique à une campagne donnée.
 *
 * <p>Implémente l'interface {@link TimeStampedEntity} afin de gérer automatiquement les dates de
 * création et de dernière mise à jour.
 */
@Entity
@Table(name = "campaign_npcs")
@Getter
@Setter
public class CampaignNpc implements TimeStampedEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "campaign_id")
  private Campaign campaign;

  @ManyToOne(optional = false)
  @JoinColumn(name = "npc_id")
  private Npcs npc;

  private LocalDateTime dateCreated;
  private LocalDateTime lastUpdated;

  // Optionnel : flag indiquant si on doit utiliser les overrides
  private boolean overrideEnabled = false;

  @Override
  public void setDateCreated(LocalDateTime date) {
    this.dateCreated = date;
  }

  @Override
  public void setLastUpdated(LocalDateTime date) {
    this.lastUpdated = date;
  }
}
