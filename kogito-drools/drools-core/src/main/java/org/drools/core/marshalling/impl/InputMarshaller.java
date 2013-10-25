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
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.drools.core.RuntimeDroolsException;
import org.drools.core.SessionConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DroolsQuery;
import org.drools.core.common.AbstractWorkingMemory;
import org.drools.core.common.AgendaGroupQueueImpl;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.BaseNode;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleBase;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.QueryElementFactHandle;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.common.ScheduledAgendaItem;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.common.WorkingMemoryFactory;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.util.ObjectHashMap;
import org.drools.core.util.ObjectHashSet;
import org.drools.core.util.StringUtils;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.drools.core.reteoo.AccumulateNode.AccumulateContext;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.InitialFactImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.QueryElementNode.UnificationNodeViewChangedEventListener;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleSink;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.WindowNode;
import org.drools.core.reteoo.WindowNode.WindowMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.Package;
import org.drools.core.rule.Rule;
import org.drools.core.rule.SlidingLengthWindow;
import org.drools.core.rule.SlidingLengthWindow.SlidingLengthWindowContext;
import org.drools.core.rule.SlidingTimeWindow;
import org.drools.core.rule.SlidingTimeWindow.SlidingTimeWindowContext;
import org.drools.core.spi.Activation;
import org.drools.core.spi.AgendaGroup;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.PropagationContext;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.CronTrigger;
import org.drools.core.time.impl.IntervalTrigger;
import org.drools.core.time.impl.PointInTimeTrigger;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.rule.EntryPoint;

public class InputMarshaller {

    public static InternalFactHandle readFactHandle( MarshallerReaderContext context ) throws IOException,
            ClassNotFoundException {
        int type = context.stream.readInt();
        int id = context.stream.readInt();
        long recency = context.stream.readLong();

        long startTimeStamp = 0;
        long duration = 0;
        boolean expired = false;
        long activationsCount = 0;
        if (type == 2) {
            startTimeStamp = context.stream.readLong();
            duration = context.stream.readLong();
            expired = context.stream.readBoolean();
            activationsCount = context.stream.readLong();
        }

        int strategyIndex = context.stream.readInt();
        Object object = null;
        ObjectMarshallingStrategy strategy = null;
        // This is the old way of de/serializing strategy objects
        if (strategyIndex >= 0) {
            strategy = context.resolverStrategyFactory.getStrategy( strategyIndex );
        }
        // This is the new way
        else if (strategyIndex == -2) {
            String strategyClassName = context.stream.readUTF();
            if (!StringUtils.isEmpty( strategyClassName )) {
                strategy = context.resolverStrategyFactory.getStrategyObject( strategyClassName );
                if (strategy == null) {
                    throw new IllegalStateException( "No strategy of type " + strategyClassName + " available." );
                }
            }
        }

        // If either way retrieves a strategy, use it
        if (strategy != null) {
            object = strategy.read( context.stream );
        }

        EntryPoint entryPoint = null;
        if (context.readBoolean()) {
            String entryPointId = context.readUTF();
            if (entryPointId != null && !entryPointId.equals( "" )) {
                entryPoint = context.wm.getEntryPoints().get( entryPointId );
            }
        }

        EntryPointId confEP;
        if ( entryPoint != null ) {
            confEP = ((NamedEntryPoint) entryPoint).getEntryPoint();
        } else {
            confEP = context.wm.getEntryPoint();
        }
        ObjectTypeConf typeConf = context.wm.getObjectTypeConfigurationRegistry().getObjectTypeConf( confEP, object );


        InternalFactHandle handle = null;
        switch (type) {
            case 0: {

                handle = new DefaultFactHandle( id,
                                                object,
                                                recency,
                                                entryPoint,
                                                typeConf != null && typeConf.isTrait() );
                break;

            }
            case 1: {
                handle = new QueryElementFactHandle( object,
                                                     id,
                                                     recency );
                break;
            }
            case 2: {
                handle = new EventFactHandle( id, object, recency, startTimeStamp, duration, entryPoint, typeConf != null && typeConf.isTrait() );
                ( (EventFactHandle) handle ).setExpired( expired );
                ( (EventFactHandle) handle ).setActivationsCount( activationsCount );
                break;
            }
            default: {
                throw new IllegalStateException( "Unable to marshal FactHandle, as type does not exist:" + type );
            }
        }

        return handle;
    }

