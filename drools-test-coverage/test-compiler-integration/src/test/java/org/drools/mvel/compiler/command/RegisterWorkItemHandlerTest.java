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
package org.drools.mvel.compiler.command;

import java.util.Collection;

import org.drools.core.process.WorkItem;
import org.drools.core.process.impl.DefaultWorkItemManager;
import org.drools.core.process.impl.WorkItemImpl;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.command.CommandFactory;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class RegisterWorkItemHandlerTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public RegisterWorkItemHandlerTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testRegisterWorkItemHandlerWithStatelessSession() {
        String str = 
                "package org.kie.workitem.test \n" +
                "import " + DefaultWorkItemManager.class.getCanonicalName() + "\n" +
                "import " + WorkItem.class.getCanonicalName() + "\n" +
                "import " + WorkItemImpl.class.getCanonicalName() + "\n" + 
                "rule r1 when \n" + 
                "then \n" +
                "  WorkItem wi = new WorkItemImpl(); \n" +
                "  wi.setName( \"wihandler\" ); \n" +
                "  DefaultWorkItemManager wim = ( DefaultWorkItemManager ) kcontext.getKieRuntime().getWorkItemManager(); \n" +
                "  wim.internalExecuteWorkItem(wi); \n" +
                "end \n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);

        final boolean[] answer = new boolean[] { false };
        StatelessKieSession ks = kbase.newStatelessKieSession();
        ks.execute( CommandFactory.newRegisterWorkItemHandlerCommand( new WorkItemHandler() {
            
            public void executeWorkItem(org.kie.api.runtime.process.WorkItem workItem,
                                        WorkItemManager manager) {
                answer[0] = true;
            }
            
            public void abortWorkItem(org.kie.api.runtime.process.WorkItem workItem,
                                      WorkItemManager manager) {
                // TODO Auto-generated method stub
                
            }
        },  "wihandler" ) );

        assertThat(answer[0]).isTrue();
    }
}
