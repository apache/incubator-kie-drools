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
package org.drools.testcoverage.functional.decisiontable;

import org.drools.testcoverage.common.model.Record;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class DecisionTableKieContainerTest {

    @Test
    public void testCSVWithRuleTemplate() {
        final KieContainer kieContainer = KieServices.Factory.get().getKieClasspathContainer();
        final KieBase kieBase = kieContainer.getKieBase("csvwithtemplate");
        assertThat(kieBase).isNotNull();

        final StatelessKieSession kieSession = kieContainer.newStatelessKieSession("csvwithtemplatesession");
        Record record1 = new Record();
        record1.setCategory("Test");
        kieSession.execute(record1);
        assertThat(record1.getPhoneNumber()).isNotNull();

        Record record2 = new Record();
        record2.setCategory("Test2");
        kieSession.execute(record2);
        assertThat(record2.getPhoneNumber()).isNotNull();
    }
}
