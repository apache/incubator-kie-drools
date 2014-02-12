/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.marshalling.impl;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.drools.core.process.instance.WorkItem;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.CronTrigger;
import org.drools.core.time.impl.IntervalTrigger;
import org.drools.core.time.impl.PointInTimeTrigger;
import org.kie.api.marshalling.ObjectMarshallingStrategy;

public class OutputMarshaller {


    public static void writeWorkItem(MarshallerWriteContext context,
                                     WorkItem workItem) throws IOException {
        ObjectOutputStream stream = context.stream;
        stream.writeLong( workItem.getId() );
        stream.writeLong( workItem.getProcessInstanceId() );
        stream.writeUTF( workItem.getName() );
        stream.writeInt( workItem.getState() );

        //Work Item Parameters
        Map<String, Object> parameters = workItem.getParameters();
        Collection<Object> notNullValues = new ArrayList<Object>();
        for ( Object value : parameters.values() ) {
            if ( value != null ) {
                notNullValues.add( value );
            }
        }

        stream.writeInt( notNullValues.size() );
        for ( String key : parameters.keySet() ) {
            Object object = parameters.get( key );
            if ( object != null ) {
                stream.writeUTF( key );
                
                ObjectMarshallingStrategy strategy = context.objectMarshallingStrategyStore.getStrategyObject( object );
                String strategyClassName = strategy.getClass().getName();
                stream.writeInt(-2); // backwards compatibility
                stream.writeUTF(strategyClassName);
                if ( strategy.accept( object ) ) {
                    strategy.write( stream,
                                    object );
                }
            }

        }

    }

    public static void writeTrigger(Trigger trigger, MarshallerWriteContext outCtx) throws IOException {
        if ( trigger instanceof CronTrigger ) {
            outCtx.writeShort( PersisterEnums.CRON_TRIGGER );

            CronTrigger cronTrigger = ( CronTrigger ) trigger;
            outCtx.writeLong( cronTrigger.getStartTime().getTime() );
            if ( cronTrigger.getEndTime() != null ) {
                outCtx.writeBoolean( true );
                outCtx.writeLong( cronTrigger.getEndTime().getTime() );
            } else {
                outCtx.writeBoolean( false );
            }
            outCtx.writeInt( cronTrigger.getRepeatLimit() );
            outCtx.writeInt( cronTrigger.getRepeatCount() );
            outCtx.writeUTF( cronTrigger.getCronEx().getCronExpression() );
            if ( cronTrigger.getNextFireTime() != null ) {
                outCtx.writeBoolean( true );
                outCtx.writeLong( cronTrigger.getNextFireTime().getTime() );
            } else {
                outCtx.writeBoolean( false );
            }
            outCtx.writeObject( cronTrigger.getCalendarNames() );
        } else if ( trigger instanceof IntervalTrigger ) {
            outCtx.writeShort( PersisterEnums.INT_TRIGGER );

            IntervalTrigger intTrigger = ( IntervalTrigger ) trigger;
            outCtx.writeLong( intTrigger.getStartTime().getTime() );
            if ( intTrigger.getEndTime() != null ) {
                outCtx.writeBoolean( true );
                outCtx.writeLong( intTrigger.getEndTime().getTime() );
            } else {
                outCtx.writeBoolean( false );
            }
            outCtx.writeInt( intTrigger.getRepeatLimit() );
            outCtx.writeInt( intTrigger.getRepeatCount() );
            if ( intTrigger.getNextFireTime() != null ) {
                outCtx.writeBoolean( true );
                outCtx.writeLong( intTrigger.getNextFireTime().getTime() );
            } else {
                outCtx.writeBoolean( false );
            }
            outCtx.writeLong( intTrigger.getPeriod() );
            outCtx.writeObject( intTrigger.getCalendarNames() );
        } else if ( trigger instanceof PointInTimeTrigger ) {
            outCtx.writeShort( PersisterEnums.POINT_IN_TIME_TRIGGER );

            PointInTimeTrigger pinTrigger = ( PointInTimeTrigger ) trigger;

            outCtx.writeLong( pinTrigger.hasNextFireTime().getTime() );
        }
    }
}
