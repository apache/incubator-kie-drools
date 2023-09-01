package org.drools.serialization.protobuf.timers;

import org.drools.core.common.DefaultEventHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.WorkingMemoryReteExpireAction;
import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.reteoo.ObjectTypeNode.ExpireJob;
import org.drools.core.reteoo.ObjectTypeNode.ExpireJobContext;
import org.drools.core.time.JobContext;
import org.drools.base.time.JobHandle;
import org.drools.core.time.TimerService;
import org.drools.core.time.impl.DefaultJobHandle;
import org.drools.core.time.impl.PointInTimeTrigger;
import org.drools.serialization.protobuf.ProtobufMarshallerReaderContext;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.serialization.protobuf.TimersInputMarshaller;

import java.io.IOException;

public class ExpireJobContextTimerInputMarshaller implements TimersInputMarshaller {
    private static final transient ExpireJob job = new ExpireJob();

    public void read( ProtobufMarshallerReaderContext inCtx) throws IOException,
            ClassNotFoundException {

        InternalFactHandle factHandle = inCtx.getHandles().get( inCtx.readLong() );

        long nextTimeStamp = inCtx.readLong();

        TimerService clock = inCtx.getWorkingMemory().getTimerService();

        JobContext jobctx = new ExpireJobContext(new WorkingMemoryReteExpireAction( (DefaultEventHandle) factHandle ), inCtx.getWorkingMemory() );
        JobHandle handle = clock.scheduleJob( job,
                jobctx,
                PointInTimeTrigger.createPointInTimeTrigger( nextTimeStamp, null ) );
        jobctx.setJobHandle( handle );

    }

    @Override
    public void deserialize( MarshallerReaderContext inCtx, ProtobufMessages.Timers.Timer timer) {
        ProtobufMessages.Timers.ExpireTimer expire = timer.getExpire();
        InternalFactHandle factHandle = inCtx.getHandles().get( expire.getHandleId() );

        TimerService clock = inCtx.getWorkingMemory().getTimerService();

        JobContext jobctx = new ExpireJobContext( new WorkingMemoryReteExpireAction((DefaultEventHandle)factHandle),
                inCtx.getWorkingMemory() );
        DefaultJobHandle jobHandle = (DefaultJobHandle) clock.scheduleJob( job, jobctx,
                PointInTimeTrigger.createPointInTimeTrigger( expire.getNextFireTimestamp(), null ) );
        jobctx.setJobHandle( jobHandle );
        ((DefaultEventHandle) factHandle).addJob(jobHandle);
    }
}