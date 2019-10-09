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

import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;
import me.kisoft.jesse.JesseServlet;
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
  public static final String url = "http://localhost:" + SERVER_PORT + JESSE_BASE;
  static Tomcat tomcat;
  public final Object syncObject = new Object();
  public static SseEventSource source;

  public static void setupServer(Map<String, String> initParameters) throws Exception {
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
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(url);
    source = SseEventSource.target(target).build();

    source.open();

  }

  public static void stopServer() throws Exception {
    source.close();
    tomcat.stop();
    tomcat.destroy();
  }

}
