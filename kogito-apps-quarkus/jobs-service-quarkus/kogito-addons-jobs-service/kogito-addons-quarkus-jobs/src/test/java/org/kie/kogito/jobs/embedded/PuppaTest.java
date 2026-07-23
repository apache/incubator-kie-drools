/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.jobs.embedded;

import java.util.concurrent.CompletionStage;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.junit.QuarkusTest;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

@QuarkusTest
public class PuppaTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PuppaTest.class);

    @Inject
    Event<PuppaEvent> bus;

    //@Test
    public void puppa() {
        IntStream.range(0, 1).forEach(i -> getJobDetails(i));
    }

    private int getJobDetails(int i) {
        try {
            LOGGER.info("publishJobStatusChange {}", i);
            //            if (eventPublishers.isEmpty()) {
            //                return jobDetails;
            //            }

            CompletionStage<PuppaEvent> puppaEventCompletionStage = bus.fireAsync(new PuppaEvent(i));

            //            CompletableFuture<PuppaEvent> completableFuture = puppaEventCompletionStage.toCompletableFuture();
            //
            //            assertThat(completableFuture).isNotNull();
            ////            try {
            ////                Thread.sleep(5000);
            ////            } catch (InterruptedException e) {
            ////                e.printStackTrace();
            ////            }
            //            assertThat(completableFuture).isDone();

            //completableFuture.get();

            LOGGER.info("fired {}", i);
            return i;
        } catch (Exception e/* InterruptedException | ExecutionException e */) {
            throw new RuntimeException(e);
        }
    }

    public static class PuppaEvent {

        private final int id;

        public PuppaEvent(int id) {
            this.id = id;
        }
    }
}
