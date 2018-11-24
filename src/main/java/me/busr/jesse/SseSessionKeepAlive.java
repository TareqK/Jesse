/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.busr.jesse;

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

    private static final ScheduledExecutorService PING_SERVICE = Executors.newScheduledThreadPool(1);
    private static final Logger LOG = Logger.getLogger(SseSessionKeepAlive.class.getName());
    private static long interval = 120;
    private static volatile Set<SseSession> SESSIONS = ConcurrentHashMap.newKeySet();

    protected static void addSession(SseSession session) {
        SESSIONS.add(session);
    }

    protected static void removeSession(SseSession session) {
        SESSIONS.remove(session);
    }

    private static class PingRunner implements Runnable {
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

  protected static void start(){
    
      PING_SERVICE.scheduleAtFixedRate(new PingRunner(), 0, interval, TimeUnit.SECONDS);
      LOG.info("Using session Keep-Alive");
  }
  
  protected static void setInterval(long interval){
      SseSessionKeepAlive.interval = interval;
  }
}
