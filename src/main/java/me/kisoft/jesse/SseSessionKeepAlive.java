/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 *
 * @author tareq
 */
class SseSessionKeepAlive {

  private static final Logger LOG = Logger.getLogger(SseSessionKeepAlive.class.getName());
  private static long interval = 120;

  static Future schedule(SseSession.KeepaliveRunner runner) {
    return JesseExecutorService.getInstance().schedule(runner, SseSessionKeepAlive.getInterval(), TimeUnit.SECONDS);
  }

  private SseSessionKeepAlive() {
    throw new IllegalArgumentException("This is a Utility Class");
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

  /**
   * gets the defined keepalive interval
   *
   * @return the keepalive interval
   */
  protected static long getInterval() {
    return interval;
  }

}
