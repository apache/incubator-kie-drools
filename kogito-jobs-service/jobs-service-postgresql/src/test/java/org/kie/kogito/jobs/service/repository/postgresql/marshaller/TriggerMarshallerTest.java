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

package org.kie.kogito.jobs.service.repository.postgresql.marshaller;

import java.util.Date;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.IntervalTrigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;

import io.vertx.core.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TriggerMarshallerTest {

    TriggerMarshaller marshaller = new TriggerMarshaller();

    @Test
    void marshallIntervalTrigger() {
        Date startTime = new Date();
        Date endTime = new Date();
        Date nextFireTime = new Date();
        Random random = new Random();
        int repeatLimit = random.nextInt();
        int repeatCount = random.nextInt();
        long period = random.nextLong();

        IntervalTrigger trigger = new IntervalTrigger();
        trigger.setStartTime(startTime);
        trigger.setEndTime(endTime);
        trigger.setRepeatLimit(repeatLimit);
        trigger.setRepeatCount(repeatCount);
        trigger.setNextFireTime(nextFireTime);
        trigger.setPeriod(period);

        JsonObject jsonObject = marshaller.marshall(trigger);

        assertEquals(new JsonObject()
                .put("startTime", startTime.getTime())
                .put("endTime", endTime.getTime())
                .put("nextFireTime", nextFireTime.getTime())
                .put("repeatLimit", repeatLimit)
                .put("repeatCount", repeatCount)
                .put("period", period)
                .put("classType", IntervalTrigger.class.getName()),
                jsonObject);
    }

    @Test
    void marshallPointInTimeTriggerAccessor() {
        Date time = new Date();
        PointInTimeTrigger trigger = new PointInTimeTrigger(time.getTime(), null, null);
        JsonObject jsonObject = marshaller.marshall(trigger);
        assertEquals(new JsonObject()
                .put("nextFireTime", time.getTime())
                .put("classType", PointInTimeTrigger.class.getName()),
                jsonObject);
    }

    @Test
    void marshallNull() {
        JsonObject jsonObject = marshaller.marshall(null);
        assertNull(jsonObject);
    }

    @Test
    void unmarshallIntervalTrigger() {
        Date startTime = new Date();
        Date endTime = new Date();
        Date nextFireTime = new Date();
        Random random = new Random();
        int repeatLimit = random.nextInt();
        int repeatCount = random.nextInt();
        long period = random.nextLong();

        JsonObject jsonObject = new JsonObject()
                .put("startTime", startTime.getTime())
                .put("endTime", endTime.getTime())
                .put("nextFireTime", nextFireTime.getTime())
                .put("repeatLimit", repeatLimit)
                .put("repeatCount", repeatCount)
                .put("period", period)
                .put("classType", IntervalTrigger.class.getName());

        Trigger trigger = marshaller.unmarshall(jsonObject);

        IntervalTrigger expected = new IntervalTrigger();
        expected.setStartTime(startTime);
        expected.setEndTime(endTime);
        expected.setRepeatLimit(repeatLimit);
        expected.setRepeatCount(repeatCount);
        expected.setNextFireTime(nextFireTime);
        expected.setPeriod(period);

        assertEquals(expected.toString(), trigger.toString());

    }

    @Test
    void unmarshallPointInTimeTriggerAccessor() {
        Date time = new Date();
        JsonObject jsonObject = new JsonObject()
                .put("nextFireTime", time.getTime())
                .put("classType", PointInTimeTrigger.class.getName());
        Trigger trigger = marshaller.unmarshall(jsonObject);
        assertEquals(new PointInTimeTrigger(time.getTime(), null, null).toString(), trigger.toString());
    }

    @Test
    void unmarshallNull() {
        Trigger trigger = marshaller.unmarshall(null);
        assertNull(trigger);
    }

    @Test
    void unmarshallInvalid() {
        Date time = new Date();
        JsonObject jsonObject = new JsonObject().put("nextFireTime", time.getTime());
        Trigger trigger = marshaller.unmarshall(jsonObject);
        assertNull(trigger);
    }
}
