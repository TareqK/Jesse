/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tareq
 */
public class DefaultSessionManager extends SseSessionManager {

  private static final Set<SseSession> SESSIONS = ConcurrentHashMap.newKeySet();
  private static final Logger LOG = Logger.getLogger(DefaultSessionManager.class.getName());
  private static final ReentrantLock LOCK = new ReentrantLock();

  /**
   * The Default implementation of the session manager which simply stores sessions in a hashset
   */
  public DefaultSessionManager() {

  }

  @Override
  public void onClose(SseSession session) {
    LOCK.lock();
    try {
      SESSIONS.remove(session);
      LOG.log(Level.FINEST, "Removed Session, Sessions are now{0}", SESSIONS.size());
    } finally {
      LOCK.unlock();
    }
  }

  @Override
  public void onOpen(SseSession session) {
    LOCK.lock();
    try {
      SESSIONS.add(session);
      LOG.log(Level.FINEST, "Added Session, Sessions are now{0}", SESSIONS.size());
    } finally {
      LOCK.unlock();
    }
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
  public void onError(SseSession session, Throwable t) {
    LOG.severe(t.getMessage());
  }

}
