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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DroolsQuery;
import org.drools.core.common.AbstractWorkingMemory;
import org.drools.core.common.ActivationIterator;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleBase;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.util.ObjectHashMap;
import org.drools.core.util.ObjectHashSet;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.reteoo.AccumulateNode.AccumulateContext;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.WindowNode;
import org.drools.core.reteoo.WindowNode.WindowMemory;
import org.drools.core.rule.Behavior;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.Rule;
import org.drools.core.rule.SlidingLengthWindow;
import org.drools.core.rule.SlidingLengthWindow.SlidingLengthWindowContext;
import org.drools.core.rule.SlidingTimeWindow;
import org.drools.core.rule.SlidingTimeWindow.SlidingTimeWindowContext;
import org.drools.core.spi.Activation;
import org.drools.core.spi.ActivationGroup;
import org.drools.core.spi.AgendaGroup;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.RuleFlowGroup;
import org.drools.core.time.JobContext;
import org.drools.core.time.SelfRemovalJobContext;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.CronTrigger;
import org.drools.core.time.impl.IntervalTrigger;
import org.drools.core.time.impl.PointInTimeTrigger;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.core.time.impl.TimerJobInstance;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;
import org.kie.api.runtime.rule.EntryPoint;

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

}
