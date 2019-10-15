/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import javax.ws.rs.WebApplicationException;

/**
 *
 * @author tareq
 */
public abstract class SseSessionManager {

  /**
   * Gets the executor service used to push events
   *
   * @return an executor service
   */
  private static ExecutorService getExecutor() {
    return JesseExecutorService.getInstance();
  }

  /**
   * Sends an event to a session
   *
   * @param session the session to send the event to
   * @param event the event to send
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
   *
   * @param sessions the array of sessions to send the event to
   * @param event the event to send
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
   * Broadcast an event to a collection of sessions
   *
   * @param sessions the collection of SSE Sessions to send the event to
   * @param event the event to send
   */
  public static void broadcastEvent(Collection<SseSession> sessions, SseEvent event) {
    if (sessions != null && event != null) {
      getExecutor().submit(() -> {
        sessions.forEach((session) -> {
          session.pushEvent(event);
        });
      });
    }
  }

  /**
   * A method called during the closing of a session
   *
   * @param session the session to close
   * @throws WebApplicationException if there is an issue closing the session
   */
  public abstract void onClose(SseSession session);

  /**
   * A method called when a session is being opened
   *
   * @param session the session to open
   * @throws WebApplicationException if there is an issue when opening the session. This could be an authentication error or something
   * similar
   */
  public abstract void onOpen(SseSession session) throws WebApplicationException;

  /**
   * A method called when there is an error in sending in a session.
   *
   * @param session the session on whom the error has happened
   * @param t the error that happened
   */
  public abstract void onError(SseSession session, Throwable t);

  /**
   * Generate a session id for this session
   *
   * @param session the session to generate the ID for
   * @return a new sesison Id as string
   */
  public String getSessionId(SseSession session) {
    return UUID.randomUUID().toString();
  }
}
