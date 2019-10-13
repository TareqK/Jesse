/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

/**
 *
 * @author tareq
 */
class SseSessionKeepAlive {

  private static final Logger LOG = Logger.getLogger(SseSessionKeepAlive.class.getName());
  private static long interval = 120;
  private static final ScheduledExecutorService KEEPALIVE_SERVICE = Executors.newScheduledThreadPool(100);

  private SseSessionKeepAlive() {
    throw new IllegalArgumentException("This is a Utility Class");
  }

  /**
   * Gets the keepalive service
   *
   * @return the keepalive service
   */
  public static ScheduledExecutorService getService() {
    return KEEPALIVE_SERVICE;
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
