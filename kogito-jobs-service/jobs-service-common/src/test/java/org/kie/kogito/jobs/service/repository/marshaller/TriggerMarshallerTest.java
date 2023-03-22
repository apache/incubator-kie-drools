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

package org.kie.kogito.jobs.service.repository.marshaller;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.IntervalTrigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;
import org.kie.kogito.timer.impl.SimpleTimerTrigger;

import io.vertx.core.json.JsonObject;

import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(jsonObject).isEqualTo(new JsonObject()
                .put("startTime", startTime.getTime())
                .put("endTime", endTime.getTime())
                .put("nextFireTime", nextFireTime.getTime())
                .put("repeatLimit", repeatLimit)
                .put("repeatCount", repeatCount)
                .put("period", period)
                .put("classType", IntervalTrigger.class.getName()));
    }

    @Test
    void marshallPointInTimeTriggerAccessor() {
        Date time = new Date();
        PointInTimeTrigger trigger = new PointInTimeTrigger(time.getTime(), null, null);
        JsonObject jsonObject = marshaller.marshall(trigger);
        assertThat(jsonObject).isEqualTo(new JsonObject()
                .put("nextFireTime", time.getTime())
                .put("classType", PointInTimeTrigger.class.getName()));
    }

    @Test
    void marshallSimpleTimerTrigger() {
        Date startTime = new Date();
        long period = 4;
        ChronoUnit periodUnit = ChronoUnit.HOURS;
        int repeatCount = 3;
        Date endTime = new Date(startTime.getTime() + 10000);
        String zoneId = "+02:00";

        SimpleTimerTrigger trigger = new SimpleTimerTrigger(startTime, period, periodUnit, repeatCount, endTime, zoneId);

        JsonObject jsonObject = marshaller.marshall(trigger);
        assertThat(jsonObject).isEqualTo(new JsonObject()
                .put("startTime", startTime.getTime())
                .put("period", period)
                .put("periodUnit", periodUnit.name())
                .put("repeatCount", repeatCount)
                .put("endTime", endTime.getTime())
                .put("zoneId", zoneId)
                .put("nextFireTime", startTime.getTime())
                .put("currentRepeatCount", 0)
                .put("endTimeReached", false)
                .put("classType", SimpleTimerTrigger.class.getName()));
    }

    @Test
    void marshallNull() {
        JsonObject jsonObject = marshaller.marshall(null);
        assertThat(jsonObject).isNull();
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

        assertThat(trigger).hasToString(expected.toString());

    }

    @Test
    void unmarshallPointInTimeTriggerAccessor() {
        Date time = new Date();
        JsonObject jsonObject = new JsonObject()
                .put("nextFireTime", time.getTime())
                .put("classType", PointInTimeTrigger.class.getName());
        Trigger trigger = marshaller.unmarshall(jsonObject);
        assertThat(trigger).hasToString(new PointInTimeTrigger(time.getTime(), null, null).toString());
    }

    @Test
    void unmarshalSimpleTimerTrigger() {
        Date startTime = new Date();
        long period = 4;
        ChronoUnit periodUnit = ChronoUnit.HOURS;
        int repeatCount = 3;
        Date endTime = new Date(startTime.getTime() + 10000);
        String zoneId = "+02:00";

        JsonObject json = new JsonObject()
                .put("startTime", startTime.getTime())
                .put("period", period)
                .put("periodUnit", periodUnit.name())
                .put("repeatCount", repeatCount)
                .put("endTime", endTime.getTime())
                .put("zoneId", zoneId)
                .put("nextFireTime", startTime.getTime())
                .put("currentRepeatCount", 0)
                .put("endTimeReached", false)
                .put("classType", SimpleTimerTrigger.class.getName());

        Trigger trigger = marshaller.unmarshall(json);
        assertThat(trigger).isExactlyInstanceOf(SimpleTimerTrigger.class);
        SimpleTimerTrigger simpleTimerTrigger = (SimpleTimerTrigger) trigger;
        assertThat(simpleTimerTrigger.getStartTime()).isEqualTo(startTime);
        assertThat(simpleTimerTrigger.getPeriod()).isEqualTo(period);
        assertThat(simpleTimerTrigger.getPeriodUnit()).isEqualTo(periodUnit);
        assertThat(simpleTimerTrigger.getRepeatCount()).isEqualTo(simpleTimerTrigger.getRepeatCount());
        assertThat(simpleTimerTrigger.getEndTime()).isEqualTo(endTime);
        assertThat(simpleTimerTrigger.getZoneId()).isEqualTo(zoneId);
        assertThat(simpleTimerTrigger.getNextFireTime()).isEqualTo(startTime);
        assertThat(simpleTimerTrigger.hasNextFireTime()).isEqualTo(startTime);
        assertThat(simpleTimerTrigger.getCurrentRepeatCount()).isZero();
        assertThat(simpleTimerTrigger.isEndTimeReached()).isFalse();
    }

    @Test
    void unmarshallNull() {
        Trigger trigger = marshaller.unmarshall(null);
        assertThat(trigger).isNull();
    }

    @Test
    void unmarshallInvalid() {
        Date time = new Date();
        JsonObject jsonObject = new JsonObject().put("nextFireTime", time.getTime());
        Trigger trigger = marshaller.unmarshall(jsonObject);
        assertThat(trigger).isNull();
    }
}
