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
import java.sql.Date;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import org.infinispan.protostream.MessageMarshaller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.IntervalTrigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;
import org.kie.kogito.timer.impl.SimpleTimerTrigger;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TriggerMarshallerTest {

    private static final OffsetDateTime START_TIME_VALUE = OffsetDateTime.parse("2023-03-16T12:13:14.001+01:00");
    private static final OffsetDateTime NEXT_FIRE_TIME_VALUE = OffsetDateTime.parse("2023-03-16T14:15:16.001+01:00");
    private static final OffsetDateTime END_TIME_VALUE = OffsetDateTime.parse("2023-03-16T15:16:17.001+01:00");
    private static final long PERIOD_VALUE = 2L;
    private static final int REPEAT_LIMIT_VALUE = 3;
    private static final int REPEAT_COUNT_VALUE = 4;
    private static final int CURRENT_REPEAT_COUNT_VALUE = 1;
    private static final boolean END_TIME_REACHED_VALUE = false;
    private static final String ZONE_ID_VALUE = "+01:00";
    private static final ChronoUnit PERIOD_UNIT_VALUE = ChronoUnit.HOURS;

    @Mock
    private MessageMarshaller.ProtoStreamWriter writer;

    @Mock
    private MessageMarshaller.ProtoStreamReader reader;

    private final TriggerMarshaller marshaller = new TriggerMarshaller();

    @Test
    void getTypeName() {
        assertThat(marshaller.getTypeName()).isEqualTo("job.service.Trigger");
    }

    @Test
    void getJavaClass() {
        assertThat(marshaller.getJavaClass()).isEqualTo(Trigger.class);
    }

    @Test
    void writeToSimpleTimerTrigger() throws IOException {
        SimpleTimerTrigger trigger = new SimpleTimerTrigger();
        trigger.setStartTime(Date.from(START_TIME_VALUE.toInstant()));
        trigger.setPeriod(PERIOD_VALUE);
        trigger.setPeriodUnit(PERIOD_UNIT_VALUE);
        trigger.setRepeatCount(REPEAT_COUNT_VALUE);
        trigger.setEndTime(Date.from(END_TIME_VALUE.toInstant()));
        trigger.setZoneId(ZONE_ID_VALUE);
        trigger.setNextFireTime(Date.from(NEXT_FIRE_TIME_VALUE.toInstant()));
        trigger.setCurrentRepeatCount(CURRENT_REPEAT_COUNT_VALUE);
        trigger.setEndTimeReached(END_TIME_REACHED_VALUE);

        marshaller.writeTo(writer, trigger);

        verify(writer).writeString(TriggerMarshaller.CLASS_TYPE, SimpleTimerTrigger.class.getName());
        verify(writer).writeInstant(TriggerMarshaller.START_TIME, START_TIME_VALUE.toInstant());
        verify(writer).writeInstant(TriggerMarshaller.END_TIME, END_TIME_VALUE.toInstant());
        verify(writer).writeInt(TriggerMarshaller.REPEAT_COUNT, REPEAT_COUNT_VALUE);
        verify(writer).writeInstant(TriggerMarshaller.NEXT_FIRE_TIME, NEXT_FIRE_TIME_VALUE.toInstant());
        verify(writer).writeLong(TriggerMarshaller.PERIOD, PERIOD_VALUE);
        verify(writer).writeString(TriggerMarshaller.PERIOD_UNIT, PERIOD_UNIT_VALUE.name());
        verify(writer).writeString(TriggerMarshaller.ZONE_ID, ZONE_ID_VALUE);
        verify(writer).writeInt(TriggerMarshaller.CURRENT_REPEAT_COUNT, CURRENT_REPEAT_COUNT_VALUE);
        verify(writer).writeBoolean(TriggerMarshaller.END_TIME_REACHED, END_TIME_REACHED_VALUE);
    }

    @Test
    void writeToIntervalTrigger() throws IOException {
        IntervalTrigger trigger = new IntervalTrigger();
        trigger.setStartTime(Date.from(START_TIME_VALUE.toInstant()));
        trigger.setEndTime(Date.from(END_TIME_VALUE.toInstant()));
        trigger.setRepeatLimit(REPEAT_LIMIT_VALUE);
        trigger.setRepeatCount(REPEAT_COUNT_VALUE);
        trigger.setNextFireTime(Date.from(NEXT_FIRE_TIME_VALUE.toInstant()));
        trigger.setPeriod(PERIOD_VALUE);

        marshaller.writeTo(writer, trigger);

        verify(writer).writeString(TriggerMarshaller.CLASS_TYPE, IntervalTrigger.class.getName());
        verify(writer).writeInstant(TriggerMarshaller.START_TIME, START_TIME_VALUE.toInstant());
        verify(writer).writeInstant(TriggerMarshaller.END_TIME, END_TIME_VALUE.toInstant());
        verify(writer).writeInt(TriggerMarshaller.REPEAT_LIMIT, REPEAT_LIMIT_VALUE);
        verify(writer).writeInt(TriggerMarshaller.REPEAT_COUNT, REPEAT_COUNT_VALUE);
        verify(writer).writeInstant(TriggerMarshaller.NEXT_FIRE_TIME, NEXT_FIRE_TIME_VALUE.toInstant());
        verify(writer).writeLong(TriggerMarshaller.PERIOD, PERIOD_VALUE);
    }

    @Test
    void writeToPointInTimeTrigger() throws IOException {
        PointInTimeTrigger trigger = new PointInTimeTrigger(NEXT_FIRE_TIME_VALUE.toInstant().toEpochMilli(), null, null);
        marshaller.writeTo(writer, trigger);
        verify(writer).writeString(TriggerMarshaller.CLASS_TYPE, PointInTimeTrigger.class.getName());
        verify(writer).writeInstant(TriggerMarshaller.NEXT_FIRE_TIME, NEXT_FIRE_TIME_VALUE.toInstant());
    }

    @Test
    void readFromSimpleTimerTrigger() throws IOException {
        doReturn(SimpleTimerTrigger.class.getName()).when(reader).readString(TriggerMarshaller.CLASS_TYPE);
        Trigger result = readTrigger();
        assertThat(result).isExactlyInstanceOf(SimpleTimerTrigger.class);
        SimpleTimerTrigger trigger = (SimpleTimerTrigger) result;
        assertThat(trigger.getStartTime()).isEqualTo(START_TIME_VALUE.toInstant());
        assertThat(trigger.getPeriod()).isEqualTo(PERIOD_VALUE);
        assertThat(trigger.getPeriodUnit()).isEqualTo(PERIOD_UNIT_VALUE);
        assertThat(trigger.getRepeatCount()).isEqualTo(REPEAT_COUNT_VALUE);
        assertThat(trigger.getEndTime()).isEqualTo(END_TIME_VALUE.toInstant());
        assertThat(trigger.getNextFireTime()).isEqualTo(NEXT_FIRE_TIME_VALUE.toInstant());
        assertThat(trigger.getZoneId()).isEqualTo(ZONE_ID_VALUE);
        assertThat(trigger.getCurrentRepeatCount()).isEqualTo(CURRENT_REPEAT_COUNT_VALUE);
        assertThat(trigger.isEndTimeReached()).isEqualTo(END_TIME_REACHED_VALUE);
    }

    @Test
    void readFromIntervalTrigger() throws IOException {
        doReturn(IntervalTrigger.class.getName()).when(reader).readString(TriggerMarshaller.CLASS_TYPE);
        Trigger result = readTrigger();
        assertThat(result).isExactlyInstanceOf(IntervalTrigger.class);
        IntervalTrigger trigger = (IntervalTrigger) result;
        assertThat(trigger.getStartTime()).isEqualTo(START_TIME_VALUE.toInstant());
        assertThat(trigger.getPeriod()).isEqualTo(PERIOD_VALUE);
        assertThat(trigger.getRepeatLimit()).isEqualTo(REPEAT_LIMIT_VALUE);
        assertThat(trigger.getRepeatCount()).isEqualTo(REPEAT_COUNT_VALUE);
        assertThat(trigger.getEndTime()).isEqualTo(END_TIME_VALUE.toInstant());
        assertThat(trigger.getNextFireTime()).isEqualTo(NEXT_FIRE_TIME_VALUE.toInstant());
    }

    @Test
    void readFromPointInTime() throws IOException {
        doReturn(PointInTimeTrigger.class.getName()).when(reader).readString(TriggerMarshaller.CLASS_TYPE);
        Trigger result = readTrigger();
        assertThat(result).isExactlyInstanceOf(PointInTimeTrigger.class);
        PointInTimeTrigger trigger = (PointInTimeTrigger) result;
        assertThat(trigger.hasNextFireTime()).isEqualTo(NEXT_FIRE_TIME_VALUE.toInstant());
    }

    private Trigger readTrigger() throws IOException {
        doReturn(START_TIME_VALUE.toInstant()).when(reader).readInstant(TriggerMarshaller.START_TIME);
        doReturn(END_TIME_VALUE.toInstant()).when(reader).readInstant(TriggerMarshaller.END_TIME);
        doReturn(REPEAT_LIMIT_VALUE).when(reader).readInt(TriggerMarshaller.REPEAT_LIMIT);
        doReturn(REPEAT_COUNT_VALUE).when(reader).readInt(TriggerMarshaller.REPEAT_COUNT);
        doReturn(NEXT_FIRE_TIME_VALUE.toInstant()).when(reader).readInstant(TriggerMarshaller.NEXT_FIRE_TIME);
        doReturn(PERIOD_VALUE).when(reader).readLong(TriggerMarshaller.PERIOD);
        doReturn(PERIOD_UNIT_VALUE.name()).when(reader).readString(TriggerMarshaller.PERIOD_UNIT);
        doReturn(ZONE_ID_VALUE).when(reader).readString(TriggerMarshaller.ZONE_ID);
        doReturn(CURRENT_REPEAT_COUNT_VALUE).when(reader).readInt(TriggerMarshaller.CURRENT_REPEAT_COUNT);
        doReturn(END_TIME_REACHED_VALUE).when(reader).readBoolean(TriggerMarshaller.END_TIME_REACHED);
        return marshaller.readFrom(reader);
    }
}
