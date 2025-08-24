package Golem.api.common.utils;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.db.NpcsRepository;
import Golem.api.rpg.campaign.CampaignNpc;
import Golem.api.rpg.dto.ReplyFactory;
import Golem.api.rpg.npcs.Npcs;
import java.util.ArrayList;
import java.util.List;
import reactor.core.publisher.Mono;

/**
 * StepHandler pour ajouter plusieurs NPCs à une campagne. L'utilisateur introduit les noms un par
 * un (ou séparés par des virgules), jusqu'à taper "done".
 */
public class AddNpcsStepHandler implements StepHandler<CampaignNpc, ContentCarrier> {

  private final NpcsRepository npcsRepository;
  private final List<Npcs> addedNpcs;
  private boolean firstPromptShown = false;

  public AddNpcsStepHandler(NpcsRepository npcsRepository) {
    this.npcsRepository = npcsRepository;
    this.addedNpcs = new ArrayList<>();
  }

  @Override
  public Mono<Void> handle(ContentCarrier event, Session<CampaignNpc> session) {
    String content = event.getContent().trim();

    // La toute première fois → on demande d'entrer un NPC
    if (!firstPromptShown) {
      firstPromptShown = true;
      return ReplyFactory.reply(
          event.getDelegate(),
          "👋 Quel est le nom du NPC que tu veux ajouter ? (ou plusieurs noms séparés par des"
              + " virgules)");
    }

    // Si l'utilisateur tape "done" → on termine
    if ("done".equalsIgnoreCase(content)) {
      if (addedNpcs.isEmpty()) {
        return ReplyFactory.reply(
            event.getDelegate(), "⚠️ Aucun NPC ajouté. Tape un nom ou annule.");
      }

      // Associer chaque NPC ajouté à l'entité CampaignNpc
      for (Npcs npc : addedNpcs) {
        session.entity.setNpc(npc);
        // éventuellement sauvegarder directement ici si besoin
      }

      return ReplyFactory.reply(
              event.getDelegate(), "✅ Tous les NPCs ont été ajoutés avec succès !")
          .doOnSuccess(v -> session.step += 1); // passe à l'étape suivante
    }

    // Sinon, interprète le(s) nom(s)
    String[] names = content.split(",");
    List<String> notFound = new ArrayList<>();

    for (String nameRaw : names) {
      String name = nameRaw.trim();
      Npcs npc = npcsRepository.findByName(name);

      if (npc != null) {
        addedNpcs.add(npc);
      } else {
        notFound.add(name);
      }
    }

    if (!notFound.isEmpty()) {
      return ReplyFactory.reply(
          event.getDelegate(), "⚠️ NPC(s) non trouvés : " + String.join(", ", notFound));
    }

    return ReplyFactory.reply(
        event.getDelegate(),
        "✅ NPC(s) ajouté(s) ! Tu peux taper un autre nom ou **done** pour terminer.");
  }
}
