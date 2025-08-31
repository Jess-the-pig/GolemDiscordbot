package Golem.api.db;

import Golem.api.rpg.monsters.Monsters;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour gérer les {@link Monsters} dans la base de données. Fournit des opérations CRUD
 * via {@link JpaRepository} et des requêtes personnalisées.
 */
@Repository
public interface MonsterRepository extends JpaRepository<Monsters, Long> {
    /**
     * Récupère un monstre par son nom.
     *
     * @param name le nom du monstre
     * @return le monstre correspondant
     */
    public Monsters findByName(String name);

    /**
     * Récupère la liste des monstres associés à un joueur spécifique.
     *
     * @param username le nom du joueur
     * @return la liste des monstres appartenant au joueur
     */
    public List<Monsters> findByUsername(String username);
}
