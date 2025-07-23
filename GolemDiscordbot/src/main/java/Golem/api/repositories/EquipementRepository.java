package Golem.api.repositories;

import Golem.api.entities.Equipments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipementRepository extends JpaRepository<Equipments, Long> {}
