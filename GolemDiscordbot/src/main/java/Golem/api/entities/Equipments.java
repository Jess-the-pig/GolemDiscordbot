package Golem.api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "equipments")
public class Equipments {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String type1;
  private String type2;
  private int weight_lbs;
  private int price_golds;

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

  public String getType1() {
    return type1;
  }

  public void setType1(String type1) {
    this.type1 = type1;
  }

  public String getType2() {
    return type2;
  }

  public void setType2(String type2) {
    this.type2 = type2;
  }

  public int getWeight_lbs() {
    return weight_lbs;
  }

  public void setWeight_lbs(int weight_lbs) {
    this.weight_lbs = weight_lbs;
  }

  public int getPrice_golds() {
    return price_golds;
  }

  public void setPrice_golds(int price_golds) {
    this.price_golds = price_golds;
  }
}
