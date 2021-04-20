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
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.IntervalTrigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;

import io.vertx.core.json.JsonObject;

@ApplicationScoped
public class TriggerMarshaller {

    private static final String CLASS_TYPE = "classType";

    public JsonObject marshall(Trigger trigger) {
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

    public Trigger unmarshall(JsonObject jsonObject) {
        String classType = Optional.ofNullable(jsonObject).map(o -> (String) o.remove(CLASS_TYPE)).orElse(null);
        if (IntervalTrigger.class.getName().equals(classType)) {
            return jsonObject.mapTo(IntervalTriggerAccessor.class).to();
        }
        if (PointInTimeTrigger.class.getName().equals(classType)) {
            return jsonObject.mapTo(PointInTimeTriggerAccessor.class).to();
        }
        return null;
    }

    private static class PointInTimeTriggerAccessor {

        private Date nextFireTime;

        public PointInTimeTriggerAccessor() {
        }

        public PointInTimeTriggerAccessor(PointInTimeTrigger trigger) {
            this.nextFireTime = trigger.hasNextFireTime();
        }

        public PointInTimeTrigger to() {
            return Optional.ofNullable(this.nextFireTime)
                    .map(Date::getTime)
                    .map(t -> new PointInTimeTrigger(t, null, null))
                    .orElse(null);
        }

        public Date getNextFireTime() {
            return nextFireTime;
        }

        public void setNextFireTime(Date nextFireTime) {
            this.nextFireTime = nextFireTime;
        }
    }

    private static class IntervalTriggerAccessor {

        private Date startTime;
        private Date endTime;
        private int repeatLimit;
        private int repeatCount;
        private Date nextFireTime;
        private long period;

        public IntervalTriggerAccessor() {
        }

        public IntervalTriggerAccessor(IntervalTrigger trigger) {
            this.startTime = trigger.getStartTime();
            this.endTime = trigger.getEndTime();
            this.repeatLimit = trigger.getRepeatLimit();
            this.repeatCount = trigger.getRepeatCount();
            this.nextFireTime = trigger.getNextFireTime();
            this.period = trigger.getPeriod();
        }

        public IntervalTrigger to() {
            IntervalTrigger intervalTrigger = new IntervalTrigger();
            intervalTrigger.setStartTime(startTime);
            intervalTrigger.setEndTime(endTime);
            intervalTrigger.setRepeatLimit(repeatLimit);
            intervalTrigger.setRepeatCount(repeatCount);
            intervalTrigger.setNextFireTime(nextFireTime);
            intervalTrigger.setPeriod(period);
            return intervalTrigger;
        }

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }

        public int getRepeatLimit() {
            return repeatLimit;
        }

        public void setRepeatLimit(int repeatLimit) {
            this.repeatLimit = repeatLimit;
        }

        public int getRepeatCount() {
            return repeatCount;
        }

        public void setRepeatCount(int repeatCount) {
            this.repeatCount = repeatCount;
        }

        public Date getNextFireTime() {
            return nextFireTime;
        }

        public void setNextFireTime(Date nextFireTime) {
            this.nextFireTime = nextFireTime;
        }

        public long getPeriod() {
            return period;
        }

        public void setPeriod(long period) {
            this.period = period;
        }
    }
}
