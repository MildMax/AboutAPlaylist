package com.jburns.aap;

import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class WebsocketServer extends WebSocketServer {

    private static int TCP_PORT = 8888;

    private Set<WebSocket> conns;
    private SpotifyConnector spotifyConnector;

    public WebsocketServer(SpotifyConnector connector) {
        super(new InetSocketAddress(TCP_PORT));
        conns = new HashSet<>();
        spotifyConnector = connector;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conns.add(conn);
        //System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        conns.remove(conn);
        //System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        //System.out.println("Message from client: " + message);

        String uri = "";
        try {
            uri = Processor.parseSpotifyURL(message);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            conn.send("N/A");
            return;
        }

        _Playlist p = spotifyConnector.getPlaylist(uri);
        //System.out.println(p.toString());

        Gson gson = new Gson();
        String o = gson.toJson(p);
        //System.out.println(o);

        conn.send(o);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        //ex.printStackTrace();
        if (conn != null) {
            conns.remove(conn);
            // do some thing if required
        }

        System.out.println(ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }




}
