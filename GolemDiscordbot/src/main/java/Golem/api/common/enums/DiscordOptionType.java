package Golem.api.common.enums;

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

  public int getCode() {
    return code;
  }
}
