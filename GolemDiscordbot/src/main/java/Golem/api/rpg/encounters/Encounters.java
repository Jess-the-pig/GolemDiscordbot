package Golem.api.rpg.encounters;

import java.time.LocalDateTime;
import java.util.List;

import Golem.api.rpg.campaign.Campaign;
import Golem.api.rpg.characters.Characters;
import Golem.api.rpg.monsters.Monsters;
import Golem.api.rpg.npcs.Npcs;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    private Campaign campaign;

    private Boolean isFinished = false;

    @OneToMany(mappedBy = "encounter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Initiative> initiative;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "encounter_monsters",
            joinColumns = @JoinColumn(name = "encounter_id"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private List<Monsters> monsters;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "encounter_characters",
            joinColumns = @JoinColumn(name = "encounter_id"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private List<Characters> characters;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "encounter_npcs",
            joinColumns = @JoinColumn(name = "encounter_id"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private List<Npcs> npcs;

    private LocalDateTime dateCreated = LocalDateTime.now();
    private LocalDateTime lastUpdated = LocalDateTime.now();
}
