/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.partitionedsearch;

import java.util.concurrent.CountDownLatch;

import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestdataSleepingEntity extends TestdataEntity {

    private static final Logger logger = LoggerFactory.getLogger(TestdataSleepingEntity.class);
    private CountDownLatch latch;

    public TestdataSleepingEntity() {
    }

    public TestdataSleepingEntity(String code, CountDownLatch cdl) {
        super(code);
        this.latch = cdl;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void setValue(TestdataValue value) {
        super.setValue(value);
        if (latch.getCount() == 0) {
            logger.debug("This entity was already interrupted in the past. Not going to sleep again.");
            return;
        }
        latch.countDown();
        long start = System.currentTimeMillis();
        logger.info("{}.setValue() started and going to sleep.", TestdataSleepingEntity.class.getSimpleName());
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while sleeping", ex);
        }
        logger.info("{}.setValue() interrupted after {}ms.",
                TestdataSleepingEntity.class.getSimpleName(),
                System.currentTimeMillis() - start);
    }

}
