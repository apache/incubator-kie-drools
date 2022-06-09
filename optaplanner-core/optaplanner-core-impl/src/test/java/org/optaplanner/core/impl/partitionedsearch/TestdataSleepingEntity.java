package org.optaplanner.core.impl.partitionedsearch;

import java.util.concurrent.CountDownLatch;

import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestdataSleepingEntity extends TestdataEntity {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestdataSleepingEntity.class);
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
            LOGGER.debug("This entity was already interrupted in the past. Not going to sleep again.");
            return;
        }
        latch.countDown();
        long start = System.currentTimeMillis();
        LOGGER.info("{}.setValue() started and going to sleep.", TestdataSleepingEntity.class.getSimpleName());
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while sleeping", ex);
        }
        LOGGER.info("{}.setValue() interrupted after {}ms.",
                TestdataSleepingEntity.class.getSimpleName(),
                System.currentTimeMillis() - start);
    }

}
