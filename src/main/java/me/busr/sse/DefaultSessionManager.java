/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.busr.sse;

import java.util.HashSet;

public class DefaultSessionManager extends SseSessionManager {
    
    private static final HashSet<SseSession> SESSIONS = new HashSet();
    
    public DefaultSessionManager() {
        
    }
    
    @Override
    public void onClose(SseSession session) {
       SESSIONS.remove(session);
    }
    
    @Override
    public void onOpen(SseSession session) {
        SESSIONS.add(session);
    }
    
    public static void broadcastEvent(SseEvent event) {
        broadcastEvent(SESSIONS,event);
    }

    @Override
    public void onError(SseSession session) {
        SESSIONS.remove(session);
    }
    
    
    
}
