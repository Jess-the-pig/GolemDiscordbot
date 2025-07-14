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

  private String player_name;
  private String character_name;
  private String race;
  private String subrace;
  private String background;
  private String alignment;
  private String class_; // "class" est un mot réservé
  private String subclass;
  private int level;
  private int experience_points;

  private int strength;
  private int dexterity;
  private int constitution;
  private int intelligence;
  private int wisdom;
  private int charisma;

  private int strength_mod;
  private int dexterity_mod;
  private int constitution_mod;
  private int intelligence_mod;
  private int wisdom_mod;
  private int charisma_mod;

  private boolean save_strength;
  private boolean save_dexterity;
  private boolean save_constitution;
  private boolean save_intelligence;
  private boolean save_wisdom;
  private boolean save_charisma;

  private boolean acrobatics;
  private boolean animal_handling;
  private boolean arcana;
  private boolean athletics;
  private boolean deception;
  private boolean history;
  private boolean insight;
  private boolean intimidation;
  private boolean investigation;
  private boolean medicine;
  private boolean nature;
  private boolean perception;
  private boolean performance;
  private boolean persuasion;
  private boolean religion;
  private boolean sleight_of_hand;
  private boolean stealth;
  private boolean survival;

  private int armor_class;
  private int initiative;
  private String speed;
  private int max_hp;
  private int current_hp;
  private int temporary_hp;
  private String hit_dice;
  private int death_saves_successes;
  private int death_saves_failures;

  @Column(columnDefinition = "TEXT")
  private String attacks_and_spellcasting;

  private String spellcasting_class;
  private int spell_save_dc;
  private int spell_attack_bonus;

  @Column(columnDefinition = "TEXT")
  private String known_spells;

  private int spell_slots_level_1;
  private int spell_slots_level_2;
  private int spell_slots_level_3;
  private int spell_slots_level_4;
  private int spell_slots_level_5;
  private int spell_slots_level_6;
  private int spell_slots_level_7;
  private int spell_slots_level_8;
  private int spell_slots_level_9;

  @Column(columnDefinition = "TEXT")
  private String equipment;

  private int currency_cp;
  private int currency_sp;
  private int currency_ep;
  private int currency_gp;
  private int currency_pp;

  @Column(columnDefinition = "TEXT")
  private String traits;

  @Column(columnDefinition = "TEXT")
  private String features_and_traits;

  @Column(length = 1000)
  private String languages;

  @Column(length = 1000)
  private String personality_traits;

  @Column(length = 1000)
  private String ideals;

  @Column(length = 1000)
  private String bonds;

  @Column(length = 1000)
  private String flaws;

  @Column(columnDefinition = "TEXT")
  private String backstory;

  @Column(length = 2000)
  private String notes;

  @Column(length = 2000)
  private String proficiencies;

  private boolean inspiration;
  private int proficiency_bonus;
  private int passive_perception;

  private LocalDateTime date_created;
  private LocalDateTime last_updated;
}
