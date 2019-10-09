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
import java.util.logging.Logger;

/**
 *
 * @author tareq
 */
class SseSessionKeepAlive {

  private static final ScheduledExecutorService KEEPALIVE_SERVIE = Executors.newScheduledThreadPool(1);
  private static final Logger LOG = Logger.getLogger(SseSessionKeepAlive.class.getName());
  private static long interval = 120;
  private static final Set<SseSession> SESSIONS = ConcurrentHashMap.newKeySet();

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
  }

  /**
   * Remove an SSE session from the keepalive list
   *
   * @param session the session to remove
   */
  protected static void removeSession(SseSession session) {
    SESSIONS.remove(session);
  }

  /**
   * A class that pings each session in the keepalive list
   */
  private static class KeepaliveRunner implements Runnable {

    @Override
    public void run() {
      SESSIONS.forEach(session -> {
        session.pushEvent(new SseEventBuilder()
         .event("ping")
         .data("Keep-Alive")
         .build());
      });
    }
  }

  /**
   * Schedules the keepalive runner
   */
  protected static void start() {

    KEEPALIVE_SERVIE.scheduleAtFixedRate(new KeepaliveRunner(), 0, interval, TimeUnit.SECONDS);
    LOG.info("Using session Keep-Alive");
  }

  /**
   * Sets the interval for the keepalive
   *
   * @param newInterval the interval to set
   */
  protected static void setInterval(long newInterval) {
    SseSessionKeepAlive.interval = newInterval;
  }
}
