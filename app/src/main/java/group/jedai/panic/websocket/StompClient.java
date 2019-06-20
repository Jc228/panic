package group.jedai.panic.websocket;

import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class StompClient extends WebSocketListener {
    private Map<String, TopicHandler> topics = new HashMap<>();
    private CloseHandler closeHandler;
    private String id = "sub-001";
    private WebSocket webSocket;

    public StompClient() {
    }

    public StompClient(String id) {
        this.id = id;
    }

    public void connect(String address) {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .pingInterval(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(address)
                .build();

        client.newWebSocket(request, this);

        client.dispatcher().executorService().shutdown();
    }

    public void disconnect() {
        if (webSocket != null) {
            closeHandler.close();
            webSocket = null;
            closeHandler = null;
        }
    }

    public boolean isConnected() {
        return closeHandler != null;
    }

    public TopicHandler subscribe(String topic) {
        TopicHandler handler = new TopicHandler(topic);
        topics.put(topic, handler);
        if (webSocket != null) {
            sendSubscribeMessage(webSocket, topic);
        }
        return handler;
    }

    public void unSubscribe(String topic) {
        topics.remove(topic);
    }

    public TopicHandler getTopicHandler(String topic) {
        if (topics.containsKey(topic)) {
            return topics.get(topic);
        }
        return null;
    }

    public void send(String topic, String content) {
        StompMessage message = new StompMessage("SEND");
        message.put("destination", topic);
        message.put("content-type", "application/json;charset=UTF-8");
        message.setContent(content);
        //this.webSocket.send(StompMessageSerializer.serialize(message));
    }

    private void sendConnectMessage(WebSocket webSocket) {
        StompMessage message = new StompMessage("CONNECT");
        message.put("accept-version", "1.1");
        message.put("heart-beat", "10000,10000");
        webSocket.send(StompMessageSerializer.serialize(message));
    }

    private void sendSubscribeMessage(WebSocket webSocket, String topic) {
        StompMessage message = new StompMessage("SUBSCRIBE");
        message.put("id", id);
        message.put("destination", topic);
        webSocket.send(StompMessageSerializer.serialize(message));
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        this.webSocket = webSocket;
        sendConnectMessage(webSocket);
        for (String topic : topics.keySet()) {
            sendSubscribeMessage(webSocket, topic);
        }
        closeHandler = new CloseHandler(webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        System.out.println("RAW MESSAGE TEXT: " + text);
        StompMessage message = StompMessageSerializer.deserialize(text);
        String topic = message.getHeader("destination");
        if (topics.containsKey(topic)) {
            topics.get(topic).onMessage(message);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        System.out.println("RAW MESSAGE BYTES: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
        System.out.println("CLOSE: " + code + " " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
        t.printStackTrace();
    }

}
