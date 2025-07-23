package Golem.api.services;

import Golem.api.repositories.CharacterRepository;
import Golem.api.repositories.MonsterRepository;
import Golem.api.repositories.TerrainRepository;
import com.austinv11.servicer.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EncounterService {
  private final RollService rollService;
  private final CharacterRepository characterRepository;
  private final MonsterRepository monsterRepository;
  private final TerrainRepository terrainRepository;
}
