package fjpc.wetalk.chat;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
public class ChatEventListener {

    private SimpMessagingTemplate messagingTemplate;

    private SimpUserRegistry userRegistry;

    public ChatEventListener(SimpMessagingTemplate messagingTemplate, SimpUserRegistry userRegistry) {
        this.messagingTemplate = messagingTemplate;
        this.userRegistry = userRegistry;
    }

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String username = Objects.requireNonNull(accessor.getUser()).getName();
        if (Objects.requireNonNull(userRegistry.getUser(username)).getSessions().size() == 1) {
            messagingTemplate.convertAndSend("/topic/chat.login", username);
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String username = Objects.requireNonNull(headers.getUser()).getName();
        if (userRegistry.getUser(username) == null) {
            messagingTemplate.convertAndSend("/topic/chat.logout", username);
        }
    }

}
