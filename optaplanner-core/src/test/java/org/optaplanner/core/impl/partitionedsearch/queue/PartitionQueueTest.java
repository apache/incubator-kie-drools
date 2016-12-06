/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.partitionedsearch.queue;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.optaplanner.core.impl.partitionedsearch.scope.PartitionChangeMove;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

import static org.junit.Assert.*;

public class PartitionQueueTest {

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Test
    public void addMove() throws ExecutionException, InterruptedException {
        PartitionQueue<TestdataSolution> partitionQueue = new PartitionQueue<>(3);
        Iterator<PartitionChangeMove<TestdataSolution>> it = partitionQueue.iterator();

        PartitionChangeMove<TestdataSolution> moveA1 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(0, moveA1)).get();
        assertSame(moveA1, it.next());

        PartitionChangeMove<TestdataSolution> moveB1 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(1, moveB1)).get(); // Skipped
        PartitionChangeMove<TestdataSolution> moveB2 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(1, moveB2)).get(); // Skipped
        PartitionChangeMove<TestdataSolution> moveB3 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(1, moveB3)).get();
        assertSame(moveB3, it.next());

        PartitionChangeMove<TestdataSolution> moveA2 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(0, moveA2)).get(); // Skipped
        PartitionChangeMove<TestdataSolution> moveA3 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(0, moveA3)).get();
        PartitionChangeMove<TestdataSolution> moveB4 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(1, moveB4)).get();
        assertSame(moveA3, it.next());
        assertSame(moveB4, it.next());

        PartitionChangeMove<TestdataSolution> moveB5 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(1, moveB5)).get(); // Skipped
        PartitionChangeMove<TestdataSolution> moveA4 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(0, moveA4)).get(); // Skipped
        PartitionChangeMove<TestdataSolution> moveA5 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(0, moveA5)).get(); // Skipped
        PartitionChangeMove<TestdataSolution> moveB6 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(1, moveB6)).get();
        PartitionChangeMove<TestdataSolution> moveC1 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(2, moveC1)).get();
        PartitionChangeMove<TestdataSolution> moveA6 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(0, moveA6)).get();
        assertSame(moveB6, it.next());
        assertSame(moveA6, it.next());
        assertSame(moveC1, it.next());


        executorService.submit(() -> partitionQueue.addFinish(0)).get();
        PartitionChangeMove<TestdataSolution> moveC2 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(2, moveC2)).get();
        executorService.submit(() -> partitionQueue.addFinish(1)).get();
        assertSame(moveC2, it.next());

        executorService.submit(() -> partitionQueue.addFinish(2)).get();
        assertSame(false, it.hasNext());
    }

    @Test
    public void addFinishWithNonEmptyQueue() throws ExecutionException, InterruptedException {
        PartitionQueue<TestdataSolution> partitionQueue = new PartitionQueue<>(3);
        Iterator<PartitionChangeMove<TestdataSolution>> it = partitionQueue.iterator();

        PartitionChangeMove<TestdataSolution> moveA1 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(0, moveA1)).get();
        executorService.submit(() -> partitionQueue.addFinish(0)).get();
        PartitionChangeMove<TestdataSolution> moveC1 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(2, moveC1)).get();
        PartitionChangeMove<TestdataSolution> moveC2 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(2, moveC2)).get();
        executorService.submit(() -> partitionQueue.addFinish(2)).get();
        executorService.submit(() -> partitionQueue.addFinish(1)).get();
        assertSame(true, it.hasNext());
        assertSame(moveA1, it.next());
        assertSame(true, it.hasNext());
        assertSame(moveC2, it.next());
        assertSame(false, it.hasNext());
    }

    @Test()
    public void addExceptionWithNonEmptyQueue() throws ExecutionException, InterruptedException {
        PartitionQueue<TestdataSolution> partitionQueue = new PartitionQueue<>(3);
        Iterator<PartitionChangeMove<TestdataSolution>> it = partitionQueue.iterator();

        PartitionChangeMove<TestdataSolution> moveA1 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(0, moveA1)).get();
        executorService.submit(() -> partitionQueue.addFinish(0)).get();
        PartitionChangeMove<TestdataSolution> moveC1 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(2, moveC1)).get();
        PartitionChangeMove<TestdataSolution> moveC2 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(2, moveC2)).get();
        IllegalArgumentException exception = new IllegalArgumentException();
        executorService.submit(() -> partitionQueue.addExceptionThrown(1, exception)).get();
        PartitionChangeMove<TestdataSolution> moveB1 = buildMove();
        executorService.submit(() -> partitionQueue.addMove(1, moveB1)).get();
        executorService.submit(() -> partitionQueue.addFinish(1)).get();
        assertSame(true, it.hasNext());
        assertSame(moveA1, it.next());
        assertSame(true, it.hasNext());
        assertSame(moveC2, it.next());
        try {
            assertSame(false, it.hasNext());
        } catch (RuntimeException e) {
            assertSame(exception, e.getCause());
            return;
        }
        fail("There was no RuntimeException thrown.");
    }

    public PartitionChangeMove<TestdataSolution> buildMove() {
        return new PartitionChangeMove<>(null);
    }

}
