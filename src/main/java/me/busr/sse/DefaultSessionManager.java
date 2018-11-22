/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.busr.sse;

import java.util.HashSet;

public class DefaultSessionManager extends SessionManager {
    
    private static final HashSet<Session> SESSIONS = new HashSet();
    
    public DefaultSessionManager() {
        
    }
    
    @Override
    public void onClose(Session session) {
       SESSIONS.remove(session);
    }
    
    @Override
    public void onOpen(Session session) {
        SESSIONS.add(session);
    }
    
    public static void broadcastEvent(Event event) {
        broadcastEvent(SESSIONS,event);
    }

    @Override
    public void onError(Session session) {
        SESSIONS.remove(session);
    }
    
    
    
}
