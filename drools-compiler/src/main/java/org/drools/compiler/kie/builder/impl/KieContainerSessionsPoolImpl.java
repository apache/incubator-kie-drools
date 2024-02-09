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

import org.drools.kiesession.session.AbstractKieSessionsPool;
import org.drools.kiesession.session.StatefulSessionPool;
import org.drools.kiesession.session.StatelessKnowledgeSessionImpl;
import org.kie.api.runtime.KieContainerSessionsPool;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;

public class KieContainerSessionsPoolImpl extends AbstractKieSessionsPool implements KieContainerSessionsPool {

    private final KieContainerImpl kContainer;

    KieContainerSessionsPoolImpl( KieContainerImpl kContainer, int initialSize ) {
        super(initialSize);
        this.kContainer = kContainer;
    }

    @Override
    public KieSession newKieSession() {
        return newKieSession( null, null );
    }

    @Override
    public KieSession newKieSession( KieSessionConfiguration conf ) {
        return newKieSession( null, conf );
    }

    @Override
    public KieSession newKieSession( String kSessionName ) {
        return newKieSession( kSessionName, null );
    }

    @Override
    public KieSession newKieSession( String kSessionName, KieSessionConfiguration conf ) {
        return getPool(kSessionName, conf, false).get();
    }

    @Override
    public StatelessKieSession newStatelessKieSession() {
        return newStatelessKieSession( null, null );
    }

    @Override
    public StatelessKieSession newStatelessKieSession( KieSessionConfiguration conf ) {
        return newStatelessKieSession( null, conf );
    }

    @Override
    public StatelessKieSession newStatelessKieSession( String kSessionName ) {
        return newStatelessKieSession( kSessionName, null );
    }

    @Override
    public StatelessKieSession newStatelessKieSession( String kSessionName, KieSessionConfiguration conf ) {
        return new StatelessKnowledgeSessionImpl( conf, getPool(kSessionName, conf, true) );
    }

    @Override
    protected StatefulSessionPool createStatefulSessionPool( String kSessionName, KieSessionConfiguration conf, boolean stateless ) {
        return kContainer.createKieSessionsPool(kSessionName, conf, environment, initialSize, stateless);
    }

    @Override
    protected String getKey(String kSessionName, KieSessionConfiguration conf, boolean stateless) {
        String key = kSessionName == null ? (stateless ? "DEFAULT_STATELESS" : "DEFAULT") : kSessionName;
        return conf == null ? key : key + "@" + System.identityHashCode( conf );
    }
}
