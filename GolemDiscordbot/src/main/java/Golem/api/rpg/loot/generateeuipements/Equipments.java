package Golem.api.rpg.loot.generateeuipements;

import Golem.api.rpg.loot.generatechest.Chest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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

  @ManyToOne
  @JoinColumn(name = "chest_id")
  private Chest chest;
  // --- Getters and Setters ---

}
