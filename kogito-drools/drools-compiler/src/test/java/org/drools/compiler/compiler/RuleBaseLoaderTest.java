package org.drools.compiler.compiler;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.drools.core.RuleBase;
import org.drools.compiler.integrationtests.DslTest;
import org.junit.Test;

public class RuleBaseLoaderTest {

    @Test
    public void testLoadRuleBase() throws Exception {
        final InputStream in = DslTest.class.getResourceAsStream( "HelloWorld.drl" );
        final RuleBase rb = RuleBaseLoader.getInstance().loadFromReader( new InputStreamReader( in ) );
        assertNotNull( rb );
    }

    @Test
    public void testLoadRuleBaseWithDSL() throws Exception {
        final InputStream in = DslTest.class.getResourceAsStream( "rule_with_expander_dsl.dslr" );
        final InputStream inDSL = DslTest.class.getResourceAsStream( "test_expander.dsl" );
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
