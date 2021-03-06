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
public class SseSessionFactory {

  private static SseSessionFactory instance = getInstance();

  /**
   * Gets the current instance of the SseSessionFactory;
   *
   * @return the current SseSessionFactory
   */
  protected static SseSessionFactory getInstance() {
    if (instance == null) {//no need for sync because of eager instantiation
      instance = new SseSessionFactory();
    }
    return instance;
  }

  /**
   * Create a session with a keep alive param
   *
   * @param asyncContext the async context of this request
   * @param manager the session manager to use
   * @return an SSE session
   */
  protected SseSession createSession(AsyncContext asyncContext, SseSessionManager manager) {
    return new SseSession(manager, asyncContext);
  }
}
