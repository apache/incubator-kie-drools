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
package org.drools.kiesession.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.impl.EnvironmentFactory;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.KieSessionsPool;

public abstract class AbstractKieSessionsPool implements KieSessionsPool {

    private volatile boolean alive = true;

    protected final int initialSize;

    private final Map<String, StatefulSessionPool> pools = new ConcurrentHashMap<>();

    protected final Environment environment = EnvironmentFactory.newEnvironment();

    protected AbstractKieSessionsPool( int initialSize ) {
        this.initialSize = initialSize;
    }

    @Override
    public void shutdown() {
        alive = false;
        pools.values().forEach( StatefulSessionPool::shutdown );
        pools.clear();
    }

    protected StatefulSessionPool getPool( KieSessionConfiguration conf, boolean stateless) {
        return getPool( null, conf, stateless);
    }

    protected StatefulSessionPool getPool( String kSessionName, KieSessionConfiguration conf, boolean stateless) {
        checkAlive();
        return pools.computeIfAbsent( getKey(kSessionName, conf, stateless), k -> createStatefulSessionPool( kSessionName, conf, stateless ) );
    }

    private void checkAlive() {
        if (!alive) {
            throw new IllegalStateException( "Illegal method call. This session pool was previously disposed." );
        }
    }

    protected abstract StatefulSessionPool createStatefulSessionPool( String kSessionName, KieSessionConfiguration conf, boolean stateless );

    protected abstract String getKey(String kSessionName, KieSessionConfiguration conf, boolean stateless);
}
