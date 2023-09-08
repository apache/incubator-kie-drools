/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.kie.builder.impl;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import org.drools.compiler.kie.builder.impl.event.KieScannerEventSupport;
import org.drools.core.impl.InternalKieContainer;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.event.kiescanner.KieScannerEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractKieScanner<T> implements KieScanner {

    private Timer timer;

    private static final Logger log = LoggerFactory.getLogger( KieScanner.class );

    protected InternalKieContainer kieContainer;

    private volatile Status status = Status.STARTING;

    private long pollingInterval;

    protected KieScannerEventSupport listeners = new KieScannerEventSupport();

    @Override
    public final void addListener( KieScannerEventListener listener ) {
        listeners.addEventListener( listener );
    }

    @Override
    public final void removeListener( KieScannerEventListener listener ) {
        listeners.removeEventListener( listener );
    }

    @Override
    public final Collection<KieScannerEventListener> getListeners() {
        return listeners.getEventListeners();
    }

    protected final void changeStatus( Status status ) {
        this.status = status;
        listeners.fireKieScannerStatusChangeEventImpl( status );
    }

    public final synchronized ReleaseId getScannerReleaseId() {
        return kieContainer.getContainerReleaseId();
    }

    public final synchronized ReleaseId getCurrentReleaseId() {
        return kieContainer.getReleaseId();
    }

    public final synchronized Status getStatus() {
        return status;
    }

    public final synchronized void start( long pollingInterval ) {
        if ( getStatus() == Status.SHUTDOWN ) {
            throw new IllegalStateException( "The scanner was shut down and can no longer be started." );
        }
        if ( pollingInterval <= 0 ) {
            throw new IllegalArgumentException( "pollingInterval must be positive" );
        }
        if ( timer != null ) {
            throw new IllegalStateException( "The scanner is already running" );
        }
        startScanTask( pollingInterval );
    }

    public final synchronized void stop() {
        if ( getStatus() == Status.SHUTDOWN ) {
            throw new IllegalStateException( "The scanner was already shut down." );
        }
        if ( timer != null ) {
            timer.cancel();
            timer = null;
        }
        this.pollingInterval = 0;
        changeStatus( Status.STOPPED );
    }

    public final synchronized long getPollingInterval() {
        return this.pollingInterval;
    }

    public final void shutdown() {
        if ( getStatus() != Status.SHUTDOWN ) {
            stop(); // making sure it is stopped
            changeStatus( Status.SHUTDOWN );
        }
    }

    private void startScanTask( long pollingInterval ) {
        changeStatus( Status.RUNNING );
        this.pollingInterval = pollingInterval;
        timer = new Timer( true );
        timer.schedule( new ScanTask(), pollingInterval, pollingInterval );
    }

    private class ScanTask extends TimerTask {
        public void run() {
            synchronized (AbstractKieScanner.this) {
                // don't scan if the scanner was already stopped! This would lead to inconsistent scanner behavior.
                if ( status == Status.STOPPED ) {
                    return;
                }
                scanNow();
                changeStatus( Status.RUNNING );
            }
        }
    }

    public final synchronized void scanNow() {
        if (getStatus() == Status.SHUTDOWN ) {
            throw new IllegalStateException("The scanner was already shut down and can no longer be used.");
        }
        // Polling can be started so remember the original state.
        final Status originalStatus = status;
        try {
            changeStatus( Status.SCANNING );
            T updatedArtifacts = internalScan();
            if ( updatedArtifacts == null ) {
                changeStatus( originalStatus );
                return;
            }
            changeStatus( Status.UPDATING );
            internalUpdate( updatedArtifacts );
        } finally {
            changeStatus( originalStatus );
        }
    }

    protected abstract T internalScan();

    protected abstract void internalUpdate( T updatedArtifacts );
}