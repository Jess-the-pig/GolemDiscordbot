package Golem.api.listener;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import Golem.api.EventListener;

@Service
public class HelloMessageListener implements EventListener<MessageCreateEvent> {

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        Message message = event.getMessage();

        if (message.getContent().equalsIgnoreCase("!hello")) {
            return message.getChannel()
                .flatMap(channel -> channel.createMessage("Hello world!"))
                .then();
        }

        return Mono.empty();
    }
}
