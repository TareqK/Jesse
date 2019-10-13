/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kisoft.jesse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author tareq
 */
public class SseSession {

  private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(150);

  private static final Logger LOG = Logger.getLogger(SseSession.class.getName());
  private Future keepaliveFuture;
  private List<Future> pushFutures;
  private static final SseEvent WELCOME_EVENT = SseEvent
   .getBuilder()
   .data("welcome")
   .event("greeting")
   .mediaType(MediaType.TEXT_PLAIN)
   .id(-9999)
   .build();

  private static final SseEvent PING_EVENT = new SseEventBuilder().event("ping").data("Keep-Alive").build();

  private final AsyncContext asyncContext;
  private final SseSessionManager sessionManager;

  /**
   *
   * @param sessionManager the session manager to register this session in
   * @param asyncContext the async context of the request
   */
  protected SseSession(SseSessionManager sessionManager, AsyncContext asyncContext) {
    this.sessionManager = sessionManager;
    this.asyncContext = asyncContext;
    this.asyncContext.addListener(new SseSessionListener());
    this.pushFutures = new ArrayList<>();
    openSession();
  }

  /**
   * Pushes an event to this SseSession
   *
   * @param event the SSE Event to send
   */
  public void pushEvent(SseEvent event) {
    pushFutures.add(EXECUTOR.submit(new SsePushRunnable(event)));
  }

  private void sessionError() {
    sessionManager.onError(this);
    closeSession();
  }

  /**
   * Closes this sseSession
   */
  public void closeSession() {
    try {
      sessionManager.onClose(this);
    } catch (WebApplicationException ex) {
      LOG.finest(ex.getMessage());
    } finally {
      stopKeepalive();
      cancelRunningTasks();
      asyncContext.complete();
    }

  }

  /**
   * Opens the SSE session, and calls the session open callaback in the session manager
   */
  private void openSession() {
    try {
      sessionManager.onOpen(this);
      sendGreeting();
      startKeepalive();
    } catch (WebApplicationException ex) {
      LOG.severe(ex.getMessage());
      sessionError();
    }
  }

  /**
   * Get the SseSession cookies
   *
   * @return the Session Cookies
   * @throws WebApplicationException if there is an issue with the request context
   */
  public Cookie[] getCookies() throws WebApplicationException {
    try {
      HttpServletRequest r = (HttpServletRequest) this.asyncContext.getRequest();
      return r.getCookies();
    } catch (ClassCastException | NullPointerException | IllegalStateException ex) {
      throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Gets a cookie by name
   *
   * @param cookieName the name of the cookie
   * @return the cookie, if found
   * @throws WebApplicationException if the cookie is not found
   */
  public Cookie getCookie(String cookieName) throws WebApplicationException {
    try {
      HttpServletRequest r = (HttpServletRequest) this.asyncContext.getRequest();
      Cookie[] cookies = r.getCookies();
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if (cookie.getName().equals(cookieName)) {
            return cookie;
          }
        }
      }
      throw new WebApplicationException(Response.Status.BAD_REQUEST);
    } catch (ClassCastException | NullPointerException | IllegalStateException ex) {
      LOG.severe(ex.getMessage());
      throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * gets the value of a cookie as a string
   *
   * @param cookieName the cookie we are searching for
   * @return the value field of the cookie
   * @throws WebApplicationException if there is an issue with the request context
   */
  public String getCookieValue(String cookieName) throws WebApplicationException {
    return getCookie(cookieName).getValue();
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 47 * hash + Objects.hashCode(this.asyncContext);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final SseSession other = (SseSession) obj;
    return this.asyncContext.equals(other.asyncContext);
  }

  @Override
  public String toString() {
    return "SseSession{" + "asyncContext=" + asyncContext + '}';
  }

  private void sendGreeting() {
    pushEvent(WELCOME_EVENT);
  }

  private void cancelRunningTasks() {
    for (Future future : pushFutures) {
      future.cancel(true);
    }
  }

  private class SseSessionListener implements AsyncListener {

    @Override
    public void onComplete(AsyncEvent ae) throws IOException {
      LOG.log(Level.FINEST, "{0} has completed", ae.toString());
    }

    @Override
    public void onTimeout(AsyncEvent ae) throws IOException {
      LOG.log(Level.FINEST, "{0} has timed out", ae.toString());
    }

    @Override
    public void onError(AsyncEvent ae) throws IOException {
      LOG.log(Level.FINEST, "{0} has an error", ae.toString());
    }

    @Override
    public void onStartAsync(AsyncEvent ae) throws IOException {
      LOG.log(Level.FINEST, "{0} has started", ae.toString());
    }
  }

  private class SsePushRunnable implements Runnable {

    private final SseEvent event;

    SsePushRunnable(SseEvent event) {
      this.event = event;
    }

    @Override
    public void run() {
      if (!Thread.interrupted()) {
        try {
          if (event != null) {
            PrintWriter printWriter = asyncContext.getResponse().getWriter();
            printWriter.write(event.getEventString());
            asyncContext.getResponse().flushBuffer();
          }
        } catch (IOException ex) {
          closeSession();
          LOG.finest(ex.getMessage());
        }
      }
    }
  }

  /**
   * A class that pings the session regularly
   */
  private class KeepaliveRunner implements Runnable {

    @Override
    public void run() {
      if (!Thread.interrupted()) {
        try {
          pushEvent(PING_EVENT);
        } finally {
          LOG.finest("Resechedueling Keepalive thread");
          schedueleKeepalive(this);
        }
      }
    }

  }

  /**
   * Schedules the keepalive runner
   */
  private void startKeepalive() {
    schedueleKeepalive(new KeepaliveRunner());
    LOG.finest("Started Keepalive runner");
  }

  private void stopKeepalive() {
    if (keepaliveFuture != null) {
      keepaliveFuture.cancel(true);
    }
  }

  /**
   * shedules a keepalive runner with the globaly set refresh interval in seconds
   *
   * @param runner the runner to reshecuele
   */
  private void schedueleKeepalive(KeepaliveRunner runner) {
    keepaliveFuture = SseSessionKeepAlive.getService().schedule(runner, SseSessionKeepAlive.getInterval(), TimeUnit.SECONDS);
  }

}
