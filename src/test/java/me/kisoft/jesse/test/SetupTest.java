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

import java.util.HashMap;
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
public class SetupTest extends JesseTest {

  private static final HashMap<String, String> TEST_MAP = new HashMap<>();

  @BeforeClass
  public static void setup() throws Exception {
    TEST_MAP.put("me.kisoft.jesse.session.keepalive.enabled", "false");
    initializeTestEnvironment(TEST_MAP);
  }

  @Test
  public void eventSentTest() {
    SseEvent event = SseEvent.getBuilder().data("test").event("test").build();
    assertEquals(event, broadcastAndListen(event, 1));
  }

  @Test
  public void eventValueTest() {
    SseEvent event = SseEvent.getBuilder().data("test").event("test").build();
    assertNotEquals(SseEvent.getBuilder().build(), broadcastAndListen(event, 1));
  }

  @AfterClass
  public static void shutdown() throws Exception {
    destroyTestEnvironment();
  }
}
