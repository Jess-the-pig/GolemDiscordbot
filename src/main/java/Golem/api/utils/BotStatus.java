package Golem.api.utils;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;

public class BotStatus {

    private GatewayDiscordClient client;

    public BotStatus(GatewayDiscordClient client) {
        this.client = client;
    }

    public void online() {
        if (client != null) {
            client.updatePresence(ClientPresence.online(ClientActivity.listening("to /commands")))
                    .block();
        }
    }

    public void idle() {
        if (client != null) {
            client.updatePresence(ClientPresence.idle()).block();
        }
    }
}
