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
import javax.ws.rs.WebApplicationException;

/**
 *
 * @author tareq
 */
public abstract class SseSessionManager {

    protected static final ExecutorService EXECUTOR = Executors.newScheduledThreadPool(15);

    public static void sendEvent(SseSession session, SseEvent event) {
        if (session != null && event != null) {
            EXECUTOR.submit(() -> {
                session.pushEvent(event);
            });
        }
    }

    public static void broadcastEvent(SseSession[] sessions, SseEvent event) {
        if (sessions != null && event != null) {
            EXECUTOR.submit(() -> {
                for (SseSession session : sessions) {
                    session.pushEvent(event);
                }
            });
        }
    }

    public static void broadcastEvent(Set<SseSession> sessions, SseEvent event) {
        if (sessions != null && event != null) {
            EXECUTOR.submit(() -> {
                for (SseSession session : sessions) {
                    session.pushEvent(event);
                }
            });
        }
    }

    public static void broadcastEvent(List<SseSession> sessions, SseEvent event) {
        if (sessions != null && event != null) {
            EXECUTOR.submit(() -> {
                for (SseSession session : sessions) {
                    session.pushEvent(event);
                }
            });
        }
    }

    public abstract void onClose(SseSession session) throws WebApplicationException;

    public abstract void onOpen(SseSession session) throws WebApplicationException;
    
    public abstract void onError(SseSession session);
}
