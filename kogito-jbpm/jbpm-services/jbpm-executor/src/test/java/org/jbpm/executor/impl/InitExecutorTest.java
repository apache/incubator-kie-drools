/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.executor.impl;

import static org.mockito.Mockito.*;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jbpm.executor.impl.ExecutorImpl;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.executor.ExecutorStoreService;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InitExecutorTest {
    
    @After
    public void cleanup() {
        System.clearProperty("org.kie.executor.pool.size");
        System.clearProperty("org.kie.executor.initial.delay");
    }

    @Test
    public void testSingleThreadExecutor() {
        
        ScheduledExecutorService mockedExecutorService = Mockito.mock(ScheduledExecutorService.class);
        ExecutorImpl executor = buildExecutor(mockedExecutorService);
        
        executor.init();
        
        verify(mockedExecutorService, times(1)).scheduleAtFixedRate(eq(null), eq(2100L), eq(3000L), eq(TimeUnit.MILLISECONDS));
    }
    
    @Test
    public void testSingleThreadExecutorCustomInitialDelay() {
        System.setProperty("org.kie.executor.initial.delay", "5000");
        ScheduledExecutorService mockedExecutorService = Mockito.mock(ScheduledExecutorService.class);
        ExecutorImpl executor = buildExecutor(mockedExecutorService);
        
        executor.init();
        
        verify(mockedExecutorService, times(1)).scheduleAtFixedRate(eq(null), eq(7000L), eq(3000L), eq(TimeUnit.MILLISECONDS));
    }
    
    @Test
    public void testManyThreadExecutor() {
        System.setProperty("org.kie.executor.pool.size", "4");
        ScheduledExecutorService mockedExecutorService = Mockito.mock(ScheduledExecutorService.class);
        ExecutorImpl executor = buildExecutor(mockedExecutorService);
        
        executor.init();
        
        verify(mockedExecutorService, times(1)).scheduleAtFixedRate(eq(null), eq(2100L), eq(3000L), eq(TimeUnit.MILLISECONDS));
        verify(mockedExecutorService, times(1)).scheduleAtFixedRate(eq(null), eq(4100L), eq(3000L), eq(TimeUnit.MILLISECONDS));
        verify(mockedExecutorService, times(1)).scheduleAtFixedRate(eq(null), eq(6100L), eq(3000L), eq(TimeUnit.MILLISECONDS));
        verify(mockedExecutorService, times(1)).scheduleAtFixedRate(eq(null), eq(8100L), eq(3000L), eq(TimeUnit.MILLISECONDS));
    }
    
    @Test
    public void testManyThreadExecutorCustomInitialDelay() {
        System.setProperty("org.kie.executor.pool.size", "4");
        System.setProperty("org.kie.executor.initial.delay", "5000");
        ScheduledExecutorService mockedExecutorService = Mockito.mock(ScheduledExecutorService.class);
        ExecutorImpl executor = buildExecutor(mockedExecutorService);
        
        executor.init();
        
        verify(mockedExecutorService, times(1)).scheduleAtFixedRate(eq(null), eq(7000L), eq(3000L), eq(TimeUnit.MILLISECONDS));
        verify(mockedExecutorService, times(1)).scheduleAtFixedRate(eq(null), eq(9000L), eq(3000L), eq(TimeUnit.MILLISECONDS));
        verify(mockedExecutorService, times(1)).scheduleAtFixedRate(eq(null), eq(11000L), eq(3000L), eq(TimeUnit.MILLISECONDS));
        verify(mockedExecutorService, times(1)).scheduleAtFixedRate(eq(null), eq(13000L), eq(3000L), eq(TimeUnit.MILLISECONDS));
    }
    
    protected ExecutorImpl buildExecutor(ScheduledExecutorService mockedExecutorService) {
        ExecutorStoreService mockedExecutorStoreService = Mockito.mock(ExecutorStoreService.class);
        
        
        ExecutorImpl executor = new ExecutorImpl(){

            @Override
            protected ScheduledExecutorService getScheduledExecutorService() {
                return mockedExecutorService;
            }
            
        };
        executor.setExecutorStoreService(mockedExecutorStoreService);
        
        return executor;
    }
}
