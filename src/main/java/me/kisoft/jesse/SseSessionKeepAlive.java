/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tareq
 */
class SseSessionKeepAlive {

  private static final ScheduledExecutorService KEEPALIVE_SERVICE = Executors.newScheduledThreadPool(1);
  private static final Logger LOG = Logger.getLogger(SseSessionKeepAlive.class.getName());
  private static long interval = 120;
  private static final Set<SseSession> SESSIONS = ConcurrentHashMap.newKeySet();
  private static final SseEvent PING_EVENT = new SseEventBuilder().event("ping").data("Keep-Alive").build();

  private SseSessionKeepAlive() {
    throw new IllegalArgumentException("This is a Utility Class");
  }

  /**
   * Add an SSE session to the keepalive list
   *
   * @param session the session to add
   */
  protected static void addSession(SseSession session) {
    SESSIONS.add(session);
    LOG.log(Level.FINEST, "Added Keepalive, Sessions are now{0}", SESSIONS.size());
  }

  /**
   * Remove an SSE session from the keepalive list
   *
   * @param session the session to remove
   */
  protected static void removeSession(SseSession session) {
    SESSIONS.remove(session);
    LOG.log(Level.FINEST, "Removed Keepalive, Sessions are now{0}", SESSIONS.size());
  }

  /**
   * A class that pings each session in the keepalive list. If this runner is interrupted, it will no longer resheduele itself, and the
   * keepalive must be started again.
   */
  private static class KeepaliveRunner implements Runnable {

    private static final Logger LOG = Logger.getLogger(KeepaliveRunner.class.getName());

    @Override
    public void run() {
      try {
        SESSIONS.forEach(session -> {
          session.pushEvent(PING_EVENT);
        });
      } finally {
        if (!Thread.interrupted()) {//only attempt a rescheduele if the thread hasnt been interrupted.
          LOG.finest("Resechedueling Keepalive thread");
          schedueleOnce(this);
        }
      }
    }

  }

  /**
   * Schedules the keepalive runner
   */
  protected static void start() {
    schedueleOnce(new KeepaliveRunner());
    LOG.info("Started Keepalive runner");
  }

  /**
   * shedules a keepalive runner with the globaly set refresh interval in seconds
   *
   * @param runner the runner to reshecuele
   */
  private static void schedueleOnce(KeepaliveRunner runner) {
    KEEPALIVE_SERVICE.schedule(runner, interval, TimeUnit.SECONDS);
  }

  /**
   * Sets the interval for the keepalive
   *
   * @param newInterval the interval to set
   */
  protected static void setInterval(long newInterval) {
    SseSessionKeepAlive.interval = newInterval;
    LOG.info("Keepalive interval changed, changes will take effect on the next keepalive run");
  }
}
