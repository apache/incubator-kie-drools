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
package org.drools.decisiontable;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class XlsFormulaTest {
    
    private KieSession ksession;

    @After
    public void tearDown() {
        if (ksession != null) {
            ksession.dispose();
        }
    }

    @Test
    public void testFormulaValue() throws Exception {
        // DROOLS-643

        Resource dt = ResourceFactory.newClassPathResource("/data/XlsFormula.drl.xls", getClass());

        ksession = getKieSession(dt);
        
        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.insert(new Person("michael", "stilton", 1));
        ksession.fireAllRules();
        assertThat(list.get(0)).isEqualTo("10"); // 10
        
        ksession.insert(new Person("michael", "stilton", 2));
        ksession.fireAllRules();
        assertThat(list.get(1)).isEqualTo("11"); // =ROW()

        ksession.insert(new Person("michael", "stilton", 3));
        
        ksession.fireAllRules();
        
        assertThat(list.get(2)).isEqualTo("21"); // =SUM(D10:D11)
    }

    private KieSession getKieSession(Resource dt) {
        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write(dt);
        KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
        assertThat(kb.getResults().getMessages().isEmpty()).isTrue();

        // get the session
        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        return ksession;
    }


}
