/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.process.audit;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.jbpm.process.instance.impl.demo.UIWorkItemHandler;

public class ProcessInstanceExecutor {
    
    public static final void main(String[] args) {
        try {
            //load the process
            RuleBase ruleBase = createKnowledgeBase();
            // create a new session
            StatefulSession session = ruleBase.newStatefulSession();
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
    private static RuleBase createKnowledgeBase() throws Exception {
        // create a builder
        PackageBuilder builder = new PackageBuilder();
        // load the process
        Reader source = new InputStreamReader(
            ProcessInstanceExecutor.class.getResourceAsStream("/ruleflow.rf"));
        builder.addProcessFromXml(source);
        source = new InputStreamReader(
            ProcessInstanceExecutor.class.getResourceAsStream("/ruleflow2.rf"));
        builder.addProcessFromXml(source);
       // create the knowledge base 
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(pkg);
        return ruleBase;
    }
    
}
