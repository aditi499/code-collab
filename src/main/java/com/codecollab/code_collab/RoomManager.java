package com.codecollab.code_collab;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.WebSocketSession;

public class RoomManager {

    // room -> (session -> username)
    private static final Map<String, Map<WebSocketSession, String>> rooms = new ConcurrentHashMap<>();

    // ---------------- ADD USER ----------------
    public static synchronized void addUser(String room, WebSocketSession session, String username) {

        rooms.putIfAbsent(room, new ConcurrentHashMap<>());

        // IMPORTANT: overwrite old session if rejoining
        rooms.get(room).put(session, username);
    }

    // ---------------- REMOVE SINGLE USER ----------------
    public static synchronized void removeUser(String room, WebSocketSession session) {

        if (!rooms.containsKey(room)) return;

        rooms.get(room).remove(session);

        if (rooms.get(room).isEmpty()) {
            rooms.remove(room);
        }
    }

    // ---------------- REMOVE FROM ALL ROOMS ----------------
    public static synchronized void removeUserFromAllRooms(WebSocketSession session) {

        List<String> emptyRooms = new ArrayList<>();

        for (String room : rooms.keySet()) {

            Map<WebSocketSession, String> users = rooms.get(room);

            if (users != null) {
                users.remove(session);

                if (users.isEmpty()) {
                    emptyRooms.add(room);
                }
            }
        }

        for (String r : emptyRooms) {
            rooms.remove(r);
        }
    }

    // ---------------- GET ALL SESSIONS ----------------
    public static Set<WebSocketSession> getRoom(String room) {

        rooms.putIfAbsent(room, new ConcurrentHashMap<>());
        return rooms.get(room).keySet();
    }

    // ---------------- GET USER LIST ----------------
    public static List<String> getUserList(String room) {

        if (!rooms.containsKey(room)) return new ArrayList<>();

        return new ArrayList<>(rooms.get(room).values());
    }

    // ---------------- GET USER COUNT ----------------
    public static int getUserCount(String room) {

        if (!rooms.containsKey(room)) return 0;

        return rooms.get(room).size();
    }

    // ---------------- DEBUG HELPER ----------------
    public static void printState() {
        System.out.println("ROOM STATE: " + rooms);
    }
}