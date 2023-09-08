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
package org.drools.commands;

import org.drools.commands.runtime.rule.FromExternalFactHandleCommand;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.RequestContext;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;

import static org.assertj.core.api.Assertions.assertThat;

public class FromExternalFactHandleCommandTest {

    private KieSession ksession;
    private ExecutableRunner<RequestContext> runner;
    private Context context;

    @Before
    public void setup() {
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ksession = kbase.newKieSession();
        runner = ExecutableRunner.create();
        context = ((RegistryContext) runner.createContext()).register(KieSession.class, ksession);
    }

    @After
    public void cleanUp() {
        ksession.dispose();
    }

    @Test
    public void testFromExternalFactHandleCommandNumberFormatException() {
        // DROOLS-7076 : Just to test not to throw NumberFormatException 
        String externalFormat = "0:2147483648:171497379:-1361525545:2147483648:null:NON_TRAIT:java.lang.String";
        FromExternalFactHandleCommand fromExternalFactHandleCommand = new FromExternalFactHandleCommand(externalFormat);
        FactHandle handle = runner.execute(fromExternalFactHandleCommand, context);

        assertThat(handle).isNull();
    }
}
