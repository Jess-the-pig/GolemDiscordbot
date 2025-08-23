package Golem.api.common.interfaces;

import java.time.LocalDateTime;

/** Interface représentant une entité horodatée. */
public interface TimeStampedEntity {
  void setDateCreated(LocalDateTime date);

  void setLastUpdated(LocalDateTime date);
}
