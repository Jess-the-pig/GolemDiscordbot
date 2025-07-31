package Golem.api.common.interfaces;

import java.time.LocalDateTime;

public interface TimeStampedEntity {
  void setDateCreated(LocalDateTime date);

  void setLastUpdated(LocalDateTime date);
}
