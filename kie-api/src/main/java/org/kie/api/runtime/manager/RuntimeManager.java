/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.api.runtime.manager;

/**
 * RuntimeManager manages <code>RuntimeEngine</code>s that are essentially build with
 * <code>KieSession</code> and <code>TaskService</code> to deliver executable environments for
 * processes and user tasks.<br>
 * Moreover <code>RuntimeManager</code> ensures that all components are configured and bootstrapped 
 * as soon as manager is instantiated to ensure its fully featured functionality right from the start. 
 * That includes:
 * <ul>
 * 	<li>timer service</li>
 * 	<li>task service</li>
 * 	<li>finds and initializes timer start events based processes</li>
 * </ul>
 * RuntimeManager shall always be closed whenever it's not needed any more to free up resources it allocated.<br>
 * <code>RuntimeManager</code>s are identified by unique identifiers and thus there cannot be two RuntimeManagers
 * with the same id active at the same time in the same system. <br>
 * RuntimeManager implements runtime strategy that provides certain management capabilities to reduce manual 
 * work needed to control ksession behavior. Which mainly covers when to create, dispose and when to use which ksession.
 * Currently there are three predefined strategies:
 * <ul>
 * 	<li>Singleton - there is only one, always active ksession for the manager, 
 * 					access to it is thread safe that is achieved by synchronization which applies to both
 * 					ksession and task service</li>
 * 	<li>PerRequest - new ksession and task service instances will be returned for every invocation of the 
 * 					getRuntimeEngine(Context) method. Important to know is same instance of RuntimeEngine will
 * 					be returned through out transaction to avoid issues with persistence context.</li>
 * 	<li>PerProcessInstance - most advanced strategy that keeps track of which ksession was used to work with
 * 					given process instance. It lives as long as process instance is alive and is destroyed
 * 					when process instance is completed/aborted.</li>
 * </ul>
 */
public interface RuntimeManager {

	/**
	 * Returns <code>RuntimeEngine</code> instance that is fully initialized:
	 * <ul>
	 * 	<li>KiseSession is created or loaded depending on the strategy</li>
	 * 	<li>TaskService is initialized and attached to ksession (via listener)</li>
	 * 	<li>WorkItemHandlers are initialized and registered on ksession</li>
	 * 	<li>EventListeners (process, agenda, working memory) are initialized and added to ksession</li>
	 * </ul>
	 * @param context the concrete implementation of the context that is supported by given <code>RuntimeManager</code>
	 * @return instance of the <code>RuntimeEngine</code>
	 */
    RuntimeEngine getRuntimeEngine(Context<?> context);
    
    /**
     * @return unique identifier of this <code>RuntimeManager</code>
     */
    String getIdentifier();
   
    /**
     * Disposes <code>RuntimeEngine</code> and notifies all listeners about that fact.
     * This method should always be used to dispose <code>RuntimeEngine</code> that is not needed
     * anymore. <br>
     * ksession.dispose() shall never be used with RuntimeManager as it will break the internal
     * mechanisms of the manager responsible for clear and efficient disposal.<br>
     * Dispose is not needed if <code>RuntimeEngine</code> was obtained within active JTA transaction, 
     * this means that when getRuntimeEngine method was invoked during active JTA transaction then dispose of
     * the runtime engine will happen automatically on transaction completion.
     * @param runtime
     */
    void disposeRuntimeEngine(RuntimeEngine runtime);
    
    /**
     * Closes <code>RuntimeManager</code> and releases it's resources. Shall always be called when
     * runtime manager is not needed any more. Otherwise it will still be active and operational.
     */
    void close();
    
    /**
     * Allows to signal event on runtime manager level which in turn allows to broadcast given event to all listening 
     * components managed by this RuntimeManager
     * @param type type of the signal
     * @param event actual event data
     */
    void signalEvent(String type, Object event);
    
}
