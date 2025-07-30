package Golem.api.db;

import Golem.api.rpg.loot.generateeuipements.Equipments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipementRepository extends JpaRepository<Equipments, Long> {}
