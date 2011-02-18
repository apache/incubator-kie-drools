/**
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

package org.drools.concurrent;

/**
 * This is an Drools Thread Pool Factory Implementation
 * responsible for making any required thread pools 
 * available to Drools. The purpose of this class is to 
 * enable Drools to directly instantiate thread pools
 * when running stand-alone or use managed thread pools
 * in JEE, Spring, or other similar managed environments 
 * 
 * @author etirelli
 */
public class DroolsThreadPoolFactoryImpl {
    
    private java.util.concurrent.ExecutorService executorService;
    
    public DroolsThreadPoolFactoryImpl() {
    }

    public DroolsThreadPoolFactoryImpl(java.util.concurrent.ExecutorService executorService) {
        super();
        this.executorService = executorService;
    }

    public java.util.concurrent.ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(java.util.concurrent.ExecutorService executorService) {
        this.executorService = executorService;
    }
    
    public java.util.concurrent.ExecutorService getThreadBoundedExecutorService( int maxThreadCount ) {
        return new ExternalExecutorService( executorService );
    }
    
}
