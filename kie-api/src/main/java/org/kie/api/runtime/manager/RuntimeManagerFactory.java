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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory that produces instances of <code>RuntimeManager</code>. It allows to produce
 * runtime managers based on predefined strategies:
 * <ul>
 * 	<li>Singleton</li>
 * 	<li>PerRequest</li>
 * 	<li>PerProcessInstance</li> 
 * </ul>
 * By default uses <code>org.jbpm.runtime.manager.impl.RuntimeManagerFactoryImpl</code> as implementation 
 * of the factory but can be overridden using system property <code>org.jbpm.runtime.manager.class</code>
 * that should provide fully qualified class name of the class that implements this factory.
 */
public interface RuntimeManagerFactory {
   
	/**
	 * Produces new instance of singleton <code>RuntimeManager</code> with default identifier. 
	 * Since it relies on default identifier it can only be invoked once unless previously produced
	 * manager is closed. Otherwise error will be thrown indicating that managers must be identifier uniquely.
	 * @param environment environment instance for the new runtime manager
	 * @return new instance of <code>RuntimeManager</code>
	 */
    RuntimeManager newSingletonRuntimeManager(RuntimeEnvironment environment);
    
    /**
     * Produces new instance of singleton <code>RuntimeManager</code> with custom identifier. 
	 * In case the given identifier is already in use error will be thrown indicating that managers must be identifier uniquely.
	 * @param environment environment instance for the new runtime manager
     * @param identifier custom identifier for the manager
     * @return new instance of <code>RuntimeManager</code>
     */
    RuntimeManager newSingletonRuntimeManager(RuntimeEnvironment environment, String identifier);
    
    /**
	 * Produces new instance of per request <code>RuntimeManager</code> with default identifier. 
	 * Since it relies on default identifier it can only be invoked once unless previously produced
	 * manager is closed. Otherwise error will be thrown indicating that managers must be identifier uniquely.
	 * @param environment environment instance for the new runtime manager
	 * @return new instance of <code>RuntimeManager</code>
	 */
    RuntimeManager newPerRequestRuntimeManager(RuntimeEnvironment environment);
    
    /**
     * Produces new instance of per request <code>RuntimeManager</code> with custom identifier. 
	 * In case the given identifier is already in use error will be thrown indicating that managers must be identifier uniquely.
	 * @param environment environment instance for the new runtime manager
     * @param identifier custom identifier for the manager
     * @return new instance of <code>RuntimeManager</code>
     */
    RuntimeManager newPerRequestRuntimeManager(RuntimeEnvironment environment, String identifier);
    
    /**
	 * Produces new instance of per process instance <code>RuntimeManager</code> with default identifier. 
	 * Since it relies on default identifier it can only be invoked once unless previously produced
	 * manager is closed. Otherwise error will be thrown indicating that managers must be identifier uniquely.
	 * @param environment environment instance for the new runtime manager
	 * @return new instance of <code>RuntimeManager</code>
	 */
    RuntimeManager newPerProcessInstanceRuntimeManager(RuntimeEnvironment environment);
    
    /**
     * Produces new instance of per process instance <code>RuntimeManager</code> with custom identifier. 
	 * In case the given identifier is already in use error will be thrown indicating that managers must be identifier uniquely.
	 * @param environment environment instance for the new runtime manager
     * @param identifier custom identifier for the manager
     * @return new instance of <code>RuntimeManager</code>
     */
    RuntimeManager newPerProcessInstanceRuntimeManager(RuntimeEnvironment environment, String identifier);
    
    /**
     * A Factory for this RuntimeManagerFactory
     */
    public static class Factory {
        private static boolean initialized = false;
        private static RuntimeManagerFactory INSTANCE;
        private static Logger logger = LoggerFactory.getLogger(Factory.class);
        
        private static Exception initializationException;

        /**
         * Returns a reference to the RuntimeManagerFactory singleton
         */
        public static RuntimeManagerFactory get() {
            return get(null);
        }

        public synchronized static RuntimeManagerFactory get(ClassLoader classLoader) {
            if (!initialized) {
                INSTANCE = create(classLoader);
            } else if (INSTANCE == null) {
                throw new RuntimeException("RuntimeManagerFactory was not initialized due to "+ initializationException.getMessage(), initializationException);
            }
            return INSTANCE;
        }

        private static RuntimeManagerFactory create(ClassLoader classLoader) {
            initialized = true;
            try {
                String className = System.getProperty("org.jbpm.runtime.manager.class",
                                                      "org.jbpm.runtime.manager.impl.RuntimeManagerFactoryImpl" );
                return classLoader != null ?
                       ( RuntimeManagerFactory ) Class.forName( className, true, classLoader ).newInstance() :
                       ( RuntimeManagerFactory ) Class.forName( className ).newInstance();
            } catch (Exception e) {
                initializationException = e;
                logger.error("Unable to instance RuntimeManagerFactory due to " + e.getMessage());
            }
            return null;
        }
        
        /**
         * This method is used in jBPM OSGi Activators as we need a way to force reset when starting
         * the bundles in case it failed previously.
         */
        public static synchronized void reset() {
            if (initializationException != null) {
                initializationException = null;
                initialized = false;
            }
        }
    }
}
