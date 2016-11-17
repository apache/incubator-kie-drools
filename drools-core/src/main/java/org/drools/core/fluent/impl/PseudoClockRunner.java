/*
 * Copyright 2011 JBoss Inc
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

package org.drools.core.fluent.impl;

import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.runtime.InternalRunner;
import org.drools.core.command.ConversationContextManager;
import org.drools.core.command.RequestContextImpl;
import org.drools.core.time.SessionPseudoClock;
import org.drools.core.world.impl.ContextManagerImpl;
import org.kie.api.command.Command;
import org.kie.api.runtime.Batch;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.Executable;
import org.kie.api.runtime.KieSession;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PseudoClockRunner implements InternalRunner {

    private final Map<String, Context>    appContexts = new HashMap<String, Context>();
    private ConversationContextManager    cvnManager = new ConversationContextManager();
    private long                          counter;

    private Set<KieSession> ksessions = new HashSet<KieSession>();

    private PriorityQueue<Batch> queue = new PriorityQueue<Batch>( BatchSorter.instance);

    private final long startTime;

    public PseudoClockRunner() {
        this(System.currentTimeMillis());
    }

    public PseudoClockRunner( long startTime ) {
        this.startTime = startTime;
    }

    public Map<String, Context> getAppContexts() {
        return appContexts;
    }

    public Context execute(Executable executable) {
        return execute( executable, createContext() );
    }

    public Context execute( Executable executable, Context ctx ) {
        executeBatches( executable, ctx );
        executeQueue( ctx );
        return ctx;
    }

    public void evaluateExecutable( Executable executable, Context ctx ) {
        if (executable != null) {
            for ( Batch batch : executable.getBatches() ) {
                executeBatch( batch, ctx );
            }
        }
    }

    public void executeBatches( Executable executable, Context ctx ) {
        for (Batch batch : executable.getBatches()) {
            if ( batch.getDistance() == 0L ) {
                executeBatch( batch, ctx );
            } else {
                queue.add( batch );
            }
        }
    }

    public void executeQueue( Context ctx ) {
        while ( !queue.isEmpty() ) {
            Batch batch = queue.remove();
            long timeNow = startTime + batch.getDistance();
            for ( KieSession ksession : ksessions  ) {
                updateKieSessionTime(timeNow, batch.getDistance(), ksession); // make sure all sessions are set to timeNow
            }

            for (Command cmd : batch.getCommands() ) {
                Object returned = ((ExecutableCommand)cmd).execute( ctx );
                if ( returned != null ) {
                    if (ctx instanceof RequestContextImpl ) {
                        ( (RequestContextImpl) ctx ).setLastReturned( returned );
                    }
                    if ( returned instanceof KieSession ) {
                        KieSession ksession = ( KieSession ) returned;
                        updateKieSessionTime(timeNow, batch.getDistance(), ksession); // make sure all sessions are set to timeNow
                        ksessions.add((KieSession)returned);
                    }
                }
            }
        }
    }

    private void executeBatch( Batch batch, Context ctx ) {
        long timeNow = startTime;
        // anything with a temporal distance of 0 is executed now
        // everything else must be handled by a priority queue and timer afterwards.
        for (Command cmd : batch.getCommands() ) {
            Object returned = ((ExecutableCommand)cmd).execute( ctx );
            if ( returned != null ) {
                if (ctx instanceof RequestContextImpl) {
                    ( (RequestContextImpl) ctx ).setLastReturned( returned );
                }
                if ( returned instanceof KieSession ) {
                    KieSession ksession = ( KieSession ) returned;
                    updateKieSessionTime(timeNow, 0, ksession); // make sure all sessions are set to timeNow
                    ksessions.add((KieSession)returned);
                }
            }
        }
    }

    private void updateKieSessionTime(long timeNow, long distance, KieSession ksession) {
        SessionPseudoClock clock = ksession.getSessionClock();

        if ( clock.getCurrentTime() != timeNow ) {
            long               newTime     = startTime + distance;
            long               currentTime = clock.getCurrentTime();
            clock.advanceTime( newTime - currentTime,
                               TimeUnit.MILLISECONDS );
        }
    }

    private static class BatchSorter implements Comparator<Batch> {
        public static BatchSorter instance = new BatchSorter();


        @Override
        public int compare(Batch o1, Batch o2) {
            if(o1.getDistance() > o2.getDistance()) {
                return 1;
            }
            else if(o1.getDistance() < o2.getDistance()) {
                return -1;
            }

            return 0;
        }
    }

    public RequestContextImpl createContext() {
        return new RequestContextImpl( counter++,
                                       new ContextManagerImpl( appContexts ),
                                       cvnManager );
    }
}
