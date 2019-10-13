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
package me.kisoft.jesse;

import java.util.concurrent.Future;
import me.kisoft.jesse.SseSession.SsePushRunnable;

/**
 *
 * @author tareq
 */
public class SseEventPusher {

  private SseEventPusher() {
    throw new IllegalArgumentException("This is a Utility Class");
  }

  /**
   * submits an SsePushRunnable to the underlying Executor service
   *
   * @param runnable the runnable to push
   * @return a Future task that resolves nothing, to be used to cancel the push if needed.
   */
  public static Future submit(SsePushRunnable runnable) {
    return JesseExecutorService.getInstance().submit(runnable);
  }

}
