/*
 * Copyright 2011 Red Hat Inc.
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
package org.drools.persistence.map.impl;

import java.util.Map;

import org.drools.persistence.mapdb.MapDBEnvironmentName;
import org.drools.persistence.mapdb.PersistentSessionSerializer;
import org.drools.persistence.mapdb.marshaller.MapDBPlaceholderResolverStrategy;
import org.drools.persistence.mapdb.util.MapDBPersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapDBBasedPersistenceTest extends MapPersistenceTest {

    private static Logger logger = LoggerFactory.getLogger(MapDBPlaceholderResolverStrategy.class);
    
    private Map<String, Object> context;
    private DB db;
    
    @Before
    public void setUp() throws Exception {
        context = MapDBPersistenceUtil.setupMapDB();
        db = (DB) context.get(MapDBEnvironmentName.DB_OBJECT);
    }
    
    @After
    public void tearDown() throws Exception {
        MapDBPersistenceUtil.cleanUp(context);
    }
    

    @Override
    protected KieSession createSession(KieBase kbase) {
        Environment env = MapDBPersistenceUtil.createEnvironment(context);
        return KieServices.Factory.get().getStoreServices().newKieSession( kbase, null, env);
    }

    @Override
    protected KieSession disposeAndReloadSession(KieSession ksession, KieBase kbase) {
        long ksessionId = ksession.getIdentifier();
        ksession.dispose();
        return KieServices.Factory.get().getStoreServices().loadKieSession(ksessionId, kbase, null, MapDBPersistenceUtil.createEnvironment(context));
    }

    @Override
    protected long getSavedSessionsCount() {
        logger.info("quering number of saved sessions.");
        return  db.treeMap("session", Serializer.LONG, new PersistentSessionSerializer()).createOrOpen().sizeLong();
    }

}
