package org.drools.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class wraps up an externally managed executor service, 
 * meaning that the life cycle of the service is not managed
 * by Drools. So, we intercept calls to shutdown() and shutdownNow()
 * to not shutdown the external pool. Also, we need to maintain a
 * list of tasks submitted to the external pool, so that they can
 * be properly cancelled on a shutdown.
 *  
 * @author etirelli
 */
public class ExternalExecutorService 
    implements
    java.util.concurrent.ExecutorService {

    // this is an atomic reference to avoid additional locking
    private AtomicReference<ExecutorService> delegate;

    // the instance responsible for tracking tasks that still need to be executed
    private TaskManager                      taskManager;

    // guarded by lock
    private boolean                          shutdown;
    private ReentrantLock                    lock;
    private Condition                        isShutdown;

    public ExternalExecutorService(java.util.concurrent.ExecutorService delegate) {
        this.delegate = new AtomicReference<ExecutorService>( delegate );
        this.shutdown = false;
        this.lock = new ReentrantLock();
        this.isShutdown = this.lock.newCondition();
        this.taskManager = new TaskManager();
    }

    public void waitUntilEmpty() {
        this.taskManager.waitUntilEmpty();
    }

    /**
     * Always returns true, if a shutdown was requested,
     * since the life cycle of this executor is externally 
     * maintained. 
     */
    public boolean awaitTermination(long timeout,
                                    TimeUnit unit) throws InterruptedException {
        try {
            lock.lockInterruptibly();
            if ( !this.shutdown ) {
                isShutdown.await();
            }
            return shutdown;
        } finally {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void execute(Runnable command) {
        ExecutorService service = delegate.get();
        if ( service != null ) {
            service.execute( taskManager.trackTask( command ) );
            return;
        }
        throw new RejectedExecutionException( "Execution service is terminated. No more tasks can be executed." );
    }
 
    /**
     * {@inheritDoc}
     */
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                         long timeout,
                                         TimeUnit unit) throws InterruptedException {
        ExecutorService service = delegate.get();
        if ( service != null ) {
            return service.invokeAll( taskManager.trackTasks( tasks ),
                                      timeout,
                                      unit );
        }
        throw new RejectedExecutionException( "Execution service is terminated. No more tasks can be executed." );
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        ExecutorService service = delegate.get();
        if ( service != null ) {
            return service.invokeAll( taskManager.trackTasks( tasks ) );
        }
        throw new RejectedExecutionException( "Execution service is terminated. No more tasks can be executed." );
    }

    /**
     * {@inheritDoc}
     */
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                           long timeout,
                           TimeUnit unit) throws InterruptedException,
                                         ExecutionException,
                                         TimeoutException {
        ExecutorService service = delegate.get();
        if ( service != null ) {
            // since the tasks are either executed or cancelled, there is no need to track them
            return service.invokeAny( tasks,
                                      timeout,
                                      unit );
        }
        throw new RejectedExecutionException( "Execution service is terminated. No more tasks can be executed." );
    }

    /**
     * {@inheritDoc}
     */
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException,
                                                         ExecutionException {
        ExecutorService service = delegate.get();
        if ( service != null ) {
            // since the tasks are either executed or cancelled, there is no need to track them
            return service.invokeAny( tasks );
        }
        throw new RejectedExecutionException( "Execution service is terminated. No more tasks can be executed." );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isShutdown() {
        lock.lock();
        try {
            return shutdown;
        } finally {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTerminated() {
        lock.lock();
        try {
            // for an externally managed service, shutdown and terminated have the same semantics
            return shutdown;
        } finally {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        lock.lock();
        try {
            shutdown = true;
            delegate.set( null );
            taskManager.cleanUpTasks();
            isShutdown.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Runnable> shutdownNow() {
        shutdown();
        // not possible to return a proper list of not executed tasks
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public <T> Future<T> submit(Callable<T> task) {
        ExecutorService service = delegate.get();
        if ( service != null ) {
            return service.submit( taskManager.trackTask( task ) );
        }
        throw new RejectedExecutionException( "Execution service is terminated. No more tasks can be executed." );
    }

    /**
     * {@inheritDoc}
     */
    public <T> Future<T> submit(Runnable task,
                                T result) {
        ExecutorService service = delegate.get();
        if ( service != null ) {
            return service.submit( taskManager.trackTask( task ),
                                   result );
        }
        throw new RejectedExecutionException( "Execution service is terminated. No more tasks can be executed." );
    }

    /**
     * {@inheritDoc}
     */
    public Future< ? > submit(Runnable task) {
        ExecutorService service = delegate.get();
        if ( service != null ) {
            return service.submit( taskManager.trackTask( task ) );
        }
        throw new RejectedExecutionException( "Execution service is terminated. No more tasks can be executed." );
    }

    /**
     * Interface that defines the methods to be implemented
     * by a task observer. These methods are called whenever
     * the observable task starts executing, finishes executing,
     * or raises a Throwable exception.
     * 
     * @author etirelli
     */
    protected static interface TaskObserver {

        public void beforeTaskStarts(Runnable task,
                                     Thread thread);

        public void beforeTaskStarts(Callable< ? > task,
                                     Thread thread);

        public void afterTaskFinishes(Runnable task,
                                      Thread thread);

        public void afterTaskFinishes(Callable< ? > task,
                                      Thread thread);

        public void taskExceptionRaised(Runnable task,
                                        Thread thread,
                                        Throwable t);
    }

    /**
     * An implementation of the TaskObserver interface that
     * keeps a map of submitted, but not executed tasks. 
     * Whenever one of the ObservableTasks is executed, it
     * is removed from the map.
     * 
     * @author etirelli
     */
    protected static class TaskManager
        implements
        TaskObserver {

        // maps Task->ObservableTask
        private final Map<Object, ObservableTask> tasks;
        
        private Lock lock = new ReentrantLock();
        private Condition empty = lock.newCondition();

        public TaskManager() {
            this.tasks = new ConcurrentHashMap<Object, ObservableTask>();
        }

        public void waitUntilEmpty() {
            //System.out.println("Will wait for empty...");
            lock.lock();
            try {
                if ( !tasks.isEmpty() ) {
                    //System.out.println("Not empty yet...");
                    try {
                        // wait until it is empty
                        empty.await();
                        //System.out.println("it is now");
                    } catch ( InterruptedException e ) {
                        //System.out.println("interruped...");
                        Thread.currentThread().interrupt();
                    }
                } else {
                    //System.out.println("Already empty...");
                }
            } finally {
                lock.unlock();
            }
        }

        public void cleanUpTasks() {
            for( ObservableTask task : tasks.values() ) {
                task.cancel();
            }
            tasks.clear();
        }

        /**
         * Creates an ObservableRunnable instance for the given task and
         * tracks the task execution
         * 
         * @param task the task to track
         * 
         * @return the observable instance of the given task
         */
        public Runnable trackTask(Runnable task) {
            //System.out.println("Tracking task = "+System.identityHashCode( task )+" : "+task);
            ObservableRunnable obs = new ObservableRunnable( task,
                                                             this );
            tasks.put( task,
                       obs );
            return obs;
        }

        /**
         * Creates an ObservableCallable<T> instance for the given task and
         * tracks the task execution
         * 
         * @param task the task to track
         * 
         * @return the observable instance of the given task
         */
        public <T> Callable<T> trackTask(Callable<T> task) {
            ObservableCallable<T> obs = new ObservableCallable<T>( task,
                                                                   this );
            tasks.put( task,
                       obs );
            return obs;
        }

        /**
         * Creates an ObservableCallable<T> instance for each of the given taks
         * and track their execution
         * 
         * @param tasksToTrack the collection of tasks to track
         * 
         * @return the collection of ObservableCallable<T> tasks
         */
        public <T> Collection<Callable<T>> trackTasks(Collection<? extends Callable<T>> tasksToTrack) {
            Collection<Callable<T>> results = new ArrayList<Callable<T>>( tasksToTrack.size() );
            for ( Callable<T> task : tasksToTrack ) {
                results.add( trackTask( task ) );
            }
            return results;
        }

        public void afterTaskFinishes(Runnable task,
                                      Thread thread) {
            lock.lock();
            try {
                //System.out.println("Task finished = "+System.identityHashCode( task )+" : "+task);
                this.tasks.remove( task );
                if( this.tasks.isEmpty() ) {
                    empty.signalAll();
                }
            } finally {
                lock.unlock();
            }
        }

        public void afterTaskFinishes(Callable< ? > task,
                                      Thread thread) {
            lock.lock();
            try {
                this.tasks.remove( task );
                if( this.tasks.isEmpty() ) {
                    empty.signalAll();
                }
            } finally {
                lock.unlock();
            }
        }

        public void beforeTaskStarts(Runnable task,
                                     Thread thread) {
            // nothing to do for now
        }

        public void beforeTaskStarts(Callable< ? > task,
                                     Thread thread) {
            // nothing to do for now
        }

        public void taskExceptionRaised(Runnable task,
                                        Thread thread,
                                        Throwable t) {
            // nothing to do for now
        }
    }

    /**
     * A super interface for ObservableTasks
     * 
     * @author etirelli
     */
    protected static interface ObservableTask {
        public static enum TaskType {
            CALLABLE, RUNNABLE
        }

        /**
         * Returns the type of this ObservableTask: either RUNNABLE or CALLABLE
         * 
         * @return
         */
        public TaskType getType();

        /**
         * Prevents the execution of the ObservableTask if it did not
         * started executing yet.
         */
        public void cancel();
    }

    /**
     * This class is a wrapper around a Runnable task
     * that will notify a listener when the task starts executing
     * and when it finishes executing.
     * 
     * @author etirelli
     */
    protected static final class ObservableRunnable
        implements
        Runnable,
        ObservableTask {
        private final Runnable     delegate;
        private final TaskObserver handler;
        private volatile boolean   cancel;

        public ObservableRunnable(Runnable delegate,
                                  TaskObserver handler) {
            this.delegate = delegate;
            this.handler = handler;
            this.cancel = false;
        }

        public void run() {
            if ( !cancel ) {
                try {
                    handler.beforeTaskStarts( delegate,
                                              Thread.currentThread() );
                    delegate.run();
                } catch ( Throwable t ) {
                    handler.taskExceptionRaised( delegate,
                                                 Thread.currentThread(),
                                                 t );
                } finally {
                    handler.afterTaskFinishes( delegate,
                                               Thread.currentThread() );
                }
            }
        }

        public TaskType getType() {
            return TaskType.RUNNABLE;
        }

        public void cancel() {
            this.cancel = true;
        }
    }

    /**
     * This class is a wrapper around a Callable<V> task
     * that will notify a listener when the task starts executing
     * and when it finishes executing.
     * 
     * @author etirelli
     */
    protected static final class ObservableCallable<V>
        implements
        Callable<V>,
        ObservableTask {
        private final Callable<V>  delegate;
        private final TaskObserver handler;
        private volatile boolean   cancel;

        public ObservableCallable(Callable<V> delegate,
                                  TaskObserver handler) {
            this.delegate = delegate;
            this.handler = handler;
        }

        public V call() throws Exception {
            if( ! cancel ) {
                try {
                    handler.beforeTaskStarts( delegate,
                                              Thread.currentThread() );
                    V result = delegate.call();
                    return result;
                } finally {
                    handler.afterTaskFinishes( delegate,
                                               Thread.currentThread() );
                }
            }
            return null;
        }

        public TaskType getType() {
            return TaskType.CALLABLE;
        }

        public void cancel() {
            this.cancel = true;
        }
    }
}
