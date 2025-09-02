package Golem.api.factories;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;

import net.datafaker.Faker;

import org.mockito.Mockito;

public class DiscordUserFactory {

    private static final Faker faker = new Faker();

    public static User createMockUser() {
        return createMockUser(faker.name().toString(), faker.number().randomNumber());
    }

    public static User createMockUser(String username, long id) {
        User mockUser = Mockito.mock(User.class);

        // Valeurs de base
        Mockito.when(mockUser.getUsername()).thenReturn(username);
        Mockito.when(mockUser.getId()).thenReturn(Snowflake.of(id));
        Mockito.when(mockUser.getMention()).thenReturn("<@" + id + ">");
        Mockito.when(mockUser.isBot()).thenReturn(false);

        // Avatar fictif
        Mockito.when(mockUser.getAvatarUrl())
                .thenReturn("https://cdn.discordapp.com/avatars/" + id + "/avatar.png");

        return mockUser;
    }
}
