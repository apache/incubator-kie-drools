/**
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
package org.drools.serialization.protobuf;

import org.drools.core.common.DefaultEventHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.marshalling.MarshallerWriteContext;
import org.drools.core.process.WorkItem;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class OldOutputMarshallerMethods {

    // Changed with JBRULES-3257
    public static void writeWorkItem_v1(MarshallerWriteContext context, WorkItem workItem) throws IOException {
        ObjectOutputStream stream = (ObjectOutputStream) context;
        stream.writeLong( workItem.getId() );
        stream.writeUTF( workItem.getProcessInstanceId() );
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
                int index = context.getObjectMarshallingStrategyStore().getStrategy( object );
                stream.writeInt( index );
                ObjectMarshallingStrategy strategy = context.getObjectMarshallingStrategyStore().getStrategy( index );
                if ( strategy.accept( object ) ) {
                    strategy.write( stream,
                            object );
                }
            }

        }

    } 

    // Changed with JBRULES-3257
    public static void writeFactHandle_v1(MarshallerWriteContext context,
            ObjectOutputStream stream,
            ObjectMarshallingStrategyStore objectMarshallingStrategyStore,
            int type,
            InternalFactHandle handle) throws IOException {
        stream.writeInt( type );
        stream.writeLong( handle.getId() );
        stream.writeLong( handle.getRecency() );

        if ( type == 2) {
            // is event
            DefaultEventHandle efh = (DefaultEventHandle) handle;
            stream.writeLong( efh.getStartTimestamp() );
            stream.writeLong( efh.getDuration() );
            stream.writeBoolean( efh.isExpired() );
        }

        //context.out.println( "Object : int:" + handle.getId() + " long:" + handle.getRecency() );
        //context.out.println( handle.getObject() );

        Object object = handle.getObject();

        if ( object != null ) {
            int index = objectMarshallingStrategyStore.getStrategy( object );

            ObjectMarshallingStrategy strategy = objectMarshallingStrategyStore.getStrategy( index );

            stream.writeInt( index );

            strategy.write( stream,
                    object );
        } else {
            stream.writeInt( -1 );
        }

        String entryPoint = handle.getEntryPointId() != null ? handle.getEntryPointId().getEntryPointId() : null;
        if ( entryPoint != null && !entryPoint.equals( "" ) ) {
            stream.writeBoolean( true );
            stream.writeUTF( entryPoint );
        }
        else {
            stream.writeBoolean( false );
        }
    }
}
