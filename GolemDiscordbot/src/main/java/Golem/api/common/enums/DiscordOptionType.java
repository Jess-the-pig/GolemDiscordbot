package Golem.api.common.enums;

/** Enum représentant les types d'options disponibles dans Discord. */
public enum DiscordOptionType {
  STRING(3),
  INTEGER(4),
  BOOLEAN(5),
  USER(6),
  CHANNEL(7);

  private final int code;

  DiscordOptionType(int code) {
    this.code = code;
  }

  /**
   * Retourne le code numérique associé au type d'option.
   *
   * @return le code du type d'option
   */
  public int getCode() {
    return code;
  }
}
