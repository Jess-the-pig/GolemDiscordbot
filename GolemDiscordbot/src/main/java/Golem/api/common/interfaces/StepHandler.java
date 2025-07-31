package Golem.api.common.interfaces;

import Golem.api.common.utils.Session;
import reactor.core.publisher.Mono;

public interface StepHandler<T extends TimeStampedEntity, E extends ContentCarrier> {
  Mono<Void> handle(E event, Session<T> session);
}
