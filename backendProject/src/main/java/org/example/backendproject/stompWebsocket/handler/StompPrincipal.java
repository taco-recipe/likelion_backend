package org.example.backendproject.stompWebsocket.handler;

import java.security.Principal;

public class StompPrincipal implements Principal {
    // 닉데임으로 식별 가능하도록? 하는 principal
    private final String name;

    public StompPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
