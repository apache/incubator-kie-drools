/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jobs.service.repository.infinispan.marshaller;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.IntervalTrigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;

public class TriggerMarshaller extends BaseMarshaller<Trigger> {

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
        writer.writeString("classType", trigger.getClass().getName());
        Optional.ofNullable(trigger)
                .filter(IntervalTrigger.class::isInstance)
                .map(IntervalTrigger.class::cast)
                .ifPresentOrElse(intervalTrigger -> {
                    try {
                        writer.writeInstant("startTime",
                                toInstant(intervalTrigger.getStartTime()));
                        writer.writeInstant("endTime", toInstant(intervalTrigger.getEndTime()));
                        writer.writeInt("repeatLimit", intervalTrigger.getRepeatLimit());
                        writer.writeInt("repeatCount", intervalTrigger.getRepeatCount());
                        writer.writeInstant("nextFireTime", toInstant(intervalTrigger.getNextFireTime()));
                        writer.writeLong("period", intervalTrigger.getPeriod());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                        () -> Optional.ofNullable(trigger)
                                .filter(PointInTimeTrigger.class::isInstance)
                                .map(PointInTimeTrigger.class::cast)
                                .ifPresent(c -> {
                                    try {
                                        writer.writeInstant("nextFireTime", toInstant(c.hasNextFireTime()));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }));
    }

    @Override
    public Trigger readFrom(ProtoStreamReader reader) throws IOException {
        String classType = reader.readString("classType");
        Date startTime = fromInstant(reader.readInstant("startTime"));
        Date endTime = fromInstant(reader.readInstant("endTime"));
        Integer repeatLimit = reader.readInt("repeatLimit");
        Integer repeatCount = reader.readInt("repeatCount");
        Date nextFireTime = fromInstant(reader.readInstant("nextFireTime"));
        Long period = reader.readLong("period");
        return Optional.ofNullable(classType)
                .filter(IntervalTrigger.class.getName()::equals)
                .<Trigger> map(c -> {
                    IntervalTrigger intervalTrigger = new IntervalTrigger();
                    intervalTrigger.setStartTime(startTime);
                    intervalTrigger.setEndTime(endTime);
                    intervalTrigger.setRepeatLimit(repeatLimit);
                    intervalTrigger.setRepeatCount(repeatCount);
                    intervalTrigger.setNextFireTime(nextFireTime);
                    intervalTrigger.setPeriod(period);
                    return intervalTrigger;
                })
                .orElseGet(() -> Optional.ofNullable(nextFireTime)
                        .map(Date::getTime)
                        .map(t -> new PointInTimeTrigger(t, null, null))
                        .orElse(null));
    }
}