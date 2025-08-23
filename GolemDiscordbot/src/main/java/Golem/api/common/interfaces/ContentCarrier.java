package Golem.api.common.interfaces;

/**
 * Interface représentant un objet qui transporte du contenu générique.
 *
 * @param <T> le type de contenu transporté
 */
public interface ContentCarrier<T> {
  String getContent();

  /**
   * Retourne l'objet délégué associé à ce contenu.
   *
   * @return l'objet délégué
   */
  Object getDelegate();
}
