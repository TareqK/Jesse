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

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.ws.rs.sse.InboundSseEvent;

/**
 * A class to watch for an SseEvent by matching the whole event string
 */
public abstract class SseEventWatcher<T> implements Consumer<InboundSseEvent> {

  private final T toWatch;
  private final CompletableFuture<T> completableFuture;

  public SseEventWatcher(T watchedEvent) {
    this.toWatch = watchedEvent;
    this.completableFuture = new CompletableFuture();
  }

  @Override
  public void accept(InboundSseEvent inboundSseEvent) {
    if (toWatch != null && inboundSseEvent != null && matches(inboundSseEvent, toWatch)) {
      completableFuture.complete(toWatch);
    }
  }

  /**
   * Check if the event matches a criteria
   *
   * @param event the event that has arrived
   * @param toWatch the object we are comparing it to
   * @return true if it matches, false otherwise
   */
  public abstract boolean matches(InboundSseEvent event, T toWatch);

  public CompletableFuture<T> getFuture() {
    return this.completableFuture;
  }

}
