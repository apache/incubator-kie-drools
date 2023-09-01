package org.drools.mvel.compiler.compiler;

import java.io.IOException;
import java.io.InputStreamReader;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.drl.parser.DroolsParserException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        InternalKnowledgePackage pkg = builder.getPackage("org.drools.mvel.compiler.test");
        assertThat(builder.hasErrors()).isFalse();
        assertThat(pkg).isNotNull();
        if (showResults) {
            System.out.print( "Time taken for " + msg + " : " + (System.currentTimeMillis() - start) );
        }
    }
    
}
