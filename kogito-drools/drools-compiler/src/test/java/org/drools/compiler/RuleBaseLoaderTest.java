package org.drools.compiler;

import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.integrationtests.IntegrationCases;

public class RuleBaseLoaderTest extends TestCase {

    public void testLoadRuleBase() throws Exception {
        final InputStream in = IntegrationCases.class.getResourceAsStream( "HelloWorld.drl" );
        final RuleBase rb = RuleBaseLoader.getInstance().loadFromReader( new InputStreamReader( in ) );
        assertNotNull( rb );
    }

    public void testLoadRuleBaseWithDSL() throws Exception {
        final InputStream in = IntegrationCases.class.getResourceAsStream( "rule_with_expander_dsl.drl" );
        final InputStream inDSL = IntegrationCases.class.getResourceAsStream( "test_expander.dsl" );
        final RuleBase rb = RuleBaseLoader.getInstance().loadFromReader( new InputStreamReader( in ),
                                                                         new InputStreamReader( inDSL ) );
        assertNotNull( rb );
    }

    // todo: fix for LEAPS
    //    public void testEngineType() throws Exception {
    //        final InputStream in = IntegrationCases.class.getResourceAsStream( "HelloWorld.drl" );
    //        final RuleBaseLoader loader = RuleBaseLoader.getInstance();
    //        loader.setDefaultEngineType( RuleBase.LEAPS );
    //        final RuleBase rb = loader.loadFromReader( new InputStreamReader( in ) );
    //        assertNotNull( rb );
    //        assertTrue( rb instanceof LeapsRuleBase );
    //
    //    }

}
