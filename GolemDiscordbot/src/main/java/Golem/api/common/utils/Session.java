package Golem.api.common.utils;

/**
 * Classe représentant une session de traitement pour une entité.
 *
 * @param <T> le type d'entité contenue dans la session
 */
public class Session<T> {

  public int step;
  public T entity;
  public String lastField;
  public String data;

  /**
   * Crée une session avec des valeurs initiales.
   *
   * @param step l'étape initiale
   * @param entity l'entité associée
   * @param lastField le dernier champ modifié
   */
  public Session(int step, T entity, String lastField) {
    this.step = step;
    this.entity = entity;
    this.lastField = lastField;
  }

  public Session() {}
}
