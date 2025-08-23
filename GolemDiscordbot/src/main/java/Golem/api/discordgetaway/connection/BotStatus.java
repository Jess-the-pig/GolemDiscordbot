package Golem.api.discordgetaway.connection;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;

/** Classe utilitaire pour gérer le statut en ligne du bot Discord. */
public class BotStatus {

  private GatewayDiscordClient client;

  /**
   * Crée une instance de BotStatus avec le client Discord fourni.
   *
   * @param client le client Discord à utiliser pour mettre à jour le statut
   */
  public BotStatus(GatewayDiscordClient client) {
    this.client = client;
  }

  /** Définit le statut du bot sur "en ligne" et affiche qu'il écoute les commandes. */
  public void online() {
    if (client != null) {
      client
          .updatePresence(ClientPresence.online(ClientActivity.listening("to /commands")))
          .block();
    }
  }

  /** Définit le statut du bot sur "inactif" (idle). */
  public void idle() {
    if (client != null) {
      client.updatePresence(ClientPresence.idle()).block();
    }
  }
}
