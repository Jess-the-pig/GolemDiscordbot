package Golem.api.db;

import Golem.api.rpg.monsters.Monsters;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonsterRepository extends JpaRepository<Monsters, Long> {
  public Monsters findByName(String name);

  public List<Monsters> findByPlayerName(String username);
}
