package org.drools.compiler;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.RuleBase;
import org.drools.integrationtests.IntegrationCases;
import org.drools.leaps.RuleBaseImpl;

import junit.framework.TestCase;

public class RuleBaseLoaderTest extends TestCase {

    public void testLoadRuleBase() throws Exception {
        InputStream in = IntegrationCases.class.getResourceAsStream( "HelloWorld.drl" );
        RuleBase rb = RuleBaseLoader.getInstance().loadFromReader(new InputStreamReader(in));
        assertNotNull(rb);
    }
    
    public void testLoadRuleBaseWithDSL() throws Exception {
        InputStream in = IntegrationCases.class.getResourceAsStream( "rule_with_expander_dsl.drl" );
        InputStream inDSL = IntegrationCases.class.getResourceAsStream( "test_expander.dsl" );
        RuleBase rb = RuleBaseLoader.getInstance().loadFromReader(new InputStreamReader(in), new InputStreamReader(inDSL));
        assertNotNull(rb);
    }    
    
    public void testEngineType() throws Exception {
        InputStream in = IntegrationCases.class.getResourceAsStream( "HelloWorld.drl" );
        RuleBaseLoader loader = RuleBaseLoader.getInstance();
        loader.setDefaultEngineType( RuleBase.LEAPS );
        RuleBase rb = loader.loadFromReader(new InputStreamReader(in));
        assertNotNull(rb);
        assertTrue(rb instanceof RuleBaseImpl);
        
    }
    
    
}
