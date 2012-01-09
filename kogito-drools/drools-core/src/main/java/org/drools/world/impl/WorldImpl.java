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

package org.drools.world.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.drools.command.Command;
import org.drools.command.Context;
import org.drools.command.World;
import org.drools.command.GetDefaultValue;
import org.drools.command.NewStatefulKnowledgeSessionCommand;
import org.drools.command.ResolvingKnowledgeCommandContext;
import org.drools.command.impl.ContextImpl;
import org.drools.command.impl.GenericCommand;
import org.drools.runtime.CommandExecutor;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.simulation.SimulationPath;
import org.drools.simulation.Simulation;
import org.drools.simulation.SimulationStep;
import org.drools.time.SessionPseudoClock;

public class WorldImpl
        implements World, GetDefaultValue, CommandExecutor {


    private Context                       root;
    private Map<String, Context>          contexts;

    private static String                 ROOT             = "ROOT";

    private CommandExecutionHandler       executionHandler = new DefaultCommandExecutionHandler();

    private Object                        lastReturnValue;

    public WorldImpl() {
        this.root = new ContextImpl( ROOT,
                                     this );
        
        this.root.set( "world", 
                       this );

        this.contexts = new HashMap<String, Context>();
        this.contexts.put( ROOT,
                           this.root );
    }
    
    

    public <T> T execute(Command<T> command) {
        return null;
    }
//    public void run() {
//        SimulationStep step;
//        while ( (step = executeNextStep()) != null ) {
//
//        }
//    }

//    public SimulationStep executeNextStep() {
//        if ( this.queue.isEmpty() ) {
//            return null;
//        }
//        SimulationStepImpl step = (SimulationStepImpl) this.queue.remove();
//        SimulationPathImpl path = (SimulationPathImpl) step.getPath();
//
//        Context pathContext = new ResolvingKnowledgeCommandContext( this.contexts.get( path.getName() ) );
//
//        // increment the clock for all the registered ksessions
//        for ( StatefulKnowledgeSession ksession : this.ksessions ) {
//            SessionPseudoClock clock = (SessionPseudoClock) ksession.getSessionClock();
//            long newTime = startTime + step.getTemporalDistance();
//            long currentTime = clock.getCurrentTime();
//
//            clock.advanceTime( newTime - currentTime,
//                               TimeUnit.MILLISECONDS );
//        }
//
//        for ( Command cmd : step.getCommands() ) {
//            if ( cmd instanceof NewStatefulKnowledgeSessionCommand ) {
//                // instantiate the ksession, set it's clock and register it
//                StatefulKnowledgeSession ksession = (StatefulKnowledgeSession) executionHandler.execute( (GenericCommand) cmd,
//                                                                                                         pathContext );
//                if ( ksession != null ) {
//                    SessionPseudoClock clock = (SessionPseudoClock) ksession.getSessionClock();
//                    long newTime = startTime + step.getTemporalDistance();
//                    long currentTime = clock.getCurrentTime();
//                    clock.advanceTime( newTime - currentTime,
//                                       TimeUnit.MILLISECONDS );
//                    this.ksessions.add( ksession );
//                    this.lastReturnValue = ksession;
//                }
//            } else if ( cmd instanceof GenericCommand ) {
//                this.lastReturnValue = executionHandler.execute( (GenericCommand) cmd,
//                                                                 pathContext );
//            }
//        }
//
//        return step;
//    }

    public void setCommandExecutionHandler(CommandExecutionHandler executionHandler) {
        this.executionHandler = executionHandler;
    }
    
    public Context createContext(String identifier) {
        Context ctx = this.contexts.get(  identifier );
        if ( ctx == null ) {
            ctx = new ContextImpl( identifier, this, root );
            this.contexts.put(  identifier, ctx );
        }
        
        return ctx;
    }

    public Context getContext(String identifier) {
        return this.contexts.get( identifier );
    }

    public Context getRootContext() {
        return this.root;
    }
    
    public Object getLastReturnValue() {
        return this.lastReturnValue;
    }

    public static interface CommandExecutionHandler {
        public Object execute(GenericCommand command,
                              Context context);
    }

    public static class DefaultCommandExecutionHandler
        implements
        CommandExecutionHandler {
        public Object execute(GenericCommand command,
                              Context context) {
            return command.execute( context );
        }
    }

    public Object getObject() {
        return lastReturnValue;
    }

	public World getContextManager() {
		return this;
	}

	public String getName() {
		return root.getName();
	}

	public Object get(String identifier) {
		return root.get( identifier );
	}

	public void set(String identifier, Object value) {
		root.set( identifier, value );
	}

	public void remove(String identifier) {
		root.remove( identifier );
	}

    //    public static interface CommandExecutorService<T> {
    //        T execute(Command command);
    //    }
    //    
    //    public static class SimulatorCommandExecutorService<T> implements CommandExecutorService {
    //        Map map = new HashMap() {
    //            {
    //               put( KnowledgeBuilderAddCommand.class, null);
    //            }
    //        };
    //        
    //        public  T execute(Command command) {
    //            return null;
    //        }
    //    }
    //    
    //    public static interface CommandContextAdapter {
    //        Context getContext();
    //    }
    //    
    //    public static class KnowledgeBuilderCommandContextAdapter implements CommandContextAdapter {
    //
    //        public Context getContext() {
    //            return new KnowledgeBuilderCommandContext();
    //        }
    //        
    //    }

    //    public void runUntil(SimulationStep step) {
    //        
    //    }
    //    
    //    public void runForTemporalDistance(long distance) {
    //        
    //    }
}
