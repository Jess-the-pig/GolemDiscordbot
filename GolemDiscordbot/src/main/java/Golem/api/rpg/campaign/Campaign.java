package Golem.api.rpg.campaign;

import Golem.api.common.interfaces.TimeStampedEntity;
import Golem.api.rpg.characters.Characters;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entité représentant une campagne RPG.
 *
 * <p>Une campagne est définie par un nom, un créateur, un maître de jeu (DM) et contient une liste
 * de personnages ainsi qu'une liste de NPC associés.
 *
 * <p>Les informations de création et de mise à jour sont également stockées pour permettre le suivi
 * de l'évolution de la campagne.
 */
@Entity
@Getter
@Setter
@Table(name = "campaign")
public class Campaign implements TimeStampedEntity {

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

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampaignNpc> campaignNpcs;

    private LocalDateTime dateCreated;
    private LocalDateTime lastUpdated;
}
