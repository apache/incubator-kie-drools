package org.drools.integrationtests;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Properties;

import org.drools.CommonTestMethodBase;
import org.drools.compiler.DroolsParserException;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KieBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.conf.DefaultDialectOption;
import org.kie.conf.SequentialOption;
import org.kie.conf.ShareAlphaNodesOption;
import org.kie.conf.ShareBetaNodesOption;
import org.kie.definition.KnowledgePackage;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.StatefulKnowledgeSession;

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
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( createKnowledgeBuilderConfiguration() );
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
