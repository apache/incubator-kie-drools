/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.service;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
abstract class RunnableBaseTest<T extends RunnableBase> {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected static final long TEST_TIMEOUT = 80;

    protected T runnableBase;

    @BeforeEach
    public void setUp() {
        runnableBase = createRunnableBase();
    }

    protected abstract T createRunnableBase();

    protected CompletableFuture<Void> startRunnableBase() {
        return CompletableFuture.runAsync(runnableBase);
    }

    @Test
    @Timeout(TEST_TIMEOUT)
    void destroy() throws Exception {
        CompletableFuture<Void> future = startRunnableBase();
        assertThat(runnableBase.isAlive()).isTrue();
        runnableBase.destroy();
        future.get();
        assertThat(runnableBase.isDestroyed()).isTrue();
    }
}