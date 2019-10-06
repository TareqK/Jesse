/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse;

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

    
    /**
     * The thread pool that handles dispatching events
     */
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    public static final ExecutorService getExecutor(){
        return EXECUTOR;
    }
    /**
     * Sends an event to a session
     * @param session
     * @param event
     */
    public static void pushEvent(SseSession session, SseEvent event) {
        if (session != null && event != null) {
            getExecutor().submit(() -> {
                session.pushEvent(event);
            });
        }
    }

    
    /**
     * Broadcast an event to an array of sessions
     * @param sessions
     * @param event
     */
    public static void broadcastEvent(SseSession[] sessions, SseEvent event) {
        if (sessions != null && event != null) {
            getExecutor().submit(() -> {
                for (SseSession session : sessions) {
                    session.pushEvent(event);
                }
            });
        }
    }

    /**
     * Broadcast an event to a set of sessions
     * @param sessions
     * @param event
     */
    public static void broadcastEvent(Set<SseSession> sessions, SseEvent event) {
        if (sessions != null && event != null) {
            getExecutor().submit(() -> {
                sessions.forEach((session) -> {
                    session.pushEvent(event);
                });
            });
        }
    }

    /**
     * Broadcast an event to a list of sessions
     * @param sessions
     * @param event
     */
    public static void broadcastEvent(List<SseSession> sessions, SseEvent event) {
        if (sessions != null && event != null) {
            getExecutor().submit(() -> {
                sessions.forEach((session) -> {
                    session.pushEvent(event);
                });
            });
        }
    }

    /**
     * A method called during the closing  of a session
     * @param session
     * @throws WebApplicationException
     */
    public abstract void onClose(SseSession session) throws WebApplicationException;

    /**
     * A method called when a session is being opened
     * @param session
     * @throws WebApplicationException
     */
    public abstract void onOpen(SseSession session) throws WebApplicationException;
    
    /**
     * A method called when there is an error in sending in a session
     * @param session
     */
    public abstract void onError(SseSession session);

}
