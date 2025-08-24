package Golem.api.rpg.dto;

import Golem.api.common.interfaces.TimeStampedEntity;

public record InitiativeRoll(Long initiativeValue, TimeStampedEntity entity) {}
