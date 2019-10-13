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

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSource;
import me.kisoft.jesse.DefaultSessionManager;
import me.kisoft.jesse.SseEvent;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author tareq
 */
public class BroadcastTest extends JesseTest {

  @BeforeClass
  public static void setup() throws Exception {
    HashMap<String, String> map = new HashMap<>();
    //map.put("me.kisoft.jesse.session.keepalive.enabled", "false");
    initializeTestEnvironment(map);
  }

  @Test
  public void eventSentTest() {
    SseEvent event = SseEvent.getBuilder().data("test").event("test").build();
    assertEquals(event, broadcastAndListen(event, 50));
  }

  @Test
  public void eventValueTest() {
    SseEvent event = SseEvent.getBuilder().data("test").event("test").build();
    assertNotEquals(SseEvent.getBuilder().build(), broadcastAndListen(event, 50));
  }

  @Test
  public void serializeJsonTest() throws JsonProcessingException {
    HashMap<String, Object> data = new HashMap<>();
    data.put("test", "thing1");
    data.put("thing2", "stuff");

    SseEvent event = SseEvent.getBuilder()
     .data(data)
     .event("test")
     .mediaType(MediaType.APPLICATION_JSON)
     .build();

    assertEquals(event, broadcastAndListen(event, 500));
  }

  @Test
  public void serializeXmlTest() throws JsonProcessingException {
    HashMap<String, Object> data = new HashMap<>();
    data.put("test", "thing1");
    data.put("thing2", "stuff");

    SseEvent event = SseEvent.getBuilder()
     .data(data)
     .event("test")
     .mediaType(MediaType.APPLICATION_XML)
     .build();

    assertEquals(event, broadcastAndListen(event, 500));
  }

  @Test
  public void broadcastManyTest() {
    SseEvent event = SseEvent.getBuilder().data("test").event("test").build();
    for (int i = 0; i < 15; i++) {
      createSource();
    }
    List<Future<SseEvent>> futureList = new ArrayList<>();
    for (SseEventSource source : getSourcesList()) {
      futureList.add(listen(source, event));
    }
    DefaultSessionManager.broadcastEvent(event);
    int i = 0;
    for (Future future : futureList) {
      SseEvent resolved = resolve((CompletableFuture<SseEvent>) future, 30);
      assertEquals(event, resolved);
    }
  }

  @Test
  public void closeSessionTest() {
    SseEventSource source = createSource();
    source.close();
    getSourcesList().remove(source);
  }

  @AfterClass
  public static void shutdown() throws Exception {
    destroyTestEnvironment();
  }
}
