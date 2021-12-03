package fjpc.wetalk.security.websocket;

import fjpc.wetalk.security.jwt.JwtUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.util.Objects;

import static org.springframework.messaging.simp.stomp.StompCommand.CONNECT;

public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private JwtUtils jwt;

    private DefaultSimpUserRegistry userRegistry;

    public WebSocketAuthInterceptor(JwtUtils jwt, DefaultSimpUserRegistry userRegistry) {
        this.jwt = jwt;
        this.userRegistry = userRegistry;
    }

    @SuppressWarnings("unchecked")
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (Objects.equals(Objects.requireNonNull(accessor).getCommand(), CONNECT)) {
            String token = jwt.getAuthHeader(message).orElse("");
            Authentication auth = jwt.verify(token);
            accessor.setUser(auth);
            userRegistry.onApplicationEvent(new SessionConnectedEvent(this, (Message<byte[]>) message, auth));
        }
        return message;
    }

}
