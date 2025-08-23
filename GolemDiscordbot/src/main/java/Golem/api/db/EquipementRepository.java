package Golem.api.db;

import Golem.api.rpg.loot.generateeuipements.Equipments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour gérer les {@link Equipments} dans la base de données. Fournit des opérations CRUD
 * via {@link JpaRepository}.
 */
@Repository
public interface EquipementRepository extends JpaRepository<Equipments, Long> {}
