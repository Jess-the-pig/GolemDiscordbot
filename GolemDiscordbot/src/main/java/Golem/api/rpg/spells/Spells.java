package Golem.api.rpg.spells;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "spells")
public class Spells {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String casting_time;
  private String casting_time_misc;
  private String component_material;
  private String component_semantic;
  private String component_verbal;

  @Column(length = 4000)
  private String component_misc;

  @Column(length = 4000)
  private String description;

  private String duration;
  private String level;
  private String range;
  private String range_area;
  private String school;
  private String school_ritual;
}
