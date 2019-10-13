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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author tareq
 */
public class JesseExecutorService implements ScheduledExecutorService {

  private final ScheduledExecutorService executor;
  private static JesseExecutorService instance = getInstance();

  /**
   * Gets the current instance of the jesse executor service
   *
   * @return the current instance of the executor service
   */
  protected static final JesseExecutorService getInstance() {
    if (instance == null) {
      instance = new JesseExecutorService();
    }
    return instance;
  }

  private JesseExecutorService() {
    this.executor = Executors.newScheduledThreadPool(150);
  }

  @Override
  public void shutdown() {
    executor.shutdown();
  }

  @Override
  public List<Runnable> shutdownNow() {
    return executor.shutdownNow();
  }

  @Override
  public boolean isShutdown() {
    return executor.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return executor.isTerminated();
  }

  @Override
  public boolean awaitTermination(long l, TimeUnit tu) throws InterruptedException {
    return executor.awaitTermination(l, tu);
  }

  @Override
  public <T> Future<T> submit(Callable<T> clbl) {
    return executor.submit(clbl);
  }

  @Override
  public <T> Future<T> submit(Runnable r, T t) {
    return executor.submit(r, t);
  }

  @Override
  public Future<?> submit(Runnable r) {
    return executor.submit(r);
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> clctn) throws InterruptedException {
    return executor.invokeAll(clctn);
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> clctn, long l, TimeUnit tu) throws InterruptedException {
    return executor.invokeAll(clctn, l, tu);
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> clctn) throws InterruptedException, ExecutionException {
    return executor.invokeAny(clctn);
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> clctn, long l, TimeUnit tu) throws InterruptedException, ExecutionException, TimeoutException {
    return executor.invokeAny(clctn, l, tu);
  }

  @Override
  public void execute(Runnable r) {
    executor.execute(r);
  }

  @Override
  public ScheduledFuture<?> schedule(Runnable r, long l, TimeUnit tu) {
    return executor.schedule(r, l, tu);
  }

  @Override
  public <V> ScheduledFuture<V> schedule(Callable<V> clbl, long l, TimeUnit tu) {
    return executor.schedule(clbl, l, tu);
  }

  @Override
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long l, long l1, TimeUnit tu) {
    return executor.scheduleAtFixedRate(r, l, l1, tu);
  }

  @Override
  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable r, long l, long l1, TimeUnit tu) {
    return executor.scheduleWithFixedDelay(r, l, l1, tu);
  }

}
