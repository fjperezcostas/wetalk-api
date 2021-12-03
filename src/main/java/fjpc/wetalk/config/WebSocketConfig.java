package fjpc.wetalk.config;

import fjpc.wetalk.security.jwt.JwtUtils;
import fjpc.wetalk.security.websocket.WebSocketAuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.user.DefaultUserDestinationResolver;
import org.springframework.messaging.simp.user.UserDestinationResolver;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private JwtUtils jwt;

    private DefaultSimpUserRegistry userRegistry;

    private DefaultUserDestinationResolver resolver;

    public WebSocketConfig(JwtUtils jwt) {
        this.jwt = jwt;
        userRegistry = new DefaultSimpUserRegistry();
        resolver = new DefaultUserDestinationResolver(userRegistry);
    }

    @Bean
    @Primary
    DefaultSimpUserRegistry userRegistry() {
        return userRegistry;
    }

    @Bean
    @Primary
    public UserDestinationResolver userDestinationResolver() {
        return resolver;
    }

    @Bean
    ChannelInterceptor webSocketAuthInterceptor() {
        return new WebSocketAuthInterceptor(jwt,userRegistry());
    }

    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setUserDestinationPrefix("/user");
        config.setApplicationDestinationPrefixes("/app");
    }

    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").setAllowedOrigins("*");
    }

    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor());
    }

}
