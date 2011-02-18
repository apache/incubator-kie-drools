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

package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

/**
 * A class to control the tasks for a given rulebase partition.
 * It requires a thread pool that is created in the working 
 * memory and injected in here.
 */
public class PartitionTaskManager {

    // we use a fly weight implementation of the partition tasks to ensure no more
    // than one task is executed concurrently for each partition
    private PartitionTask task = null;

    public PartitionTaskManager(final PartitionManager manager,
                                final InternalWorkingMemory workingMemory) {
        this.task = new PartitionTask( manager,
                                       workingMemory );
    }

    /**
     * Adds the given action to the processing queue
     *
     * @param action the action to be processed
     * @return true if the action was successfully added to the processing queue. false otherwise.
     */
    public boolean enqueue(final Action action) {
        return this.task.enqueue( action );
    }

    /**
     * A worker task that keeps processing the nodes queue.
     * The task uses a non-blocking queue and is re-submitted
     * for execution for each element that is added to the queue.
     */
    public static class PartitionTask
        implements
        Runnable,
        Comparable<PartitionTask> {

        // the priority of this task
        private int                   priority;

        // the executor service (thread pool) reference
        private PartitionManager      manager;

        // the shared priority queue with the nodes that need to be processed
        private BlockingQueue<Action> queue;

        // the working memory reference
        private InternalWorkingMemory workingMemory;

        // true if this task is already enqueued
        private AtomicBoolean         enqueued;

        // true if YieldAction already added to queue
        private AtomicBoolean         isYieldAdded;

        /**
         * Constructor
         *
         * @param workingMemory the working memory reference that is used for node processing
         */
        public PartitionTask(final PartitionManager manager,
                             final InternalWorkingMemory workingMemory) {
            this.queue = new PriorityBlockingQueue<Action>();
            this.manager = manager;
            this.workingMemory = workingMemory;
            this.priority = Action.PRIORITY_NORMAL;
            this.enqueued = new AtomicBoolean( false );
            this.isYieldAdded = new AtomicBoolean( false );
        }

        public boolean enqueue(Action action) {
            boolean result = queue.add( action );
            addToExecutorQueue();
            return result;
        }

        /**
         * Default execution method.
         *
         * @see Runnable
         */
        public void run() {
            try {
                Action action = queue.poll();
                if ( action != null ) {
                    action.execute( workingMemory );
                }
                enqueued.set( false );
                addToExecutorQueue();
            } catch ( Exception e ) {
                System.err.println( "*******************************************************************************************************" );
                System.err.println( "Partition task manager caught an unexpected exception: " + e.getMessage() );
                System.err.println( "Drools is capturing the exception to avoid thread death. Please report stack trace to development team." );
                e.printStackTrace();
            }
        }

        public void addToExecutorQueue() {
            synchronized ( isYieldAdded ) {
                if ( this.manager.isOnHold() && (!queue.isEmpty()) && isYieldAdded.compareAndSet( false,
                                                                                                  true ) ) {
                    //System.out.println( "Adding yield " + System.identityHashCode( this ) );
                    queue.add( YieldAction.INSTANCE );
                }
            }
            if ( !queue.isEmpty() && enqueued.compareAndSet( false,
                                                             true ) ) {
                Action head = queue.peek();
                int priority = Action.PRIORITY_HIGH;
                while ( head != null && head instanceof YieldAction ) {
                    isYieldAdded.compareAndSet( true,
                                                false );
                    //System.out.println( "Yield consumed " + System.identityHashCode( this ) );
                    priority = Action.PRIORITY_NORMAL;
                    queue.remove();
                    head = queue.peek();
                }
                if ( head != null ) {
                    this.setPriority( priority );
                    manager.execute( this );
                } else {
                    enqueued.compareAndSet( true,
                                            false );
                }
            }
        }

        public int getPriority() {
            return priority;
        }

        public boolean isEnqueued() {
            return enqueued.get();
        }

        private void setPriority(int priority) {
            this.priority = priority;
        }

        public int compareTo(PartitionTask o) {
            return this.getPriority() - o.getPriority();
        }

        @Override
        public String toString() {
            return "PartitionTask( priority=" + priority + " action=" + queue.peek() + " )";
        }
    }

    /**
     * An interface for all actions to be executed by the PartitionTask
     */
    public static interface Action
        extends
        Externalizable,
        Comparable<Action> {
        public static final int PRIORITY_HIGH   = 10;
        public static final int PRIORITY_NORMAL = 0;
        public static final int PRIORITY_LOW    = -10;

        public int getPriority();

        public void execute(final InternalWorkingMemory workingMemory);
    }

