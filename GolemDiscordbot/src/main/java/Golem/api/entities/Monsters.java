package Golem.api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "monsters")
public class Monsters {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;
  private String size;
  private String monster_type;
  private String alignment;
  private int ac;
  private int hp;
  private int strength;
  private int str_mod;
  private int dex;
  private int dex_mod;
  private int con;
  private int con_mod;
  private int intel;
  private int int_mod;
  private int wis;
  private int wis_mod;
  private int cha;
  private int cha_mod;
  private String senses;
  private String languages;
  private String cr;
  private int str_save;
  private int dex_save;
  private int con_save;
  private int int_save;
  private int wis_save;
  private int cha_save;
  private int speed;
  private int swim;
  private int fly;
  private int climb;
  private int burrow;
  private int number_legendary_actions;
  private int history;
  private int perception;
  private int stealth;
  private int persuasion;
  private int insight;
  private int deception;
  private int arcana;
  private int religion;
  private int acrobatics;
  private int athletics;
  private int intimidation;

  // --- Getters and Setters ---

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public String getMonster_type() {
    return monster_type;
  }

  public void setMonster_type(String monster_type) {
    this.monster_type = monster_type;
  }

  public String getAlignment() {
    return alignment;
  }

  public void setAlignment(String alignment) {
    this.alignment = alignment;
  }

  public int getAc() {
    return ac;
  }

  public void setAc(int ac) {
    this.ac = ac;
  }

  public int getHp() {
    return hp;
  }

  public void setHp(int hp) {
    this.hp = hp;
  }

  public int getStrength() {
    return strength;
  }

  public void setStrength(int strength) {
    this.strength = strength;
  }

  public int getStr_mod() {
    return str_mod;
  }

  public void setStr_mod(int str_mod) {
    this.str_mod = str_mod;
  }

  public int getDex() {
    return dex;
  }

  public void setDex(int dex) {
    this.dex = dex;
  }

  public int getDex_mod() {
    return dex_mod;
  }

  public void setDex_mod(int dex_mod) {
    this.dex_mod = dex_mod;
  }

  public int getCon() {
    return con;
  }

  public void setCon(int con) {
    this.con = con;
  }

  public int getCon_mod() {
    return con_mod;
  }

  public void setCon_mod(int con_mod) {
    this.con_mod = con_mod;
  }

  public int getIntel() {
    return intel;
  }

  public void setIntel(int intel) {
    this.intel = intel;
  }

  public int getInt_mod() {
    return int_mod;
  }

  public void setInt_mod(int int_mod) {
    this.int_mod = int_mod;
  }

  public int getWis() {
    return wis;
  }

  public void setWis(int wis) {
    this.wis = wis;
  }

  public int getWis_mod() {
    return wis_mod;
  }

  public void setWis_mod(int wis_mod) {
    this.wis_mod = wis_mod;
  }

  public int getCha() {
    return cha;
  }

  public void setCha(int cha) {
    this.cha = cha;
  }

  public int getCha_mod() {
    return cha_mod;
  }

  public void setCha_mod(int cha_mod) {
    this.cha_mod = cha_mod;
  }

  public String getSenses() {
    return senses;
  }

  public void setSenses(String senses) {
    this.senses = senses;
  }

  public String getLanguages() {
    return languages;
  }

  public void setLanguages(String languages) {
    this.languages = languages;
  }

  public String getCr() {
    return cr;
  }

  public void setCr(String cr) {
    this.cr = cr;
  }

  public int getStr_save() {
    return str_save;
  }

  public void setStr_save(int str_save) {
    this.str_save = str_save;
  }

  public int getDex_save() {
    return dex_save;
  }

  public void setDex_save(int dex_save) {
    this.dex_save = dex_save;
  }

  public int getCon_save() {
    return con_save;
  }

  public void setCon_save(int con_save) {
    this.con_save = con_save;
  }

  public int getInt_save() {
    return int_save;
  }

  public void setInt_save(int int_save) {
    this.int_save = int_save;
  }

  public int getWis_save() {
    return wis_save;
  }

  public void setWis_save(int wis_save) {
    this.wis_save = wis_save;
  }

  public int getCha_save() {
    return cha_save;
  }

  public void setCha_save(int cha_save) {
    this.cha_save = cha_save;
  }

  public int getSpeed() {
    return speed;
  }

  public void setSpeed(int speed) {
    this.speed = speed;
  }

  public int getSwim() {
    return swim;
  }

  public void setSwim(int swim) {
    this.swim = swim;
  }

  public int getFly() {
    return fly;
  }

  public void setFly(int fly) {
    this.fly = fly;
  }

  public int getClimb() {
    return climb;
  }

  public void setClimb(int climb) {
    this.climb = climb;
  }

  public int getBurrow() {
    return burrow;
  }

  public void setBurrow(int burrow) {
    this.burrow = burrow;
  }

  public int getNumber_legendary_actions() {
    return number_legendary_actions;
  }

  public void setNumber_legendary_actions(int number_legendary_actions) {
    this.number_legendary_actions = number_legendary_actions;
  }

  public int getHistory() {
    return history;
  }

  public void setHistory(int history) {
    this.history = history;
  }

  public int getPerception() {
    return perception;
  }

  public void setPerception(int perception) {
    this.perception = perception;
  }

  public int getStealth() {
    return stealth;
  }

  public void setStealth(int stealth) {
    this.stealth = stealth;
  }

  public int getPersuasion() {
    return persuasion;
  }

  public void setPersuasion(int persuasion) {
    this.persuasion = persuasion;
  }

  public int getInsight() {
    return insight;
  }

  public void setInsight(int insight) {
    this.insight = insight;
  }

  public int getDeception() {
    return deception;
  }

  public void setDeception(int deception) {
    this.deception = deception;
  }

  public int getArcana() {
    return arcana;
  }

  public void setArcana(int arcana) {
    this.arcana = arcana;
  }

  public int getReligion() {
    return religion;
  }

  public void setReligion(int religion) {
    this.religion = religion;
  }

  public int getAcrobatics() {
    return acrobatics;
  }

  public void setAcrobatics(int acrobatics) {
    this.acrobatics = acrobatics;
  }

  public int getAthletics() {
    return athletics;
  }

  public void setAthletics(int athletics) {
    this.athletics = athletics;
  }

  public int getIntimidation() {
    return intimidation;
  }

  public void setIntimidation(int intimidation) {
    this.intimidation = intimidation;
  }
}
