package com.jburns.aap;

import java.io.IOException;

public class BEMain {

    public static void main(String[] args) {

        SpotifyConnector c = new SpotifyConnector();
        WebsocketServer server1 = new WebsocketServer(c);
        server1.start();
    }

}
