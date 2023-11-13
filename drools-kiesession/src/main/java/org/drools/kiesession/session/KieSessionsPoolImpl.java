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

import org.drools.core.SessionConfiguration;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;

public class KieSessionsPoolImpl extends AbstractKieSessionsPool {

    private final InternalKnowledgeBase kBase;

    public KieSessionsPoolImpl(InternalKnowledgeBase kBase, int initialSize) {
        super(initialSize);
        this.kBase = kBase;
    }

    @Override
    public KieSession newKieSession() {
        return newKieSession( kBase.getSessionConfiguration() );
    }

    @Override
    public KieSession newKieSession( KieSessionConfiguration conf ) {
        return getPool(conf, false).get();
    }

    @Override
    public StatelessKieSession newStatelessKieSession() {
        return newStatelessKieSession( kBase.getSessionConfiguration() );
    }

    @Override
    public StatelessKieSession newStatelessKieSession( KieSessionConfiguration conf ) {
        return new StatelessKnowledgeSessionImpl( conf, getPool(conf, true) );
    }

    @Override
    protected String getKey( String kSessionName, KieSessionConfiguration conf, boolean stateless ) {
        String key = stateless ? "DEFAULT_STATELESS" : "DEFAULT";
        return conf == null ? key : key + "@" + System.identityHashCode( conf );
    }

    @Override
    protected StatefulSessionPool createStatefulSessionPool( String kSessionName, KieSessionConfiguration conf, boolean stateless ) {
        return new StatefulSessionPool(kBase, initialSize, () ->
                stateless ?
                    ((StatefulKnowledgeSessionImpl ) RuntimeComponentFactory.get().createStatefulSession(kBase, environment, conf.as(SessionConfiguration.KEY), true )).setStateless( true ) :
                    (StatefulKnowledgeSessionImpl ) kBase.newKieSession(conf, environment, true));
    }

}
