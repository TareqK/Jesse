/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.busr.sse;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author tareq
 */
public abstract class SessionManager {
   protected static final ExecutorService EXECUTOR = Executors.newScheduledThreadPool(15);

    public static void sendEvent(Session session, Event event) {
        EXECUTOR.submit(()->{
            session.pushEvent(event);
        });
    }

    public static void broadcastEvent(Session[] sessions, Event event) {
        EXECUTOR.submit(()->{
            for(Session session : sessions){
            session.pushEvent(event);
            }
        });
    }
    
    public static void broadcastEvent(Set<Session> sessions, Event event) {
        EXECUTOR.submit(()->{
            for(Session session : sessions){
            session.pushEvent(event);
            }
        });
    }

    
    public static void broadcastEvent(List<Session> sessions, Event event) {
        EXECUTOR.submit(()->{
            for(Session session : sessions){
            session.pushEvent(event);
            }
        });
    }
    public static void broadcastEvent(Event event) {

    }

    public abstract void onClose(Session session);

    public abstract void onOpen(Session session);
}
