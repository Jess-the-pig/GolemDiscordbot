package Golem.api.discordgetaway;

import java.util.List;

public interface DiscordEventHandlerProvider {
  List<DiscordEventHandler<?>> getEventHandlers();
}
