package com.github.nosh11.ekidensys.session;

public class SessionManager {
    private static final SessionManager instance = new SessionManager();
    public static SessionManager getInstance() {
        return instance;
    }

    private final Session[] sessions = new Session[3];

    public Session[] getSessions() {
        return sessions;
    }

    public Session get(int session_id) {
        if (session_id < 0 | sessions.length <= session_id) return null;
        return sessions[session_id];
    }

    public void reload() {
        for (int i = 0; i < sessions.length; i++) sessions[i] = new Session(i);
    }

    public void save() {
        for (Session session : sessions) session.save();
    }
}
