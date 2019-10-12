/*
 * Copyright 2019 tareq.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.kisoft.jesse.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import me.kisoft.jesse.DefaultSessionManager;
import me.kisoft.jesse.JesseServlet;
import me.kisoft.jesse.SseEvent;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;

/**
 *
 * @author tareq
 */
public class JesseTest {

  public final static String JESSE_BASE = "/jesse";
  public final static String APPLICATION_BASE = "";
  public final static String JESSE_NAME = "Jesse Servlet";
  public final static int SERVER_PORT = 9090;
  public final static int TIMEOUT = 30000;
  public static final String URL = "http://localhost:" + SERVER_PORT + JESSE_BASE;
  public static final List<SseEventSource> SOURCES = new ArrayList<>();
  static Tomcat tomcat;
  public final Object syncObject = new Object();
  public static SseEventSource defaultSource;

  /**
   *
   *
   *
   * Starts up the tomcat server and sets the init params for the JesseServlet. Additionally, creates a default SseEvent source for basic
   * tests.
   *
   * @param initParameters if not null, sets the init params for the jesse servlet. If null, will use default values. The default values are
   * true for sessionKeepalive and a keepalive interval of 1 second
   * @throws Exception
   */
  public static void initializeTestEnvironment(Map<String, String> initParameters) throws Exception {
    /*
    XXX : i shouldnt be starting tomcat every test. It should be enough to just unmount
    and remount the servlet to cut down testing time
     */

    tomcat = new Tomcat();

    tomcat.setPort(SERVER_PORT);

    Context ctx = tomcat.addContext(APPLICATION_BASE, System.getProperty("java.io.tmpdir"));

    Wrapper wrapper = Tomcat.addServlet(ctx, JESSE_NAME, new JesseServlet());
    wrapper.addInitParameter("me.kisoft.jesse.session.keepalive.enabled", "true");
    wrapper.addInitParameter("me.kisoft.jesse.session.keepalive.interval", "1");
    ctx.addServletMappingDecoded(JESSE_BASE, JESSE_NAME);
    if (initParameters != null) {
      for (String key : initParameters.keySet()) {
        wrapper.addInitParameter(key, initParameters.get(key));
      }
    }
    wrapper.setAsyncSupported(true);
    tomcat.start();
    defaultSource = getSource();

  }

  /**
   * Closes all open SseEventSources and Shuts down the tomcat server
   *
   * @throws Exception if there is any error.
   */
  public static void destroyTestEnvironment() throws Exception {
    for (SseEventSource source : SOURCES) {
      try {
        source.close();
      } catch (Exception e) {
        //just a catch all for silent destruction
      }
    }
    tomcat.stop();
    tomcat.destroy();
  }

  /**
   *
   * returns the default source for this test
   *
   * @return an SseEvent source
   */
  public static SseEventSource getDefaultSource() {
    /*XXX : Should be a factory somehow.*/
    return defaultSource;
  }

  /**
   *
   * Gets an SseEvent source, opens it, and adds it to the source list for resource cleanup.
   *
   * @return an SseEventSource listening to Jesse
   */
  public static SseEventSource getSource() {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(URL);
    SseEventSource source = SseEventSource.target(target).build();
    source.open();
    SOURCES.add(source);
    return source;
  }

  /**
   * Listens for an SseEvent
   *
   * @param source the defaultSource to listen into
   * @param event the event to send and listen for
   * @return the Future for this Sse Event
   */
  public CompletableFuture<SseEvent> listen(SseEventSource source, SseEvent event) {
    SseEventWatcher watcher = new SseEventWatcher(event);
    source.register(watcher);
    return watcher.getFuture();

  }

  /**
   * Listens for an SseEvent on the default source
   *
   * @param event the event to send and listen for
   * @return the Future for this Sse Event
   */
  CompletableFuture<SseEvent> listen(SseEvent event) {
    return listen(defaultSource, event);
  }

  /**
   * Broadcasts and listens for an SseEvent.The event must have the exact same content as the sent event to match
   *
   * @param source the source to listen in for
   * @param event the event to send and listen for
   * @param timeout the timeout in milliseconds
   * @return the SseEvent, if found within the timeout period, null otherwise
   */
  public SseEvent broadcastAndListen(SseEventSource source, SseEvent event, long timeout) {
    CompletableFuture<SseEvent> future = listen(source, event);
    DefaultSessionManager.broadcastEvent(event);
    return resolve(future, timeout);
  }

  /**
   * Broadcasts and listens for an SseEvent on the default source.The event must have the exact same content as the sent event to match
   *
   * @param event the event to send and listen for
   * @param timeout the timeout in milliseconds
   * @return the SseEvent, if found within the timeout period, null otherwise
   */
  public SseEvent broadcastAndListen(SseEvent event, long timeout) {
    return broadcastAndListen(defaultSource, event, timeout);
  }

  /**
   * Attempts to resolve a future for an SseEvent.
   *
   * @param future the future to resolve
   * @param timeout the timeout in milliseconds
   * @return The SseEvent if it was resolved within the time period, null otherwise.
   */
  public SseEvent resolve(CompletableFuture<SseEvent> future, long timeout) {
    try {
      return future.get(timeout, TimeUnit.MILLISECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException ex) {
      return null;
    }
  }

  /**
   * A class to watch for an SseEvent by matching the whole event string
   */
  protected class SseEventWatcher implements Consumer<InboundSseEvent> {

    private final SseEvent toWatch;
    private final CompletableFuture<SseEvent> completableFuture;

    public SseEventWatcher(SseEvent watchedEvent) {
      this.toWatch = watchedEvent;
      this.completableFuture = new CompletableFuture();
    }

    @Override
    public void accept(InboundSseEvent inboundSseEvent) {
      if (inboundSseEvent.getId() == null ? toWatch.getId() == null : inboundSseEvent.getId().equals(toWatch.getId())) {
        completableFuture.complete(toWatch);
      }
    }

    public CompletableFuture<SseEvent> getFuture() {
      return this.completableFuture;
    }
  }
}
