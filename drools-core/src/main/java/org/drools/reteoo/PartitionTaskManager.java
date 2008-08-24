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

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class to control the worker thread for each rulebase partition.
 * It contains an internal Single Thread Pool that ensures thread
 * respawn and a task that ensures no more than a single thread is
 * executing it concurrently.
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 */
public class PartitionTaskManager {

    private ExecutorService pool = null;
    private PartitionTask task = null;

    public PartitionTaskManager( final InternalWorkingMemory workingMemory ) {
        this.task = new PartitionTask( workingMemory );
    }

    /**
     * Starts the service
     */
    public synchronized void startService() {
        if( !isRunning() ) {
            // I'm not sure we should create and destroy the pool every service start/stop,
            // but for now, lets do that. Later we can reevaluate if that is needed.
            this.pool = Executors.newSingleThreadExecutor();
            this.pool.execute( this.task );
        }
    }

    /**
     * Nicely requests the service to stop. This method will not wait
     * for the service to finish.
     */
    public synchronized boolean stopService() {
        boolean result = true;
        if( isRunning() ) {
            this.task.shutdown();
            // I'm not sure we should create and destroy the pool every service start/stop,
            // but for now, lets do that. Later we can reevaluate if that is needed.
            this.pool.shutdown();
            this.pool = null;
        }
        return result;
    }

    /**
     * Nicely requests the service to stop. This method will wait up to
     * the given timeout for the service to finish and will return.
     *
     * @return true in case the services finished, false otherwise
     */
    public synchronized boolean stopService( final long timeout, final TimeUnit unit ) {
        boolean result = true;
        if( isRunning() ) {
            this.task.shutdown();
            // I'm not sure we should create and destroy the pool every service start/stop,
            // but for now, lets do that. Later we can reevaluate if that is needed.
            this.pool.shutdown();
            try {
                result = this.pool.awaitTermination( timeout, unit );
            } catch( InterruptedException e ) {
                result = false;
            }
            this.pool = null;
        }
        return result;
    }

    /**
     * Nicely requests the service to stop. This method will wait until
     * the service finishes or an InterruptedException is generated
     * and will return.
     *
     * @return true in case the services finished, false otherwise
     */
    public synchronized boolean stopServiceAndWait() {
        boolean result = true;
        if( isRunning() ) {
            this.task.shutdown();
            // I'm not sure we should create and destroy the pool every service start/stop,
            // but for now, lets do that. Later we can reevaluate if that is needed.
            this.pool.shutdown();
            try {
                while( !this.pool.awaitTermination( 10, TimeUnit.SECONDS ) ) {
                    ;
                }
                result = this.pool.isTerminated();
            } catch( InterruptedException e ) {
                result = false;
            }
            this.pool = null;
        }
        return result;
    }

    /**
     * Checks if the task is running.
     *
     * @return true if the task is running. false otherwise.
     */
    public synchronized boolean isRunning() {
        return pool != null && !pool.isTerminated();
    }

    /**
     * Adds the given action to the processing queue
     *
     * @param action the action to be processed
     * @return true if the action was successfully added to the processing queue. false otherwise.
     */
    public boolean enqueue( final Action action ) {
        return this.task.enqueue( action );
    }

    /**
     * A worker task that keeps processing the nodes queue.
     * The task uses a blocking queue and keeps processing
     * nodes while there are nodes in the queue and it is not
     * shutdown. If the queue is emptied, the class will wait
     * until a new node is added.
     */
    public static class PartitionTask implements Runnable {

        // the queue with the nodes that need to be processed
        private BlockingQueue<Action> queue;

        // the working memory reference
        private InternalWorkingMemory workingMemory;

        // a flag to nicely shutdown the thread
        private volatile AtomicBoolean shutdown;

        // the actual thread that is running
        private Thread runner;


        /**
         * Constructor
         *
         * @param workingMemory the working memory reference that is used for node processing
         */
        public PartitionTask( final InternalWorkingMemory workingMemory ) {
            this.queue = new LinkedBlockingQueue<Action>();
            this.shutdown = new AtomicBoolean( false );
            this.workingMemory = workingMemory;
            this.runner = null;
        }

        /**
         * Default execution method.
         *
         * @see Runnable
         */
        public void run() {
            // this task can not be shared among multiple threads
            if( checkAndSetRunning() ) {
                return;
            }

            while( !shutdown.get() ) {
                try {
                    // this is a blocking call
                    if( Thread.currentThread().isInterrupted() ) {
                        cancel();
                        break;
                    }
                    Action action = queue.take();
                    action.execute( workingMemory );

                } catch( InterruptedException e ) {
                    cancel();
                }
            }
        }

        /**
         * Requests this task to shutdown
         */
        public void shutdown() {
            synchronized( this ) {
                if( this.runner != null ) {
                    this.runner.interrupt();
                }
            }
            this.cancel();
        }

        /**
         * Returns true if this task is currently executing
         *
         * @return true if the task is currently executing
         */
        public boolean isRunning() {
            synchronized( this ) {
                return !shutdown.get() && this.runner != null;
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
            return this.queue.offer( action );
        }

        /**
         * Cancels current execution and cleans up used resources
         */
        private void cancel() {
            // if the blocking call was interrupted, then check for the cancelation flag
            shutdown.set( true );
            // cleaning up cache reference
            synchronized( this ) {
                this.runner = null;
            }
        }

        /**
         * Checks if the task is already running in a different thread. If it is not
         * running yet, caches current thread reference.
         *
         * @return true if the task is already running in a different thread. false otherwise.
         */
        private boolean checkAndSetRunning() {
            synchronized( this ) {
                if( this.runner == null && !Thread.currentThread().isInterrupted() ) {
                    // if it is not running yet, cache the thread reference
                    this.runner = Thread.currentThread();
                    this.shutdown.set( false );
                } else {
                    // there can be only one thread executing each instance of PartitionTask
                    return true;
                }
            }
            return false;
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
