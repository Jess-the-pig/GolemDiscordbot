package Golem.api.db;

import Golem.api.rpg.encounters.Encounters;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository pour gérer les {@link Encounters} dans la base de données. Fournit des opérations CRUD
 * via {@link JpaRepository}.
 */
public interface EncounterRepository extends JpaRepository<Encounters, Long> {}
