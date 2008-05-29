/*
 * Copyright 2007 JBoss Inc
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
 *
 * Created on Oct 17, 2007
 */
package org.drools.time.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.drools.TemporalSession;
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.WorkingMemoryAction;
import org.drools.marshalling.MarshallerWriteContext;
import org.drools.reteoo.ReteooTemporalSession;
import org.drools.rule.Behavior;
import org.drools.temporal.SessionClock;

/**
 * A SessionPseudoClock is a clock that allows the user to explicitly 
 * control current time.
 * 
 * @author etirelli
 *
 */
public class SessionPseudoClock
    implements
    SessionClock {

    private long                                          timer;
    private PriorityQueue<ScheduledItem>                  queue;
    private transient Map<Behavior, ScheduledItem>        schedules;
    private transient ReteooTemporalSession<SessionClock> session;

    public SessionPseudoClock() {
        this( null );
    }

    public SessionPseudoClock(TemporalSession<SessionClock> session) {
        this.timer = 0;
        this.queue = new PriorityQueue<ScheduledItem>();
        this.schedules = new HashMap<Behavior, ScheduledItem>();
        this.session = (ReteooTemporalSession<SessionClock>) session;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        timer = in.readLong();
        PriorityQueue<ScheduledItem> tmp = (PriorityQueue<ScheduledItem>) in.readObject();
        if( tmp != null ) {
            queue = tmp;
            for ( ScheduledItem item : queue ) {
                this.schedules.put( item.getBehavior(),
                                    item );
            }
        }
        session = (ReteooTemporalSession<SessionClock>) ((DroolsObjectInputStream) in).getWorkingMemory();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong( timer );
        // this is a work around to a bug in the object stream code, where it raises exceptions
        // when trying to deserialize an empty priority queue.
        out.writeObject( queue.isEmpty() ? null : queue );
    }

    /* (non-Javadoc)
    * @see org.drools.temporal.SessionClock#getCurrentTime()
    */
    public synchronized long getCurrentTime() {
        return this.timer;
    }

    public synchronized long advanceTime(long millisecs) {
        this.timer += millisecs;
        this.runCallBacks();
        return this.timer;
    }

    public synchronized void setStartupTime(int i) {
        this.timer = i;
    }

    public synchronized void schedule(final Behavior behavior,
                                      final Object behaviorContext,
                                      final long timestamp) {
        ScheduledItem item = schedules.remove( behavior );
        if ( item != null ) {
            queue.remove( item );
        }
        item = new ScheduledItem( timestamp,
                                  behavior,
                                  behaviorContext );
        schedules.put( behavior,
                       item );
        queue.add( item );
    }

    public synchronized void unschedule(final Behavior behavior) {
        ScheduledItem item = schedules.remove( behavior );
        if ( item != null ) {
            queue.remove( item );
        }
    }

    /**
     * @return the session
     */
    public synchronized TemporalSession<SessionClock> getSession() {
        return session;
    }

    /**
     * @param session the session to set
     */
    public synchronized void setSession(TemporalSession<? extends SessionClock> session) {
        this.session = (ReteooTemporalSession<SessionClock>) session;
    }

    private void runCallBacks() {
        ScheduledItem item = queue.peek();
        while ( item != null && item.getTimestamp() <= this.timer ) {
            // remove the head
            queue.remove();
            // enqueue the callback
            session.queueWorkingMemoryAction( item );
            // get next head
            item = queue.peek();
        }
    }

    private static final class ScheduledItem
        implements
        Comparable<ScheduledItem>,
        WorkingMemoryAction {
        private long     timestamp;
        private Behavior behavior;
        private Object   behaviorContext;

        /**
         * @param timestamp
         * @param behavior
         * @param behaviorContext 
         */
        public ScheduledItem(final long timestamp,
                             final Behavior behavior, 
                             final Object behaviorContext) {
            super();
            this.timestamp = timestamp;
            this.behavior = behavior;
            this.behaviorContext = behaviorContext;
        }

        /**
         * @return the timestamp
         */
        public final long getTimestamp() {
            return timestamp;
        }

        /**
         * @return the behavior
         */
        public final Behavior getBehavior() {
            return behavior;
        }

        public int compareTo(ScheduledItem o) {
            return this.timestamp < o.getTimestamp() ? -1 : this.timestamp == o.getTimestamp() ? 0 : 1;
        }

        public void execute(final InternalWorkingMemory workingMemory) {
            behavior.expireTuples( behaviorContext, workingMemory );
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            timestamp = in.readLong();
            behavior = (Behavior) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeLong( timestamp );
            out.writeObject( behavior );
        }
        
        public String toString() {
            return "ScheduledItem( timestamp="+timestamp+", behavior="+behavior+" )";
        }

        public void write(MarshallerWriteContext context) throws IOException {
            // TODO Auto-generated method stub
            
        }
    }

}
