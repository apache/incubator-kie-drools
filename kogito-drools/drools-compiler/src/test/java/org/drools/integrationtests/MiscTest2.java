/*
 * Copyright 2005 JBoss Inc
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

package org.drools.integrationtests;

import org.drools.Address;
import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Run all the tests with the ReteOO engine implementation
 */
public class MiscTest2 extends CommonTestMethodBase {

    private static Logger logger = LoggerFactory.getLogger(MiscTest2.class);


    @Test
    public void testUpdateWithNonEffectiveActivations() throws Exception {
        // JBRULES-3604
        String str = "package inheritance\n" +
                "\n" +
                "import org.drools.Address\n" +
                "\n" +
                "rule \"Parent\"\n" +
                "    enabled false\n" +
                "    when \n" +
                "        $a : Address(suburb == \"xyz\")\n" +
                "    then \n" +
                "        System.out.println( $a ); \n" +
                "end \n" +
                "rule \"Child\" extends \"Parent\" \n" +
                "    when \n" +
                "        $b : Address( this == $a, street == \"123\")\n" +
                "    then \n" +
                "        System.out.println( $b ); \n" +
                "end";


        KnowledgeBase knowledgeBase = null;

        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        builder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL);

        if ( builder.hasErrors() ) {
            throw new RuntimeException(builder.getErrors().toString());
        }
        knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages(builder.getKnowledgePackages());

        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();

        Address address = new Address();

        address.setSuburb("xyz");
        org.drools.runtime.rule.FactHandle addressHandle = ksession.insert(address);

        int rulesFired = ksession.fireAllRules();

        assertEquals( 0, rulesFired );

        address.setStreet("123");


        ksession.update(addressHandle, address);

        rulesFired = ksession.fireAllRules();

        System.out.println( rulesFired );
        assertEquals( 1, rulesFired );

        ksession.dispose();
    }
}
