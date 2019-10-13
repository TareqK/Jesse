/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tareq
 */
public class DefaultSessionManager extends SseSessionManager {

  private static final HashSet<SseSession> SESSIONS = new HashSet();
  private static final Logger LOG = Logger.getLogger(DefaultSessionManager.class.getName());

  /**
   * The Default implementation of the session manager which simply stores sessions in a hashset
   */
  public DefaultSessionManager() {

  }

  @Override
  public void onClose(SseSession session) {
    SESSIONS.remove(session);
    LOG.log(Level.FINEST, "Removed Session, Sessions are now{0}", SESSIONS.size());
  }

  @Override
  public void onOpen(SseSession session) {
    SESSIONS.add(session);
    LOG.log(Level.FINEST, "Added Session, Sessions are now{0}", SESSIONS.size());
  }

  /**
   * send an event to all sessions
   *
   * @param event the SSE Event to send
   */
  public static void broadcastEvent(SseEvent event) {
    broadcastEvent(SESSIONS, event);
  }

  @Override
  public void onError(SseSession session) {
    SESSIONS.remove(session);
  }

}
