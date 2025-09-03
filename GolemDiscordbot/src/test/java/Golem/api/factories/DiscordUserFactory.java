package Golem.api.factories;

import org.mockito.Mockito;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;
import net.datafaker.Faker;

public class DiscordUserFactory {

    private static final Faker faker = new Faker();

    public static User createMockUser() {
        return createMockUser(faker.name().toString(), faker.number().randomNumber());
    }

    public static User createMockUser(String username, long id) {
        // Création d’un mock lenient
        User mockUser = Mockito.mock(User.class, Mockito.withSettings().lenient());

        // Valeurs de base
        Mockito.lenient().when(mockUser.getUsername()).thenReturn(username);
        Mockito.lenient().when(mockUser.getId()).thenReturn(Snowflake.of(id));

        // Ces valeurs ne sont peut-être pas utilisées dans tes tests,
        // mais on les garde lenient pour éviter l’exception
        Mockito.lenient().when(mockUser.getMention()).thenReturn("<@" + id + ">");
        Mockito.lenient().when(mockUser.isBot()).thenReturn(false);
        Mockito.lenient()
                .when(mockUser.getAvatarUrl())
                .thenReturn("https://cdn.discordapp.com/avatars/" + id + "/avatar.png");

        return mockUser;
    }
}
