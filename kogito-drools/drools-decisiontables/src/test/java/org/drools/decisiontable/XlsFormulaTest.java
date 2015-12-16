/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.decisiontable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

public class XlsFormulaTest {

    @Test
    public void testFormulaValue() throws Exception {
        // DROOLS-643

        Resource dt = ResourceFactory.newClassPathResource("/data/XlsFormula.xls", getClass());

        KieSession ksession = getKieSession( dt );
        
        final List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        ksession.insert( new Person( "michael", "stilton", 1 ) );
        ksession.fireAllRules();
        assertEquals( "10", list.get(0) ); // 10
        
        ksession.insert( new Person( "michael", "stilton", 2 ) );
        ksession.fireAllRules();
        assertEquals( "11", list.get(1) ); // =ROW()

        ksession.insert( new Person( "michael", "stilton", 3 ) );
        ksession.fireAllRules();
        assertEquals( "21", list.get(2) ); // =SUM(D10:D11)
    }

    private KieSession getKieSession(Resource dt) {
        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( dt );
        KieBuilder kb = ks.newKieBuilder( kfs ).buildAll();
        assertTrue( kb.getResults().getMessages().isEmpty() );

        // get the session
        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        return ksession;
    }


}
