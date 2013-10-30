/*
 * Copyright 2013 JBoss Inc
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
package org.kie.internal.runtime.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated
 * Use <code>org.kie.api.runtime.manager.RuntimeManagerFactory</code>
 */
public interface RuntimeManagerFactory extends org.kie.api.runtime.manager.RuntimeManagerFactory {
   	
    
	/**
	 * @deprecated
	 * Use <code>org.kie.api.runtime.manager.RuntimeManagerFactory</code>
	 */
    public static class Factory {
        private static RuntimeManagerFactory INSTANCE;
        private static Logger logger = LoggerFactory.getLogger(Factory.class);
        
        static {
            try {                
                INSTANCE = ( RuntimeManagerFactory ) 
                		Class.forName( System.getProperty("org.jbpm.runtime.manager.class", 
                				"org.jbpm.runtime.manager.impl.RuntimeManagerFactoryImpl") ).newInstance();
            } catch (Exception e) {
                logger.error("Unable to instance RuntimeManagerFactory due to " + e.getMessage());
            }
        }

        /**
         * Returns a reference to the RuntimeManagerFactory singleton
         */
        public static RuntimeManagerFactory get() {
            if (INSTANCE == null) {
                throw new RuntimeException("RuntimeManagerFactory was not initialized, see previous errors");
            }
            return INSTANCE;
        }
    }
}
