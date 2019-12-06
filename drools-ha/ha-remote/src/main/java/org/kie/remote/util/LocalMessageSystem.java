/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.remote.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalMessageSystem {

  private Logger logger = LoggerFactory.getLogger(LocalMessageSystem.class);
  private Map<String, BlockingQueue<Object>> queues = new HashMap<>();

  private LocalMessageSystem() {
  }

  private BlockingQueue<Object> queueForTopic(String topic) {
    return queues.computeIfAbsent(topic,
                                  k -> new LinkedBlockingQueue<>());
  }

  public void put(String topic, Object message) {
    if (!queueForTopic(topic).offer(message)) {
      logger.info("msg :{} not added in the topic:{}", message, topic);
    }
  }

  public Object peek(String topic) {
    return queueForTopic(topic).peek();
  }

  public Object poll(String topic) {
    return queueForTopic(topic).poll();
  }

  public Object poll(String topic, int durationMillis) {
    try {
      return queueForTopic(topic).poll(durationMillis, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new LocalMessageSystemException(e.getMessage(), e);
    }
  }

  public static LocalMessageSystem get() {
    return LazyHolder.get();
  }

  private static class LazyHolder {

    private static final LocalMessageSystem INSTANCE = new LocalMessageSystem();

    public static LocalMessageSystem get() {
      return INSTANCE;
    }
  }
}
