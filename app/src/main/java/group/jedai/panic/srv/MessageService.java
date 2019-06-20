package group.jedai.panic.srv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import group.jedai.panic.R;
import group.jedai.panic.activitys.MenuMapActivity;
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

    }

    public void connect(String id, final GoogleMap googleMap, final MenuMapActivity.ICallback iCallback) {

        client = new StompClient("canal1");
        client.connect(Constantes.WEB_SOCKET_URL);

        TopicHandler handler = client.subscribe(Constantes.TOPIC + id);

        handler.addListener(new StompMessageListener() {
            @Override
            public void onMessage(StompMessage message) {
                System.out.println("MESSAGE FROM: " + message.getHeader("destination") + " : " + message.getContent());

                try {
                    Alerta alerta = mapper.readValue(message.getContent(), Alerta.class);
                    if (alerta != null && alerta.getLatitudeG() != 0.0) {
                        iCallback.run(googleMap, alerta.getLatitude(), alerta.getLongitude(), alerta.getLatitudeG(), alerta.getLongitudeG());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
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
