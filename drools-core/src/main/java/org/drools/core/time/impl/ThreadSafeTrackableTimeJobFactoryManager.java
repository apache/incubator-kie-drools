package org.drools.core.time.impl;

import org.drools.core.time.JobContext;
import org.drools.core.time.SelfRemovalJobContext;

import java.util.concurrent.ConcurrentHashMap;

public class ThreadSafeTrackableTimeJobFactoryManager extends TrackableTimeJobFactoryManager {
    public ThreadSafeTrackableTimeJobFactoryManager() {
        super(new ConcurrentHashMap<>());
    }

    @Override
    protected SelfRemovalJobContext createJobContext( JobContext ctx ) {
        return new SelfRemovalJobContext( ctx, timerInstances );
    }
}
