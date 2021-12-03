package fjpc.wetalk.chat;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller
public class ChatController {

    private SimpMessagingTemplate messagingTemplate;

    private SimpUserRegistry userRegistry;

    public ChatController(SimpMessagingTemplate messagingTemplate, SimpUserRegistry userRegistry) {
        this.messagingTemplate = messagingTemplate;
        this.userRegistry = userRegistry;
    }

    @SubscribeMapping("/chat.users")
    public List<String> getAllUsers() {
        return userRegistry.getUsers()
                .stream()
                .map(SimpUser::getName)
                .collect(toList());
    }

    @MessageMapping("/chat.home")
    public Message sendToAll(@Payload String content, Principal principal) {
        String from = principal.getName();
        String to = "all";
        return new Message(from,to,content);
    }

    @MessageMapping("/chat.{username}.private")
    @SendToUser("/queue/chat.private")
    public Message sendToUser(@Payload String content, @DestinationVariable String username, Principal principal) {
        String from = principal.getName();
        Message message = new Message(from,username,content);
        if (!message.getFrom().equals(message.getTo())) {
            messagingTemplate.convertAndSendToUser(message.getTo(), "/queue/chat.private", message);
        }
        return message;
    }

}