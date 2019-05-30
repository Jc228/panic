package group.jedai.panic.srv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import group.jedai.panic.dto.Alerta;
import group.jedai.panic.utils.Constantes;
import group.jedai.panic.websocket.StompClient;
import group.jedai.panic.websocket.StompMessage;
import group.jedai.panic.websocket.StompMessageListener;
import group.jedai.panic.websocket.TopicHandler;

public class MessageService {

    private ObjectMapper mapper = new ObjectMapper();
    private StompClient client;

    public MessageService() {
        client = new StompClient("canal1");
        TopicHandler handler = client.subscribe(Constantes.TOPIC);
        handler.addListener(new StompMessageListener() {
            @Override
            public void onMessage(StompMessage message) {
                System.out.println("MESSAGE FROM: " + message.getHeader("destination") + " : " + message.getContent());
            }
        });
    }

    public void connect() {
        client.connect(Constantes.WEB_SOCKET_URL);
    }

    public void send(Alerta message) {
        try {
            client.send(Constantes.URL_WS, mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public void disconnect() {
        client.disconnect();
    }

}
