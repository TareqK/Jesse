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

import java.util.function.Consumer;
import javax.ws.rs.sse.InboundSseEvent;
import me.kisoft.jesse.DefaultSessionManager;
import me.kisoft.jesse.SseEvent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author tareq
 */
public class SetupTest extends JesseTest {

  @BeforeClass
  public static void setup() throws Exception {
    setupServer(null);
  }

  @Test
  public void testTest() throws InterruptedException {

    source.register(new Consumer<InboundSseEvent>() {
      @Override
      public void accept(InboundSseEvent inboundSseEvent) {
        System.out.println(inboundSseEvent.getName());
        synchronized (syncObject) {
          syncObject.notifyAll();
        }
      }
    });
    DefaultSessionManager.broadcastEvent(SseEvent.getBuilder().data("test").event("test").build());
    synchronized (syncObject) {
      syncObject.wait();
    }

  }

  @AfterClass
  public static void shutdown() throws Exception {
    stopServer();
  }
}
