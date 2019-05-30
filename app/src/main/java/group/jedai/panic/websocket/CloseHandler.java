package group.jedai.panic.websocket;

import okhttp3.WebSocket;

public class CloseHandler {
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private final WebSocket webSocket;

    public CloseHandler(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public void close() {
        webSocket.close(NORMAL_CLOSURE_STATUS, "websocket cerrado");
    }

}
