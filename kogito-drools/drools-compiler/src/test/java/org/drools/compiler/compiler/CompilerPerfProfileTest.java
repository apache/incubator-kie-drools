package org.drools.compiler.compiler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStreamReader;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.junit.Test;

public class CompilerPerfProfileTest {

    @Test
    public void testProfileRuns() throws Exception {

        //first run for warm up
        build("JDT", "largeRuleNumber.drl", false);
        build("MVEL", "largeRuleNumberMVEL.drl", false);

        System.gc();
        Thread.sleep( 100 );
        
        build("MVEL", "largeRuleNumberMVEL.drl", true);

        System.gc();
        Thread.sleep( 100 );

        
        build("JDT", "largeRuleNumber.drl", true);
        

        
        
        
    }

    private void build(String msg, String resource, boolean showResults) throws DroolsParserException,
                        IOException {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        long start = System.currentTimeMillis();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( resource ) ) );
        InternalKnowledgePackage pkg = builder.getPackage();
        assertFalse(builder.hasErrors());
        assertNotNull(pkg);
        if (showResults) {
            System.out.print( "Time taken for " + msg + " : " + (System.currentTimeMillis() - start) );
        }
    }
    
}
