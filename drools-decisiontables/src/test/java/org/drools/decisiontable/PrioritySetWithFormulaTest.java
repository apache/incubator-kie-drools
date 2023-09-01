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

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieModuleModel;

import static org.assertj.core.api.Assertions.assertThat;

public class PrioritySetWithFormulaTest {

    private KieBase kieBase;

    @Before
    public void init() {

        final KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieModuleModel kmodule = ks.newKieModuleModel();

        kfs.writeKModuleXML(kmodule.toXML());
        kfs.write(ks.getResources().newClassPathResource("prioritySetWithFormula.drl.xls", getClass()));

        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();

        assertThat(kieBuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR)).isEmpty();

        kieBase = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).getKieBase();

    }

    @Test
    public void test() {

        // RULE CheeseWorld_11 has salience "=3+2"
        RuleImpl cheeseWorld11 = (RuleImpl) kieBase.getRule( "test", "CheeseWorld_11" );
        assertThat(cheeseWorld11.getSalience().getValue()).isEqualTo(5);

        // RULE CheeseWorld_12 has salience "=ROW()"
        RuleImpl cheeseWorld12 = (RuleImpl) kieBase.getRule( "test", "CheeseWorld_12" );
        
        assertThat(cheeseWorld12.getSalience().getValue()).isEqualTo(12);

    }
}

