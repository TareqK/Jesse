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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSource;
import me.kisoft.jesse.DefaultSessionManager;
import me.kisoft.jesse.SseEvent;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tareq
 */
public class BroadcastTest extends JesseTest {

  @BeforeAll
  public static void setup() throws Exception {
    HashMap<String, String> map = new HashMap<>();
    initializeTestEnvironment(map);
  }

  @Test
  public void eventSentTest() {
    SseEvent event = SseEvent
     .getBuilder()
     .data("test")
     .id(getId())
     .event(getEventString())
     .build();

    assertEquals(event, broadcastAndListen(event, new SseEventIdWatcher(event)));
  }

  @Test
  public void eventValueTest() {
    SseEvent event = SseEvent.getBuilder().data("test").id(getId()).event(getEventString()).build();
    assertNotEquals(SseEvent.getBuilder().build(), broadcastAndListen(event, new SseEventIdWatcher(event)));
  }

  @Test
  public void serializeJsonTest() throws JsonProcessingException {
    HashMap<String, Object> data = new HashMap<>();
    data.put("test", "thing1");
    data.put("thing2", "stuff");
    ObjectMapper mapper = new ObjectMapper();
    SseEvent event = SseEvent
     .getBuilder()
     .data(data)
     .id(getId())
     .event(getEventString())
     .mediaType(MediaType.APPLICATION_JSON)
     .build();

    assertEquals(mapper.writeValueAsString(data), broadcastAndListen(event, new SseEventIdWatcher(event)).getDataAsString());
  }

  @Test
  public void serializeXmlTest() throws JsonProcessingException {
    HashMap<String, Object> data = new HashMap<>();
    data.put("test", "thing1");
    data.put("thing2", "stuff");
    XmlMapper mapper = new XmlMapper();
    SseEvent event = SseEvent
     .getBuilder()
     .data(data)
     .id(getId())
     .event(getEventString())
     .mediaType(MediaType.APPLICATION_XML)
     .build();

    assertEquals(mapper.writeValueAsString(data), broadcastAndListen(event, new SseEventIdWatcher(event)).getDataAsString());
  }

  @Test
  public void broadcastManyTest() {
    SseEvent event = SseEvent
     .getBuilder()
     .data("test")
     .id(getId())
     .event(getEventString())
     .build();

    for (int i = 0; i < 15; i++) {
      createSource();
    }
    List<Future<SseEvent>> futureList = new ArrayList<>();
    for (SseEventSource source : getSourcesList()) {
      futureList.add(listen(source, event, new SseEventIdWatcher(event)));
    }
    DefaultSessionManager.broadcastEvent(event);
    for (Future future : futureList) {
      SseEvent resolved = resolve((CompletableFuture<SseEvent>) future, getDefaultTimeout());
      assertEquals(event, resolved);
    }
  }

  @Test
  public void checkPingTest() {
    SseEvent event = SseEvent
     .getBuilder()
     .event("ping")
     .build();

    CompletableFuture<SseEvent> listen = listen(event, new SseEventNameWatcher(event));
    assertTrue(resolve(listen, 3000) != null);

    CompletableFuture<SseEvent> listen2 = listen(event, new SseEventNameWatcher(event));
    assertTrue(resolve(listen2, 3000) != null);
  }

  @Test
  public void closeSessionTest() {
    SseEventSource source = createSource();
    source.close();
    getSourcesList().remove(source);
  }

  @AfterAll
  public static void shutdown() throws Exception {
    destroyTestEnvironment();
  }
}
