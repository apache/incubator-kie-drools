/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.solver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class ConsumerSupportTest {

    private ConsumerSupport<TestdataSolution, Long> consumerSupport;

    @AfterEach
    void close() {
        consumerSupport.close();
    }

    @Test
    @Timeout(60)
    void skipAhead() throws InterruptedException {
        CountDownLatch consumptionPaused = new CountDownLatch(1);
        CountDownLatch consumptionCompleted = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();
        List<TestdataSolution> consumedSolutions = Collections.synchronizedList(new ArrayList<>());
        consumerSupport = new ConsumerSupport<>(1L, testdataSolution -> {
            try {
                consumptionPaused.await();
                consumedSolutions.add(testdataSolution);
                if (testdataSolution.getEntityList().size() == 3) { // The last best solution.
                    consumptionCompleted.countDown();
                }
            } catch (InterruptedException e) {
                error.set(new IllegalStateException("Interrupted waiting.", e));
            }
        }, null, null);

        // This solution may be skipped.
        consumerSupport.consumeIntermediateBestSolution(TestdataSolution.generateSolution(1, 1));
        // This solution should be skipped.
        consumerSupport.consumeIntermediateBestSolution(TestdataSolution.generateSolution(2, 2));
        // This solution should never be skipped.
        consumerSupport.consumeIntermediateBestSolution(TestdataSolution.generateSolution(3, 3));

        consumptionPaused.countDown();
        consumptionCompleted.await();
        assertThat(consumedSolutions).hasSizeBetween(1, 2);
        if (consumedSolutions.size() == 2) {
            assertThat(consumedSolutions.get(0).getEntityList()).hasSize(1);
        }
        assertThat(consumedSolutions.get(consumedSolutions.size() - 1).getEntityList()).hasSize(3);

        if (error.get() != null) {
            fail("Exception during consumption.", error.get());
        }
    }
}
