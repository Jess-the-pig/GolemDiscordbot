package Golem.api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
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

  // --- Getters and Setters ---

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCasting_time() {
    return casting_time;
  }

  public void setCasting_time(String casting_time) {
    this.casting_time = casting_time;
  }

  public String getCasting_time_misc() {
    return casting_time_misc;
  }

  public void setCasting_time_misc(String casting_time_misc) {
    this.casting_time_misc = casting_time_misc;
  }

  public String getComponent_material() {
    return component_material;
  }

  public void setComponent_material(String component_material) {
    this.component_material = component_material;
  }

  public String getComponent_semantic() {
    return component_semantic;
  }

  public void setComponent_semantic(String component_semantic) {
    this.component_semantic = component_semantic;
  }

  public String getComponent_verbal() {
    return component_verbal;
  }

  public void setComponent_verbal(String component_verbal) {
    this.component_verbal = component_verbal;
  }

  public String getComponent_misc() {
    return component_misc;
  }

  public void setComponent_misc(String component_misc) {
    this.component_misc = component_misc;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public String getRange() {
    return range;
  }

  public void setRange(String range) {
    this.range = range;
  }

  public String getRange_area() {
    return range_area;
  }

  public void setRange_area(String range_area) {
    this.range_area = range_area;
  }

  public String getSchool() {
    return school;
  }

  public void setSchool(String school) {
    this.school = school;
  }

  public String getSchool_ritual() {
    return school_ritual;
  }

  public void setSchool_ritual(String school_ritual) {
    this.school_ritual = school_ritual;
  }
}
