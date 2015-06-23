/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.persistence.kie.persistence.cdi;

import org.drools.persistence.util.PersistenceUtil;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.cdi.KBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.drools.persistence.util.PersistenceUtil.*;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CDITest {

    private HashMap<String, Object> context;
    private Environment env;
    private boolean locking;

    @Parameters(name="{0}")
    public static Collection<Object[]> persistence() {
        Object[][] locking = new Object[][] { 
                { OPTIMISTIC_LOCKING }, 
                { PESSIMISTIC_LOCKING } 
                };
        return Arrays.asList(locking);
    };
    
    public CDITest(String locking) { 
        this.locking = PESSIMISTIC_LOCKING.equals(locking);
    }
    
    @Before
    public void setUp() throws Exception {
        context = PersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        env = createEnvironment(context);
        if( locking ) { 
            env.set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        }
    }

    @After
    public void tearDown() throws Exception {
        PersistenceUtil.cleanUp(context);
    }

    @Test
    public void testCDI() {
        // DROOLS-34
        Weld w = new Weld();
        WeldContainer wc = w.initialize();

        CDIBean bean = wc.instance().select(CDIBean.class).get();
        bean.test(env);

        w.shutdown();
    }

    public static class CDIBean {
        @Inject @KBase("cdiexample")
        KieBase kBase;

        public void test(Environment env) {
            KieSession ksession = KieServices.Factory.get().getStoreServices().newKieSession(kBase, null, env);

            List<?> list = new ArrayList<Object>();

            ksession.setGlobal( "list", list );

            ksession.insert( 1 );
            ksession.insert( 2 );
            ksession.insert( 3 );

            ksession.fireAllRules();

            assertEquals( 3, list.size() );
        }
    }
}
