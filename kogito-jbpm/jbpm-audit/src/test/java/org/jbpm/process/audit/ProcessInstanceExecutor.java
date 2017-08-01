/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.audit;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.jbpm.process.instance.impl.demo.UIWorkItemHandler;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class ProcessInstanceExecutor {
    
    public static final void main(String[] args) {
        try {
            //load the process
            KieBase kbase = createKnowledgeBase();
            // create a new session
            KieSession session = kbase.newKieSession();
            new JPAWorkingMemoryDbLogger(session);
            UIWorkItemHandler uiHandler = new UIWorkItemHandler();
            session.getWorkItemManager().registerWorkItemHandler("Human Task", uiHandler);
            uiHandler.setVisible(true);
            new ProcessInstanceExecutorFrame(session).setVisible(true);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Creates the knowledge base by loading the process definition.
     */
    private static KieBase createKnowledgeBase() throws Exception {
        // create a builder
        KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        // load the process
        Reader source = new InputStreamReader(
            ProcessInstanceExecutor.class.getResourceAsStream("/ruleflow.rf"));
        builder.addProcessFromXml(source);
        source = new InputStreamReader(
            ProcessInstanceExecutor.class.getResourceAsStream("/ruleflow2.rf"));
        builder.addProcessFromXml(source);
       // create the knowledge base 
        InternalKnowledgeBase ruleBase = KnowledgeBaseFactory.newKnowledgeBase();
        ruleBase.addPackages(Arrays.asList(builder.getPackages()));
        return ruleBase;
    }
    
}
