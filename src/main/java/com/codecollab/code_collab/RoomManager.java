package com.codecollab.code_collab;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.WebSocketSession;

public class RoomManager {

    private static final Map<String, Map<WebSocketSession, String>> rooms = new ConcurrentHashMap<>();

    public static synchronized void addUser(String room, WebSocketSession session, String username) {

        rooms.putIfAbsent(room, new ConcurrentHashMap<>());

        // remove old mapping if exists
        rooms.get(room).remove(session);

        rooms.get(room).put(session, username);
    }

    public static synchronized void removeUserFromAllRooms(WebSocketSession session) {

        for (String room : rooms.keySet()) {

            Map<WebSocketSession, String> users = rooms.get(room);

            if (users != null) {
                users.remove(session);
            }
        }

        rooms.entrySet().removeIf(e -> e.getValue().isEmpty());
    }

    public static Set<WebSocketSession> getRoom(String room) {

        rooms.putIfAbsent(room, new ConcurrentHashMap<>());
        return rooms.get(room).keySet();
    }

    public static List<String> getUserList(String room) {

        if (!rooms.containsKey(room)) return new ArrayList<>();
        return new ArrayList<>(rooms.get(room).values());
    }

    public static int getUserCount(String room) {

        if (!rooms.containsKey(room)) return 0;
        return rooms.get(room).size();
    }
}