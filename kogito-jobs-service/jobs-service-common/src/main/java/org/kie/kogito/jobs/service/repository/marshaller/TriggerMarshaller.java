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
package org.kie.kogito.jobs.service.repository.marshaller;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.IntervalTrigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;
import org.kie.kogito.timer.impl.SimpleTimerTrigger;

import io.vertx.core.json.JsonObject;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TriggerMarshaller implements Marshaller<Trigger, JsonObject> {

    private static final String CLASS_TYPE = "classType";

    @Override
    public JsonObject marshall(Trigger trigger) {
        if (trigger instanceof SimpleTimerTrigger) {
            return JsonObject.mapFrom(new SimpleTimerTriggerAccessor((SimpleTimerTrigger) trigger))
                    .put(CLASS_TYPE, trigger.getClass().getName());
        }
        if (trigger instanceof IntervalTrigger) {
            return JsonObject.mapFrom(new IntervalTriggerAccessor((IntervalTrigger) trigger))
                    .put(CLASS_TYPE, trigger.getClass().getName());
        }
        if (trigger instanceof PointInTimeTrigger) {
            return JsonObject.mapFrom(new PointInTimeTriggerAccessor((PointInTimeTrigger) trigger))
                    .put(CLASS_TYPE, trigger.getClass().getName());
        }
        return null;
    }

    @Override
    public Trigger unmarshall(JsonObject jsonObject) {
        String classType = Optional.ofNullable(jsonObject).map(o -> (String) o.remove(CLASS_TYPE)).orElse(null);
        if (SimpleTimerTrigger.class.getName().equals(classType)) {
            return jsonObject.mapTo(SimpleTimerTriggerAccessor.class).to();
        }
        if (IntervalTrigger.class.getName().equals(classType)) {
            return jsonObject.mapTo(IntervalTriggerAccessor.class).to();
        }
        if (PointInTimeTrigger.class.getName().equals(classType)) {
            return jsonObject.mapTo(PointInTimeTriggerAccessor.class).to();
        }
        return null;
    }

    private static class PointInTimeTriggerAccessor {

        private Long nextFireTime;

        public PointInTimeTriggerAccessor() {
        }

        public PointInTimeTriggerAccessor(PointInTimeTrigger trigger) {
            this.nextFireTime = toTime(trigger.hasNextFireTime());
        }

        public PointInTimeTrigger to() {
            return Optional.ofNullable(this.nextFireTime)
                    .map(t -> new PointInTimeTrigger(t, null, null))
                    .orElse(null);
        }

        public Long getNextFireTime() {
            return nextFireTime;
        }
    }

    public static Long toTime(Date date) {
        return Optional.ofNullable(date).map(Date::getTime).orElse(null);
    }

    public static Date toDate(Long time) {
        return Optional.ofNullable(time).map(Date::new).orElse(null);
    }

    private static class IntervalTriggerAccessor {

        private Long startTime;
        private Long endTime;
        private int repeatLimit;
        private int repeatCount;
        private Long nextFireTime;
        private long period;

        public IntervalTriggerAccessor() {
        }

        public IntervalTriggerAccessor(IntervalTrigger trigger) {
            this.startTime = toTime(trigger.getStartTime());
            this.endTime = toTime(trigger.getEndTime());
            this.repeatLimit = trigger.getRepeatLimit();
            this.repeatCount = trigger.getRepeatCount();
            this.nextFireTime = toTime(trigger.getNextFireTime());
            this.period = trigger.getPeriod();
        }

        public IntervalTrigger to() {
            IntervalTrigger intervalTrigger = new IntervalTrigger();
            intervalTrigger.setStartTime(toDate(startTime));
            intervalTrigger.setEndTime(toDate(endTime));
            intervalTrigger.setRepeatLimit(repeatLimit);
            intervalTrigger.setRepeatCount(repeatCount);
            intervalTrigger.setNextFireTime(toDate(nextFireTime));
            intervalTrigger.setPeriod(period);
            return intervalTrigger;
        }

        public Long getStartTime() {
            return startTime;
        }

        public Long getEndTime() {
            return endTime;
        }

        public int getRepeatLimit() {
            return repeatLimit;
        }

        public int getRepeatCount() {
            return repeatCount;
        }

        public Long getNextFireTime() {
            return nextFireTime;
        }

        public long getPeriod() {
            return period;
        }
    }

    private static class SimpleTimerTriggerAccessor {

        private Long startTime;
        private long period;
        private ChronoUnit periodUnit;
        private int repeatCount;
        private Long endTime;
        private String zoneId;
        private Long nextFireTime;
        private int currentRepeatCount;
        private boolean endTimeReached;

        public SimpleTimerTriggerAccessor() {
        }

        public SimpleTimerTriggerAccessor(SimpleTimerTrigger trigger) {
            this.startTime = toTime(trigger.getStartTime());
            this.period = trigger.getPeriod();
            this.periodUnit = trigger.getPeriodUnit();
            this.repeatCount = trigger.getRepeatCount();
            this.endTime = toTime(trigger.getEndTime());
            this.zoneId = trigger.getZoneId();
            this.nextFireTime = toTime(trigger.getNextFireTime());
            this.currentRepeatCount = trigger.getCurrentRepeatCount();
            this.endTimeReached = trigger.isEndTimeReached();
        }

        public SimpleTimerTrigger to() {
            SimpleTimerTrigger simpleTimerTrigger = new SimpleTimerTrigger();
            simpleTimerTrigger.setStartTime(toDate(startTime));
            simpleTimerTrigger.setPeriod(period);
            simpleTimerTrigger.setPeriodUnit(periodUnit);
            simpleTimerTrigger.setRepeatCount(repeatCount);
            simpleTimerTrigger.setEndTime(toDate(endTime));
            simpleTimerTrigger.setZoneId(zoneId);
            simpleTimerTrigger.setNextFireTime(toDate(nextFireTime));
            simpleTimerTrigger.setCurrentRepeatCount(currentRepeatCount);
            simpleTimerTrigger.setEndTimeReached(endTimeReached);
            return simpleTimerTrigger;
        }

        public Long getStartTime() {
            return startTime;
        }

        public long getPeriod() {
            return period;
        }

        public ChronoUnit getPeriodUnit() {
            return periodUnit;
        }

        public int getRepeatCount() {
            return repeatCount;
        }

        public Long getEndTime() {
            return endTime;
        }

        public String getZoneId() {
            return zoneId;
        }

        public Long getNextFireTime() {
            return nextFireTime;
        }

        public int getCurrentRepeatCount() {
            return currentRepeatCount;
        }

        public boolean isEndTimeReached() {
            return endTimeReached;
        }
    }
}
