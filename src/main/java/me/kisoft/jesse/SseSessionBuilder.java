/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse;

import javax.servlet.AsyncContext;

/**
 *
 * @author tareq
 */
public class SseSessionBuilder {

  /**
   * Build a session without keepalive
   *
   * @param asyncContext the async context of the request
   * @param manager the session manager to use
   * @return an SSE Session
   */
  protected static SseSession buildSession(AsyncContext asyncContext, SseSessionManager manager) {
    return new SseSession(manager, asyncContext);
  }

  /**
   * Build a session with a keep alive param
   *
   * @param asyncContext the async context of this request
   * @param manager the session manager to use
   * @param keepAlive whether to keep the session alive or not
   * @return an SSE session
   */
  protected static SseSession buildSession(AsyncContext asyncContext, SseSessionManager manager, boolean keepAlive) {
    return new SseSession(manager, asyncContext, keepAlive);
  }
}