    public static WorkItem readWorkItem( MarshallerReaderContext context ) throws IOException {
        ObjectInputStream stream = context.stream;

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setId( stream.readLong() );
        workItem.setProcessInstanceId( stream.readLong() );
        workItem.setName( stream.readUTF() );
        workItem.setState( stream.readInt() );

        //WorkItem Paramaters
        int nbVariables = stream.readInt();
        if (nbVariables > 0) {

            for (int i = 0; i < nbVariables; i++) {
                String name = stream.readUTF();
                try {
                    int index = stream.readInt();
                    ObjectMarshallingStrategy strategy = null;
                    // Old way of retrieving strategy objects
                    if (index >= 0) {
                        strategy = context.resolverStrategyFactory.getStrategy( index );
                        if (strategy == null) {
                            throw new IllegalStateException( "No strategy of with index " + index + " available." );
                        }
                    }
                    // New way 
                    else if (index == -2) {
                        String strategyClassName = stream.readUTF();
                        // fix for backwards compatibility (5.x -> 6.x)
                        if ("org.drools.marshalling.impl.SerializablePlaceholderResolverStrategy".equals(strategyClassName)) {
                        	strategyClassName = "org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy";
                        }
                        strategy = context.resolverStrategyFactory.getStrategyObject( strategyClassName );
                        if (strategy == null) {
                            throw new IllegalStateException( "No strategy of type " + strategyClassName + " available." );
                        }
                    }

                    Object value = strategy.read( stream );
                    workItem.setParameter( name,
                                           value );
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException(
                                                        "Could not reload variable " + name );
                }
            }
        }

        return workItem;
    }

    public static Trigger readTrigger( MarshallerReaderContext inCtx ) throws IOException, ClassNotFoundException {
        short triggerInt = inCtx.readShort();

        switch (triggerInt) {
            case PersisterEnums.CRON_TRIGGER: {
                long startTime = inCtx.readLong();

                CronTrigger trigger = new CronTrigger();
                trigger.setStartTime( new Date( startTime ) );
                if (inCtx.readBoolean()) {
                    long endTime = inCtx.readLong();
                    trigger.setEndTime( new Date( endTime ) );
                }

                int repeatLimit = inCtx.readInt();
                trigger.setRepeatLimit( repeatLimit );

                int repeatCount = inCtx.readInt();
                trigger.setRepeatCount( repeatCount );

                String expr = inCtx.readUTF();
                trigger.setCronExpression( expr );
                if (inCtx.readBoolean()) {
                    long nextFireTime = inCtx.readLong();
                    trigger.setNextFireTime( new Date( nextFireTime ) );
                }

                String[] calendarNames = (String[]) inCtx.readObject();
                trigger.setCalendarNames( calendarNames );
                return trigger;
            }
            case PersisterEnums.INT_TRIGGER: {
                IntervalTrigger trigger = new IntervalTrigger();
                long startTime = inCtx.readLong();
                trigger.setStartTime( new Date( startTime ) );
                if (inCtx.readBoolean()) {
                    long endTime = inCtx.readLong();
                    trigger.setEndTime( new Date( endTime ) );
                }
                int repeatLimit = inCtx.readInt();
                trigger.setRepeatLimit( repeatLimit );
                int repeatCount = inCtx.readInt();
                trigger.setRepeatCount( repeatCount );
                if (inCtx.readBoolean()) {
                    long nextFireTime = inCtx.readLong();
                    trigger.setNextFireTime( new Date( nextFireTime ) );
                }
                long period = inCtx.readLong();
                trigger.setPeriod( period );
                String[] calendarNames = (String[]) inCtx.readObject();
                trigger.setCalendarNames( calendarNames );
                return trigger;
            }
            case PersisterEnums.POINT_IN_TIME_TRIGGER: {
                long startTime = inCtx.readLong();

                PointInTimeTrigger trigger = new PointInTimeTrigger( startTime, null, null );
                return trigger;
            }
        }
        throw new RuntimeException( "Unable to persist Trigger for type: " + triggerInt );

    }
}
