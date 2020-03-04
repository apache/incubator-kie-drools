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

package org.kie.remote;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DroolsExecutorTest {

    private static final int ITERATIONS = 3;
    private static final String[] TEST_SIDE_EFFECTS = { "effect1", "effect2" };

    @Test
    public void testGetMaster() {
        final DroolsExecutor master = getMasterExecutor();

        Assertions.assertThat(master).isInstanceOf( DroolsExecutor.Leader.class);
    }

    @Test
    public void testGetSlave() {
        final DroolsExecutor slave = getSlaveExecutor();

        Assertions.assertThat(slave).isInstanceOf(DroolsExecutor.Slave.class);
    }

    @Test
    public void testExecuteRunnableOnMaster() {
        final DroolsExecutor master = getMasterExecutor();

        for (int i = 0; i < ITERATIONS; i++) {
            master.execute(new DummyRunnable());
        }

        final Queue<Serializable> results = master.getAndReset();
        Assertions.assertThat(results).isNotNull();
        Assertions.assertThat(results.size()).isEqualTo(ITERATIONS);
        Assertions.assertThat(results).containsOnly(DroolsExecutor.EmptyResult.INSTANCE);

        Assertions.assertThat(master.getAndReset()).isEmpty();
    }

    @Test
    public void testExecuteSupplierOnMaster() {
        final DroolsExecutor master = getMasterExecutor();

        final String testString = "test string";
        final String resultString = master.execute(() -> testString);
        Assertions.assertThat(resultString).isEqualTo(testString);

        final Queue<Serializable> results = master.getAndReset();
        Assertions.assertThat(results).isNotNull();
        Assertions.assertThat(results).hasSize(1);
        Assertions.assertThat(results.poll()).isEqualTo(testString);

        Assertions.assertThat(master.getAndReset()).isEmpty();
    }

    @Test
    public void testExecuteOnSlave() {
        final DroolsExecutor slave = getSlaveExecutor();
        final Queue<Serializable> sideEffects = new ArrayDeque<>(Arrays.asList(TEST_SIDE_EFFECTS));
        slave.appendSideEffects(sideEffects);

        slave.execute(new DummyRunnable());
        Assertions.assertThat(slave.execute(() -> "test"))
                .isNotNull()
                .isEqualTo(TEST_SIDE_EFFECTS[1]);
        Assertions.assertThat(slave.execute(() -> "test")).isNull();
    }

    private static DroolsExecutor getMasterExecutor() {
        DroolsExecutor.setAsLeader();
        return DroolsExecutor.getInstance();
    }

    private static DroolsExecutor getSlaveExecutor() {
        DroolsExecutor.setAsReplica();
        return DroolsExecutor.getInstance();
    }

    private class DummyRunnable implements Runnable {

        @Override
        public void run() {
        }
    }
}
