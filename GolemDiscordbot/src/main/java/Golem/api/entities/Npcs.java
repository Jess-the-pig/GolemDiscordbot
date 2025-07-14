package Golem.api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "npcs")
public class Npcs {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String char_id;
  private String name;
  private int base_hp;
  private int stats_1;
  private int stats_2;
  private int stats_3;
  private int stats_4;
  private int stats_5;
  private int stats_6;
  private String background;
  private String race;
  private String class_starting;
  private int class_starting_level;
  private String subclass_starting;
  private String class_other;
  private String subclass_other;
  private int total_level;
  private String feats;
  private String inventory;
  private String date_modified;
  private int notes_len;

  @Column(precision = 10, scale = 2)
  private BigDecimal gold;

  // --- Getters and Setters ---

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getChar_id() {
    return char_id;
  }

  public void setChar_id(String char_id) {
    this.char_id = char_id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getBase_hp() {
    return base_hp;
  }

  public void setBase_hp(int base_hp) {
    this.base_hp = base_hp;
  }

  public int getStats_1() {
    return stats_1;
  }

  public void setStats_1(int stats_1) {
    this.stats_1 = stats_1;
  }

  public int getStats_2() {
    return stats_2;
  }

  public void setStats_2(int stats_2) {
    this.stats_2 = stats_2;
  }

  public int getStats_3() {
    return stats_3;
  }

  public void setStats_3(int stats_3) {
    this.stats_3 = stats_3;
  }

  public int getStats_4() {
    return stats_4;
  }

  public void setStats_4(int stats_4) {
    this.stats_4 = stats_4;
  }

  public int getStats_5() {
    return stats_5;
  }

  public void setStats_5(int stats_5) {
    this.stats_5 = stats_5;
  }

  public int getStats_6() {
    return stats_6;
  }

  public void setStats_6(int stats_6) {
    this.stats_6 = stats_6;
  }

  public String getBackground() {
    return background;
  }

  public void setBackground(String background) {
    this.background = background;
  }

  public String getRace() {
    return race;
  }

  public void setRace(String race) {
    this.race = race;
  }

  public String getClass_starting() {
    return class_starting;
  }

  public void setClass_starting(String class_starting) {
    this.class_starting = class_starting;
  }

  public int getClass_starting_level() {
    return class_starting_level;
  }

  public void setClass_starting_level(int class_starting_level) {
    this.class_starting_level = class_starting_level;
  }

  public String getSubclass_starting() {
    return subclass_starting;
  }

  public void setSubclass_starting(String subclass_starting) {
    this.subclass_starting = subclass_starting;
  }

  public String getClass_other() {
    return class_other;
  }

  public void setClass_other(String class_other) {
    this.class_other = class_other;
  }

  public String getSubclass_other() {
    return subclass_other;
  }

  public void setSubclass_other(String subclass_other) {
    this.subclass_other = subclass_other;
  }

  public int getTotal_level() {
    return total_level;
  }

  public void setTotal_level(int total_level) {
    this.total_level = total_level;
  }

  public String getFeats() {
    return feats;
  }

  public void setFeats(String feats) {
    this.feats = feats;
  }

  public String getInventory() {
    return inventory;
  }

  public void setInventory(String inventory) {
    this.inventory = inventory;
  }

  public String getDate_modified() {
    return date_modified;
  }

  public void setDate_modified(String date_modified) {
    this.date_modified = date_modified;
  }

  public int getNotes_len() {
    return notes_len;
  }

  public void setNotes_len(int notes_len) {
    this.notes_len = notes_len;
  }

  public BigDecimal getGold() {
    return gold;
  }

  public void setGold(BigDecimal gold) {
    this.gold = gold;
  }
}