    /**
     * An abstract super class for all handle-related actions
     */
    public static abstract class FactAction
        implements
        Action,
        Externalizable {

        protected InternalFactHandle handle;
        protected PropagationContext context;
        protected ObjectSink         sink;
        protected int                priority;

        public FactAction() {
            priority = PRIORITY_NORMAL;
        }

        public FactAction(final InternalFactHandle handle,
                          final PropagationContext context,
                          final ObjectSink sink,
                          final int priority) {
            super();
            this.handle = handle;
            this.context = context;
            this.sink = sink;
            this.priority = priority;
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            handle = (InternalFactHandle) in.readObject();
            context = (PropagationContext) in.readObject();
            sink = (ObjectSink) in.readObject();
            priority = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( handle );
            out.writeObject( context );
            out.writeObject( sink );
            out.writeInt( priority );
        }

        public int getPriority() {
            return priority;
        }

        public int compareTo(Action o) {
            return this.getPriority() - o.getPriority();
        }

        public abstract void execute(final InternalWorkingMemory workingMemory);

        @Override
        public String toString() {
            return getClass().getSimpleName() + "( part=" + sink.getPartitionId() + " sink=" + sink + " )";
        }
    }

    public static class FactAssertAction extends FactAction {
        private static final long serialVersionUID = 510l;

        FactAssertAction() {
        }

        public FactAssertAction(final InternalFactHandle handle,
                                final PropagationContext context,
                                final ObjectSink sink,
                                final int priority) {
            super( handle,
                   context,
                   sink,
                   priority );
        }

        public void execute(final InternalWorkingMemory workingMemory) {
            sink.assertObject( this.handle,
                               this.context,
                               workingMemory );
        }
    }

    /**
     * An abstract super class for all leftTuple-related actions
     */
    public static abstract class LeftTupleAction
        implements
        Action,
        Externalizable {

        protected LeftTuple          leftTuple;
        protected PropagationContext context;
        protected LeftTupleSink      sink;
        protected int                priority;

        public LeftTupleAction() {
            priority = PRIORITY_NORMAL;
        }

        public LeftTupleAction(final LeftTuple leftTuple,
                               final PropagationContext context,
                               final LeftTupleSink sink,
                               final int priority) {
            super();
            this.leftTuple = leftTuple;
            this.context = context;
            this.sink = sink;
            this.priority = priority;
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            leftTuple = (LeftTuple) in.readObject();
            context = (PropagationContext) in.readObject();
            sink = (LeftTupleSink) in.readObject();
            priority = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( leftTuple );
            out.writeObject( context );
            out.writeObject( sink );
            out.writeInt( priority );
        }

        public int getPriority() {
            return priority;
        }

        public int compareTo(Action o) {
            return this.getPriority() - o.getPriority();
        }

        public abstract void execute(final InternalWorkingMemory workingMemory);
    }

    public static class LeftTupleAssertAction extends LeftTupleAction {

        public LeftTupleAssertAction() {
        }

        public LeftTupleAssertAction(final LeftTuple leftTuple,
                                     final PropagationContext context,
                                     final LeftTupleSink sink,
                                     final int priority) {
            super( leftTuple,
                   context,
                   sink,
                   priority );
        }

        public void execute(InternalWorkingMemory workingMemory) {
            this.sink.assertLeftTuple( leftTuple,
                                       context,
                                       workingMemory );
        }
    }

    public static class LeftTupleRetractAction extends LeftTupleAction {

        public LeftTupleRetractAction() {
        }

        public LeftTupleRetractAction(final LeftTuple leftTuple,
                                      final PropagationContext context,
                                      final LeftTupleSink sink,
                                      final int priority) {
            super( leftTuple,
                   context,
                   sink,
                   priority );
        }

        public void execute(InternalWorkingMemory workingMemory) {
            this.sink.assertLeftTuple( leftTuple,
                                       context,
                                       workingMemory );
        }
    }

    /**
     * A markup action used to mark spots in the queue where
     * the next action must be executed at normal priority
     */
    private static class YieldAction
        implements
        Action {
        public static final YieldAction INSTANCE = new YieldAction();

        private YieldAction() {
        }

        public void execute(InternalWorkingMemory workingMemory) {
        }

        public int getPriority() {
            return PRIORITY_NORMAL;
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public int compareTo(Action o) {
            return this.getPriority() - o.getPriority();
        }
    }
}
