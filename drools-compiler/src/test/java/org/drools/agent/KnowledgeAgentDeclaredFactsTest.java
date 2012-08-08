/*
 * Copyright 2012 JBoss Inc
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

package org.drools.agent;

import junit.framework.Assert;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.conf.NewInstanceOption;
import org.drools.agent.conf.ScanDirectoriesOption;
import org.drools.agent.conf.ScanResourcesOption;
import org.drools.builder.ResourceType;
import org.drools.definition.type.FactType;
import org.drools.event.knowledgeagent.*;
import org.drools.io.Resource;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.FileSystemResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * JBRULES - 2962  / JBRULES - 3033
 */
public class KnowledgeAgentDeclaredFactsTest extends BaseKnowledgeAgentTest {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private KnowledgeAgent kagent;
    private File res = null;

    @Test
    public void testStatefulSessionNewInstance() throws Exception {
        System.out.println("************ Running Stateful Session New Instance");
        runAgent(true, true);
    }

    @Test
    public void testStatefulSessionSameInstance() throws Exception {
        System.out.println("************ Running Stateful Session Same Instance");
        runAgent(true, false);
    }

    @Test
    public void testStatelessSessionNewInstance() throws Exception {
        System.out.println("************ Running Stateless Session New Instance");
        runAgent(false, true);
    }

    @Test
    public void testStatelessSessionSameInstance() throws Exception {
        System.out.println("************ Running Stateless Session Same Instance");
        runAgent(false, false);
    }


    private void runAgent(boolean stateful, boolean newInstance) throws Exception {
        String result;

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kagent = createKAgent( kbase, newInstance, true );
        createRuleResource();


        ChangeSetHelperImpl cs = new ChangeSetHelperImpl();
        FileSystemResource r = (FileSystemResource) ResourceFactory.newFileResource(res);
        r.setResourceType( ResourceType.DRL );
        cs.addNewResource(r);

        kagent.applyChangeSet( cs.getChangeSet() );

        result = insertMessageAndFire("test1", stateful);
        Assert.assertEquals("Echo:test1", result);

        if ( newInstance ) {
            modifyRuleResourceBrandNew();
        } else {
            modifyRuleResourceIncremental();
        }
        scan( kagent );

        result = insertMessageAndFire("test2", stateful);
        Assert.assertEquals("Echo:test2", result);

        kagent.dispose();
    }

    private String insertMessageAndFire(String message, boolean stateful) throws IllegalAccessException, InstantiationException {
        System.out.println("********** Firing rules");

        String result = null;

        FactType testFactType = kagent.getKnowledgeBase().getFactType("test", "TestFact");
        Object fact = testFactType.newInstance();
        testFactType.set(fact, "message", message);

        if (stateful) {
            StatefulKnowledgeSession session = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
            session.insert(fact);
            session.fireAllRules();
            result = (String) testFactType.get(fact, "message");
            session.dispose();
        } else {
            StatelessKnowledgeSession session = kagent.getKnowledgeBase().newStatelessKnowledgeSession();
            session.execute(fact);
            result = (String) testFactType.get(fact, "message");
        }

        return result;
    }

    private void createRuleResource() throws IOException {
        String ruleString = "package test; \n" +
                "declare TestFact \n" +
                "  message : String \n" +
                "end \n" +
                "rule test1 \n" +
                "  when \n" +
                "    $m : TestFact( message == \"test1\") \n" +
                "  then \n" +
                "    System.out.println(\"********** FOUND \" + $m.getMessage()); \n" +
                "    $m.setMessage(\"Echo:\" + $m.getMessage()); \n" +
                "end \n";

        res = fileManager.write( "rule.drl", ruleString );
    }

    private void modifyRuleResourceIncremental() throws IOException {
        String ruleString = "package test; \n" +
                "rule test2 \n" +
                "  when \n" +
                "    $m : TestFact( message == \"test2\") \n" +
                "  then \n" +
                "    System.out.println(\"********** FOUND \" + $m.getMessage()); \n" +
                "    $m.setMessage(\"Echo:\" + $m.getMessage()); \n" +
                "end \n";

        res = fileManager.write( "rule.drl", ruleString );
    }

    private void modifyRuleResourceBrandNew() throws IOException {
        String ruleString = "package test; \n" +
                "declare TestFact \n" +
                "  message : String \n" +
                "end \n" +
                " \n " +
                "rule test2 \n" +
                "  when \n" +
                "    $m : TestFact( message == \"test2\") \n" +
                "  then \n" +
                "    System.out.println(\"********** FOUND \" + $m.getMessage()); \n" +
                "    $m.setMessage(\"Echo:\" + $m.getMessage()); \n" +
                "end \n";

        res = fileManager.write( "rule.drl", ruleString );
    }


}
