/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.busr.sseservlet;

import java.util.HashSet;

public class SessionManagerDefaultImpl implements SessionManager {
    
    private static final HashSet<Session> SESSIONS = new HashSet();
    
    public SessionManagerDefaultImpl() {
        
    }
    
    @Override
    public void onClose(Session session) {
       System.out.println(SESSIONS.remove(session));
    }
    
    @Override
    public void onOpen(Session session) {
        SESSIONS.add(session);
    }
    
    public static void broadcast(Event event) {
        SESSIONS.forEach(consumer -> {
            consumer.pushEvent(event);
        });
    }
    
    
    
}
