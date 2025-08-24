package Golem.api.rpg.encounters;

import Golem.api.rpg.characters.Characters;
import Golem.api.rpg.monsters.Monsters;
import Golem.api.rpg.npcs.Npcs;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "initiative")
@Getter
@Setter
public class Initiative {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Valeur du jet de dé
  private Long initiativeValue;

  // Position dans l'ordre (0, 1, 2...)
  private Integer initiativeOrder;

  // Lien vers l'Encounter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "encounter_id")
  private Encounters encounter;

  // Relations polymorphes vers différents types d'entités
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "character_id")
  private Characters character;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "monster_id")
  private Monsters monster;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "npc_id")
  private Npcs npc;
}
