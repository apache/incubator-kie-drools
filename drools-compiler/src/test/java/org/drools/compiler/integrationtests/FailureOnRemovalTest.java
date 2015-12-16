/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Properties;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.compiler.DroolsParserException;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.DefaultDialectOption;
import org.kie.internal.conf.SequentialOption;
import org.kie.internal.conf.ShareAlphaNodesOption;
import org.kie.internal.conf.ShareBetaNodesOption;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;

public class FailureOnRemovalTest extends CommonTestMethodBase {
    
    private static final String  LS                   = System.getProperty( "line.separator" );
    private static final String  PACKAGE              = "failure_on_removal";
    private static final String  RULE_1               = "rule_1";
    private static final String  RULE_2               = "rule_2";
    private static final String  RULE_3               = "rule_3";
    private static final boolean SHARE_BETA_NODES     = true;
    private static final boolean NOT_SHARE_BETA_NODES = false;

    @Test
    public void testWithBetaNodeSharing() throws Exception {
        runTest( SHARE_BETA_NODES );
    }

    @Test
    public void testWithoutBetaNodeSharing() throws Exception {
        runTest( NOT_SHARE_BETA_NODES );
    }

    private void runTest(boolean shareBetaNodes) throws Exception {
        KnowledgeBase kbase = createKnowledgeBase( shareBetaNodes );
        Collection<KnowledgePackage> rule1 = compileRule( RULE_1 );
        kbase.addKnowledgePackages( rule1 );

        // we need to add at least two rules. Test will not fail with only one rule.
        Collection<KnowledgePackage> rule2 = compileRule( RULE_2 );
        kbase.addKnowledgePackages( rule2 );

        kbase.removeRule( PACKAGE,
                          RULE_1 );
        
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        int fired = ksession.fireAllRules();
        ksession.dispose();
        
        assertEquals( 1, fired );

        Collection<KnowledgePackage> rule3 = compileRule( RULE_3 );
        kbase.addKnowledgePackages( rule3 );
    }

    private Collection<KnowledgePackage> compileRule(String name) throws DroolsParserException,
                                                                 IOException {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(createKnowledgeBuilderConfiguration());
        String drl = getDrl( name );
        Reader drlReader = new StringReader( drl );
        kbuilder.add( ResourceFactory.newReaderResource( drlReader ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );
        return kbuilder.getKnowledgePackages();
    }

    private KnowledgeBuilderConfiguration createKnowledgeBuilderConfiguration() {
        Properties properties = new Properties();
        properties.setProperty( "drools.dialect.java.compiler",
                                "JANINO" );
        KnowledgeBuilderConfiguration kconf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration( properties,
                                                                                                        getClass().getClassLoader() );
        kconf.setOption( DefaultDialectOption.get( "java" ) );
        return kconf;
    }

    private KnowledgeBase createKnowledgeBase(boolean shareBetaNodes) {
        KieBaseConfiguration ruleBaseConfiguration = createKnowledgeBaseConfiguration( shareBetaNodes );
        return KnowledgeBaseFactory.newKnowledgeBase( ruleBaseConfiguration );
    }

    private KieBaseConfiguration createKnowledgeBaseConfiguration(boolean shareBetaNodes) {
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( SequentialOption.NO );
        kconf.setOption( ShareAlphaNodesOption.YES );
        kconf.setOption( shareBetaNodes ? ShareBetaNodesOption.YES : ShareBetaNodesOption.NO );
        return kconf;
    }

    private String getDrl(String name) {
        return new StringBuffer( "package " ).append( PACKAGE ).append( LS ).append( "rule '" ).append( name ).append( '\'' ).append( LS ).append( "when" ).append( LS ).append( "eval (true)" ).append( LS ).append( "then" ).append( LS ).append( "end" ).append( LS ).toString();
    }
}
