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
package org.kie.kogito.jobs.service.repository.infinispan.marshaller;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.IntervalTrigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;
import org.kie.kogito.timer.impl.SimpleTimerTrigger;

public class TriggerMarshaller extends BaseMarshaller<Trigger> {

    static final String CLASS_TYPE = "classType";
    static final String START_TIME = "startTime";
    static final String END_TIME = "endTime";
    static final String REPEAT_LIMIT = "repeatLimit";
    static final String REPEAT_COUNT = "repeatCount";
    static final String NEXT_FIRE_TIME = "nextFireTime";
    static final String PERIOD = "period";
    static final String PERIOD_UNIT = "periodUnit";
    static final String ZONE_ID = "zoneId";
    static final String CURRENT_REPEAT_COUNT = "currentRepeatCount";
    static final String END_TIME_REACHED = "endTimeReached";

    @Override
    public String getTypeName() {
        return getPackage() + ".Trigger";
    }

    @Override
    public Class<? extends Trigger> getJavaClass() {
        return Trigger.class;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, Trigger trigger) throws IOException {
        if (trigger instanceof SimpleTimerTrigger) {
            writer.writeString(CLASS_TYPE, trigger.getClass().getName());
            SimpleTimerTrigger simpleTimerTrigger = (SimpleTimerTrigger) trigger;
            writer.writeInstant(START_TIME, toInstant(simpleTimerTrigger.getStartTime()));
            writer.writeInstant(END_TIME, toInstant(simpleTimerTrigger.getEndTime()));
            writer.writeInt(REPEAT_COUNT, simpleTimerTrigger.getRepeatCount());
            writer.writeInstant(NEXT_FIRE_TIME, toInstant(simpleTimerTrigger.getNextFireTime()));
            writer.writeLong(PERIOD, simpleTimerTrigger.getPeriod());
            writer.writeString(PERIOD_UNIT, simpleTimerTrigger.getPeriodUnit().name());
            writer.writeString(ZONE_ID, simpleTimerTrigger.getZoneId());
            writer.writeInt(CURRENT_REPEAT_COUNT, simpleTimerTrigger.getCurrentRepeatCount());
            writer.writeBoolean(END_TIME_REACHED, simpleTimerTrigger.isEndTimeReached());
        } else if (trigger instanceof IntervalTrigger) {
            writer.writeString(CLASS_TYPE, trigger.getClass().getName());
            IntervalTrigger intervalTrigger = (IntervalTrigger) trigger;
            writer.writeInstant(START_TIME, toInstant(intervalTrigger.getStartTime()));
            writer.writeInstant(END_TIME, toInstant(intervalTrigger.getEndTime()));
            writer.writeInt(REPEAT_LIMIT, intervalTrigger.getRepeatLimit());
            writer.writeInt(REPEAT_COUNT, intervalTrigger.getRepeatCount());
            writer.writeInstant(NEXT_FIRE_TIME, toInstant(intervalTrigger.getNextFireTime()));
            writer.writeLong(PERIOD, intervalTrigger.getPeriod());
        } else if (trigger instanceof PointInTimeTrigger) {
            writer.writeString(CLASS_TYPE, trigger.getClass().getName());
            PointInTimeTrigger pointInTimeTrigger = (PointInTimeTrigger) trigger;
            writer.writeInstant(NEXT_FIRE_TIME, toInstant(pointInTimeTrigger.hasNextFireTime()));
        } else {
            throw new IOException("Marshalling of trigger class: " + trigger.getClass() + " is not supported.");
        }
    }

    @Override
    public Trigger readFrom(ProtoStreamReader reader) throws IOException {
        String classType = reader.readString(CLASS_TYPE);
        Date startTime = fromInstant(reader.readInstant(START_TIME));
        Date endTime = fromInstant(reader.readInstant(END_TIME));
        Integer repeatLimit = reader.readInt(REPEAT_LIMIT);
        Integer repeatCount = reader.readInt(REPEAT_COUNT);
        Date nextFireTime = fromInstant(reader.readInstant(NEXT_FIRE_TIME));
        Long period = reader.readLong(PERIOD);
        String periodUnit = reader.readString(PERIOD_UNIT);
        String zoneId = reader.readString(ZONE_ID);
        Integer currentRepeatCount = reader.readInt(CURRENT_REPEAT_COUNT);
        Boolean endTimeReached = reader.readBoolean(END_TIME_REACHED);

        if (SimpleTimerTrigger.class.getName().equals(classType)) {
            SimpleTimerTrigger simpleTimerTrigger = new SimpleTimerTrigger();
            simpleTimerTrigger.setStartTime(startTime);
            simpleTimerTrigger.setPeriod(period);
            simpleTimerTrigger.setPeriodUnit(ChronoUnit.valueOf(periodUnit));
            simpleTimerTrigger.setRepeatCount(repeatCount);
            simpleTimerTrigger.setEndTime(endTime);
            simpleTimerTrigger.setZoneId(zoneId);
            simpleTimerTrigger.setNextFireTime(nextFireTime);
            simpleTimerTrigger.setCurrentRepeatCount(currentRepeatCount);
            simpleTimerTrigger.setEndTimeReached(endTimeReached);
            return simpleTimerTrigger;
        } else if (IntervalTrigger.class.getName().equals(classType)) {
            IntervalTrigger intervalTrigger = new IntervalTrigger();
            intervalTrigger.setStartTime(startTime);
            intervalTrigger.setEndTime(endTime);
            intervalTrigger.setRepeatLimit(repeatLimit);
            intervalTrigger.setRepeatCount(repeatCount);
            intervalTrigger.setNextFireTime(nextFireTime);
            intervalTrigger.setPeriod(period);
            return intervalTrigger;
        } else if (PointInTimeTrigger.class.getName().equals(classType)) {
            return nextFireTime != null ? new PointInTimeTrigger(nextFireTime.getTime(), null, null) : null;
        }
        throw new IOException("Unmarshalling of trigger class: " + classType + " is not supported.");
    }
}
