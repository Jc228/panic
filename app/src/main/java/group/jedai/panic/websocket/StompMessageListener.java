package group.jedai.panic.websocket;

public interface StompMessageListener {
    void onMessage(StompMessage message);
}
