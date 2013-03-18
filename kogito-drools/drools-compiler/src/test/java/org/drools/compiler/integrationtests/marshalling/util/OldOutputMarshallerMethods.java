package org.drools.compiler.integrationtests.marshalling.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.process.instance.WorkItem;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;

public class OldOutputMarshallerMethods {

    // Changed with JBRULES-3257
    public static void writeWorkItem_v1(MarshallerWriteContext context, WorkItem workItem) throws IOException {
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
                int index = context.objectMarshallingStrategyStore.getStrategy( object );
                stream.writeInt( index );
                ObjectMarshallingStrategy strategy = context.objectMarshallingStrategyStore.getStrategy( index );
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
        stream.writeInt( handle.getId() );
        stream.writeLong( handle.getRecency() );

        if ( type == 2) {
            // is event
            EventFactHandle efh = ( EventFactHandle ) handle;
            stream.writeLong( efh.getStartTimestamp() );
            stream.writeLong( efh.getDuration() );
            stream.writeBoolean( efh.isExpired() );
            stream.writeLong( efh.getActivationsCount() );
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

        if ( handle.getEntryPoint() instanceof InternalWorkingMemoryEntryPoint ) {
            String entryPoint = ((InternalWorkingMemoryEntryPoint) handle.getEntryPoint()).getEntryPoint().getEntryPointId();
            if ( entryPoint != null && !entryPoint.equals( "" ) ) {
                stream.writeBoolean( true );
                stream.writeUTF( entryPoint );
            }
            else {
                stream.writeBoolean( false );
            }
        } else {
            stream.writeBoolean( false );
        }
    }
}
