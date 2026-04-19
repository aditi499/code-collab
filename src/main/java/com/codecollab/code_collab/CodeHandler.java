package com.codecollab.code_collab;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class CodeHandler extends TextWebSocketHandler {

    ObjectMapper mapper = new ObjectMapper();

    private final ConcurrentHashMap<WebSocketSession, String> sessionRoom = new ConcurrentHashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        JsonNode json = mapper.readTree(message.getPayload());

        String type = json.has("type") ? json.get("type").asText() : "chat";
        String room = json.has("room") ? json.get("room").asText() : "";
        String user = json.has("user") ? json.get("user").asText() : "anonymous";
        String content = json.has("content") ? json.get("content").asText() : "";

        // ---------------- JOIN ROOM ----------------
        if ("join".equals(type)) {

            RoomManager.removeUserFromAllRooms(session);

            sessionRoom.put(session, room);
            RoomManager.addUser(room, session, user);

            broadcastSystem(room, user + " joined the room");
            broadcastUserList(room);
            return;
        }

        // ensure session tracked
        if (!room.isEmpty()) {
            sessionRoom.putIfAbsent(session, room);
            RoomManager.addUser(room, session, user);
        }

        // ---------------- REAL-TIME CODE SYNC ----------------
        if ("code".equals(type)) {

            Set<WebSocketSession> users = RoomManager.getRoom(room);

            for (WebSocketSession s : users) {

                if (s.isOpen() && !s.equals(session)) {
                    s.sendMessage(message); // forward raw message
                }
            }

            return;
        }

        // ---------------- CHAT / DEFAULT MESSAGE ----------------
        Message msg = new Message(type, room, user, content);
        String broadcastMsg = mapper.writeValueAsString(msg);

        Set<WebSocketSession> users = RoomManager.getRoom(room);

        for (WebSocketSession s : users) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(broadcastMsg));
            }
        }

        broadcastUserList(room);
    }

    // ---------------- USERS ----------------
    private void broadcastUserList(String room) throws Exception {

        List<String> usersList = RoomManager.getUserList(room);

        ObjectNode msg = mapper.createObjectNode();
        msg.put("type", "users");
        msg.putPOJO("users", usersList);
        msg.put("count", usersList.size());

        String json = mapper.writeValueAsString(msg);

        Set<WebSocketSession> sessions = RoomManager.getRoom(room);

        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(json));
            }
        }
    }

    // ---------------- SYSTEM ----------------
    private void broadcastSystem(String room, String text) throws Exception {

        ObjectNode msg = mapper.createObjectNode();
        msg.put("type", "system");
        msg.put("content", text);

        String json = mapper.writeValueAsString(msg);

        Set<WebSocketSession> sessions = RoomManager.getRoom(room);

        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(json));
            }
        }
    }

    // ---------------- CLEANUP ----------------
    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      org.springframework.web.socket.CloseStatus status) {

        String room = sessionRoom.get(session);

        RoomManager.removeUserFromAllRooms(session);

        if (room != null) {
            try {
                broadcastUserList(room);
            } catch (Exception ignored) {}
        }

        sessionRoom.remove(session);
    }
}