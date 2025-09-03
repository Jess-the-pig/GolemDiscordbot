package Golem.api.feature;

import static org.mockito.Mockito.when;

import Golem.api.db.CampaignRepository;
import Golem.api.db.CharacterRepository;
import Golem.api.factories.DiscordUserFactory;
import Golem.api.rpg.campaign.CampaignService;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

import net.datafaker.Faker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock private CampaignRepository campaignRepository;
    @Mock private CharacterRepository characterRepository;

    @Mock private ButtonInteractionEvent buttonEvent;
    @Mock private Interaction interaction;
    @Mock private MessageCreateEvent messageEvent;

    @InjectMocks private CampaignService campaignService;

    private Faker faker;
    private User mockUser;

    @BeforeEach
    void setup() {
        faker = new Faker();

        // Création d’un mock User Discord via la factory
        mockUser =
                DiscordUserFactory.createMockUser(
                        faker.name().toString(), faker.number().randomNumber());
    }

    @Test
    void testHandleCampaignMessage_noSession_returnsEmpty() {
        // Arrange
        var fakeChannelId = faker.number().randomNumber();
        Message mockMessage = Mockito.mock(Message.class);

        when(messageEvent.getMessage()).thenReturn(mockMessage);
        when(mockMessage.getChannelId()).thenReturn(Snowflake.of(fakeChannelId));
        when(mockMessage.getAuthor()).thenReturn(Optional.empty());

        // Act
        Mono<Void> result = campaignService.handleCampaignMessage(messageEvent);

        // Assert
        StepVerifier.create(result).verifyComplete();
    }
}
