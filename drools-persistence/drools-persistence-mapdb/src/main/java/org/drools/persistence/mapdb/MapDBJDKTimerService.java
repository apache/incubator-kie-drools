package org.drools.persistence.mapdb;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.time.InternalSchedulerService;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.SelfRemovalJob;
import org.drools.core.time.SelfRemovalJobContext;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.DefaultTimerJobInstance;
import org.drools.core.time.impl.JDKTimerService;
import org.drools.core.time.impl.TimerJobInstance;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutableRunner;

public class MapDBJDKTimerService extends JDKTimerService {

    private ExecutableRunner              runner;

    private Map<Long, TimerJobInstance> timerInstances;

    public void setCommandService(ExecutableRunner runner) {
        this.runner = runner;
    }

    public MapDBJDKTimerService() {
        this( 1 );
        timerInstances = new ConcurrentHashMap<Long, TimerJobInstance>();
    }

    public MapDBJDKTimerService(int size) {
        super( size );
        timerInstances = new ConcurrentHashMap<Long, TimerJobInstance>();
    }

    protected Callable<Void> createCallableJob(Job job,
                                               JobContext ctx,
                                               Trigger trigger,
                                               JDKJobHandle handle,
                                               InternalSchedulerService scheduler) {
        MapDBJDKCallableJob jobInstance = new MapDBJDKCallableJob( new SelfRemovalJob( job ),
                                                               new SelfRemovalJobContext( ctx,
                                                                                          timerInstances ),
                                                               trigger,
                                                               handle,
                                                               scheduler );

        this.timerInstances.put( handle.getId(),
                                 jobInstance );
        return jobInstance;
    }

    public Collection<TimerJobInstance> getTimerJobInstances() {
        return timerInstances.values();
    }

    public class MapDBJDKCallableJob extends DefaultTimerJobInstance {

		private static final long serialVersionUID = 1L;

		public MapDBJDKCallableJob(Job job,
                                 JobContext ctx,
                                 Trigger trigger,
                                 JDKJobHandle handle,
                                 InternalSchedulerService scheduler) {
            super( job,
                   ctx,
                   trigger,
                   handle,
                   scheduler );
        }

        public Void call() throws Exception {
            try { 
                MapDBCallableJobCommand command = new MapDBCallableJobCommand( this );
                runner.execute( command );
            } catch(Exception e ) { 
                logger.error("Unable to execute job!", e);
                throw e;
            }
            
            return null;
        }

        private Void internalCall() throws Exception {
            return super.call();
        }
    }

    public static class MapDBCallableJobCommand
        implements
        ExecutableCommand<Void> {

        private static final long serialVersionUID = 4L;

        private MapDBJDKCallableJob job;

        public MapDBCallableJobCommand(MapDBJDKCallableJob job) {
            this.job = job;
        }

        public Void execute(Context context) {
            try {
                return job.internalCall();
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
