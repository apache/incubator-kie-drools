/*
 * Copyright 2008 JBoss Inc
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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

/**
 * A class to control the tasks for a given rulebase partition.
 * It requires a thread pool that is created in the working 
 * memory and injected in here.
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 */
public class PartitionTaskManager {

    private PartitionTask task = null;
    private AtomicReference<ExecutorService> pool = new AtomicReference<ExecutorService>();

    public PartitionTaskManager( final InternalWorkingMemory workingMemory ) {
        this.task = new PartitionTask( workingMemory );
    }

    /**
     * Sets the thread pool to be used by this partition
     * @param pool
     */
    public void setPool(ExecutorService pool) {
        if( pool != null && this.pool.compareAndSet( null, pool ) ) {
            int size = this.task.queue.size();
            for( int i = 0; i < size; i++ ) {
                this.pool.get().execute( this.task );
            }
        } else {
            this.pool.set( pool );
        }
    }


    /**
     * Adds the given action to the processing queue
     *
     * @param action the action to be processed
     * @return true if the action was successfully added to the processing queue. false otherwise.
     */
    public boolean enqueue( final Action action ) {
        boolean result = this.task.enqueue( action );
        assert result : "result must be true";
        ExecutorService service = this.pool.get(); 
        if(  service != null ) {
            service.execute( this.task );
        } 
        return result;
    }

    /**
     * A worker task that keeps processing the nodes queue.
     * The task uses a non-blocking queue and is re-submitted
     * for execution for each element that is added to the queue.
     */
    public static class PartitionTask implements Runnable {

        // the queue with the nodes that need to be processed
        private Queue<Action> queue;

        // the working memory reference
        private InternalWorkingMemory workingMemory;

        /**
         * Constructor
         *
         * @param workingMemory the working memory reference that is used for node processing
         */
        public PartitionTask( final InternalWorkingMemory workingMemory ) {
            this.queue = new ConcurrentLinkedQueue<Action>();
            this.workingMemory = workingMemory;
        }

        /**
         * Default execution method.
         *
         * @see Runnable
         */
        public void run() {
            try {
                Action action = queue.poll();
                if( action != null ) {
                    action.execute( workingMemory );
                }
            } catch( Exception e ) {
                System.err.println("*******************************************************************************************************");
                System.err.println("Partition task manager caught an unexpected exception: "+e.getMessage());
                System.err.println("Drools is capturing the exception to avoid thread death. Please report stack trace to development team.");
                e.printStackTrace();
            }
        }

        /**
         * Adds the given action to the processing queue returning true if the action
         * was correctly added or false otherwise.
         *
         * @param action the action to add to the processing queue
         * @return true if the node was successfully added to the queue. false otherwise.
         */
        public boolean enqueue( final Action action ) {
            return this.queue.add( action );
        }
    }

    /**
     * An interface for all actions to be executed by the PartitionTask
     */
    public static interface Action extends Externalizable {
        public abstract void execute( final InternalWorkingMemory workingMemory );
    }

    /**
     * An abstract super class for all handle-related actions
     */
    public static abstract class FactAction implements Action, Externalizable {

        protected InternalFactHandle handle;
        protected PropagationContext context;
        protected ObjectSink         sink;

        public FactAction() {
        }

        public FactAction( final InternalFactHandle handle, final PropagationContext context,
                           final ObjectSink sink ) {
            super();
            this.handle = handle;
            this.context = context;
            this.sink = sink;
        }

        public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
            handle = (InternalFactHandle) in.readObject();
            context = (PropagationContext) in.readObject();
            sink = (ObjectSink) in.readObject();
        }

        public void writeExternal( ObjectOutput out ) throws IOException {
            out.writeObject( handle );
            out.writeObject( context );
            out.writeObject( sink );
        }

        public abstract void execute( final InternalWorkingMemory workingMemory );
    }

    public static class FactAssertAction extends FactAction {
        private static final long serialVersionUID = -8478488926430845209L;

        FactAssertAction() {
        }

        public FactAssertAction( final InternalFactHandle handle, final PropagationContext context,
                                 final ObjectSink sink ) {
            super( handle, context, sink );
        }

        public void execute( final InternalWorkingMemory workingMemory ) {
            sink.assertObject( this.handle, this.context, workingMemory );
        }
    }

    /**
     * An abstract super class for all leftTuple-related actions
     */
    public static abstract class LeftTupleAction implements Action, Externalizable {

        protected LeftTuple          leftTuple;
        protected PropagationContext context;
        protected LeftTupleSink      sink;

        public LeftTupleAction() {
        }

        public LeftTupleAction( final LeftTuple leftTuple, final PropagationContext context,
                           final LeftTupleSink sink ) {
            super();
            this.leftTuple = leftTuple;
            this.context = context;
            this.sink = sink;
        }

        public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
            leftTuple = (LeftTuple) in.readObject();
            context = (PropagationContext) in.readObject();
            sink = (LeftTupleSink) in.readObject();
        }

        public void writeExternal( ObjectOutput out ) throws IOException {
            out.writeObject( leftTuple );
            out.writeObject( context );
            out.writeObject( sink );
        }

        public abstract void execute( final InternalWorkingMemory workingMemory );
    }

    public static class LeftTupleAssertAction extends LeftTupleAction {

        public LeftTupleAssertAction() {
        }
        
        public LeftTupleAssertAction( LeftTuple leftTuple, PropagationContext context, LeftTupleSink sink ) {
            super(leftTuple, context, sink );
        }

        public void execute( InternalWorkingMemory workingMemory ) {
            this.sink.assertLeftTuple( leftTuple, context, workingMemory );
        }
    }


    public static class LeftTupleRetractAction extends LeftTupleAction {
        
        public LeftTupleRetractAction() {
        }

        public LeftTupleRetractAction( LeftTuple leftTuple, PropagationContext context, LeftTupleSink sink ) {
            super(leftTuple, context, sink );
        }

        public void execute( InternalWorkingMemory workingMemory ) {
            this.sink.assertLeftTuple( leftTuple, context, workingMemory );
        }
    }
}
