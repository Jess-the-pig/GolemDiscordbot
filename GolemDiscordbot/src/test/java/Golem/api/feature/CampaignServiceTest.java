package Golem.api.feature;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import Golem.api.db.CampaignRepository;
import Golem.api.db.CharacterRepository;
import Golem.api.factories.DiscordUserFactory;
import Golem.api.rpg.campaign.CampaignService;
import Golem.api.rpg.dto.ReplyFactory;

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
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
    @Mock private Message message;

    @InjectMocks private CampaignService campaignService;

    private Faker faker;
    private User mockUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        faker = new Faker();

        // Utilisation de la factory pour créer un mock User Discord
        mockUser =
                DiscordUserFactory.createMockUser(
                        faker.name().username(), faker.number().randomNumber());
    }

    @Test
    void testStartCampaignCreation_createsSessionAndSendsReply() {
        // Arrange
        var fakeChannelId = faker.number().randomNumber();

        when(buttonEvent.getInteraction()).thenReturn(interaction);
        when(interaction.getUser()).thenReturn(mockUser);
        when(interaction.getChannelId()).thenReturn(Snowflake.of(fakeChannelId));

        // Mock du ReplyFactory
        try (MockedStatic<ReplyFactory> replyFactoryMock = Mockito.mockStatic(ReplyFactory.class)) {
            replyFactoryMock
                    .when(() -> ReplyFactory.deferAndSend(any(), anyString()))
                    .thenReturn(Mono.empty());

            // Act
            Mono<Void> result = campaignService.startCampaignCreation(buttonEvent);

            // Assert
            StepVerifier.create(result).verifyComplete();
            replyFactoryMock.verify(
                    () ->
                            ReplyFactory.deferAndSend(
                                    eq(buttonEvent),
                                    eq("Campaign creation started! Please answer below 👇")));
        }
    }

    @Test
    void testHandleCampaignMessage_noSession_returnsEmpty() {
        // Arrange
        var fakeChannelId = faker.number().randomNumber();

        when(messageEvent.getMessage()).thenReturn(message);
        when(message.getChannelId()).thenReturn(Snowflake.of(fakeChannelId));
        when(message.getAuthor()).thenReturn(Optional.empty());

        // Act
        Mono<Void> result = campaignService.handleCampaignMessage(messageEvent);

        // Assert
        StepVerifier.create(result).verifyComplete();
    }
}
