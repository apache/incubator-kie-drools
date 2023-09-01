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
package org.drools.mvel.compiler.reteoo;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.drl.parser.DroolsParserException;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.mvel.integrationtests.LargeRuleBase;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.internal.conf.CompositeBaseConfiguration;

import static org.assertj.core.api.Assertions.fail;

/**
 * Created by IntelliJ IDEA. User: SG0521861 Date: Mar 20, 2008 Time: 2:36:47 PM To change this template use File |
 * Settings | File Templates.
 */
@Ignore
public class ReteooBuilderPerformanceTest {
    private static final int    RULE_COUNT  = Integer.parseInt(System.getProperty("rule.count", "1000"));
    private static final int    RETEBUILDER_COUNT  = Integer.parseInt(System.getProperty("retebuilder.count", "1"));

    @Test
    public void testReteBuilder() throws DroolsParserException {
        addRules(generatePackage(RULE_COUNT));
    }

    private static void addRules(InternalKnowledgePackage pkg) {
        ReteooBuilder[]  reteBuilders   = getReteBuilders(RETEBUILDER_COUNT);

        System.out.println("Adding rules to ReteBuilder");
        long    start   = System.currentTimeMillis();
        for (ReteooBuilder reteBuilder : reteBuilders) {
            for (Rule rule : pkg.getRules())
                reteBuilder.addRule((RuleImpl)rule, Collections.emptyList());
        }
        System.out.println("Added "+RULE_COUNT+" rules to each ReteBuilder's in "+
                           format(System.currentTimeMillis()-start));
    }

    private static ReteooBuilder[] getReteBuilders(int count) {
        System.out.println("Creating "+count+" ReteBuilder's");
        ReteooBuilder[]  reteBuilders   = new ReteooBuilder[count];
        CompositeBaseConfiguration conf = (CompositeBaseConfiguration) RuleBaseFactory.newKnowledgeBaseConfiguration();

        for (int i = 0; i < reteBuilders.length; i++) {
            reteBuilders[i] = new ReteooBuilder(new KnowledgeBaseImpl("id1", conf ));
        }
        return reteBuilders;
    }

    private static InternalKnowledgePackage generatePackage(int ruleCount) throws DroolsParserException {
        StringReader    reader  = new StringReader(generateRules(ruleCount));
        
        System.out.println("Generating packages");
        KnowledgeBuilderImpl pkgBuilder = new KnowledgeBuilderImpl();
        try {
            pkgBuilder.addPackageFromDrl( reader );
        } catch ( IOException e ) { 
            fail( "Unable to parse rules\n" + e.getMessage());
        }

        if ( pkgBuilder.hasErrors() ) {
            fail( pkgBuilder.getErrors().toString() );
        }

        return pkgBuilder.getPackage("org.kie.test");
    }

    private static String generateRules(int ruleCount) {
        System.out.println("Generating "+ruleCount+" rules");
        StringBuilder   sb  = new StringBuilder( LargeRuleBase.getHeader());

        for (int i = 1; i <= ruleCount; i++) {
            sb.append(LargeRuleBase.getTemplate1("testrule"+i, i));
        }
        return sb.toString();
    }

    private static final int    MILLIS_IN_SECOND   = 1000;
    private static final int    MILLIS_IN_MINUTE    = MILLIS_IN_SECOND*60;
    private static final int    MILLIS_IN_HOUR      = MILLIS_IN_MINUTE*60;

    private static String format(long time) {
        StringBuilder   sb  = new StringBuilder();

        sb.append(time/MILLIS_IN_HOUR).append(':');
        time = time % MILLIS_IN_HOUR;
        sb.append(time/MILLIS_IN_MINUTE).append(':');
        time = time % MILLIS_IN_MINUTE;
        sb.append(time*1.0/MILLIS_IN_SECOND);
        return sb.toString();
    }
}
