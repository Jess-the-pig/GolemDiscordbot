package Golem.api.rpg.npcs.modify_npcs;

import Golem.api.rpg.npcs.Npcs;
import java.util.Map;
import java.util.function.BiConsumer;

public class NpcsFieldSetters {
  public static final Map<String, BiConsumer<Npcs, Object>> SETTERS =
      Map.ofEntries(
          Map.entry("name", (BiConsumer<Npcs, Object>) (n, v) -> n.setName((String) v)),
          Map.entry(
              "base hp",
              (BiConsumer<Npcs, Object>) (n, v) -> n.setBase_hp(Integer.parseInt(v.toString()))),
          Map.entry(
              "stat 1",
              (BiConsumer<Npcs, Object>) (n, v) -> n.setStats_1(Integer.parseInt(v.toString()))),
          Map.entry(
              "stat 2",
              (BiConsumer<Npcs, Object>) (n, v) -> n.setStats_2(Integer.parseInt(v.toString()))),
          Map.entry(
              "stat 3",
              (BiConsumer<Npcs, Object>) (n, v) -> n.setStats_3(Integer.parseInt(v.toString()))),
          Map.entry(
              "stat 4",
              (BiConsumer<Npcs, Object>) (n, v) -> n.setStats_4(Integer.parseInt(v.toString()))),
          Map.entry(
              "stat 5",
              (BiConsumer<Npcs, Object>) (n, v) -> n.setStats_5(Integer.parseInt(v.toString()))),
          Map.entry(
              "stat 6",
              (BiConsumer<Npcs, Object>) (n, v) -> n.setStats_6(Integer.parseInt(v.toString()))),
          Map.entry("background", (BiConsumer<Npcs, Object>) (n, v) -> n.setBackground((String) v)),
          Map.entry("race", (BiConsumer<Npcs, Object>) (n, v) -> n.setRace((String) v)),
          Map.entry(
              "starting class",
              (BiConsumer<Npcs, Object>) (n, v) -> n.setClass_starting((String) v)),
          Map.entry(
              "class starting level",
              (BiConsumer<Npcs, Object>)
                  (n, v) -> n.setClass_starting_level(Integer.parseInt(v.toString()))),
          Map.entry(
              "starting subclass",
              (BiConsumer<Npcs, Object>) (n, v) -> n.setSubclass_starting((String) v)),
          Map.entry(
              "other class", (BiConsumer<Npcs, Object>) (n, v) -> n.setClass_other((String) v)),
          Map.entry(
              "other subclass",
              (BiConsumer<Npcs, Object>) (n, v) -> n.setSubclass_other((String) v)),
          Map.entry(
              "total level",
              (BiConsumer<Npcs, Object>)
                  (n, v) -> n.setTotal_level(Integer.parseInt(v.toString()))),
          Map.entry("feats", (BiConsumer<Npcs, Object>) (n, v) -> n.setFeats((String) v)),
          Map.entry("inventory", (BiConsumer<Npcs, Object>) (n, v) -> n.setInventory((String) v)),
          Map.entry(
              "notes length",
              (BiConsumer<Npcs, Object>) (n, v) -> n.setNotes_len(Integer.parseInt(v.toString()))));

  private NpcsFieldSetters() {
    // Utility class -> pas d'instanciation
  }
}
