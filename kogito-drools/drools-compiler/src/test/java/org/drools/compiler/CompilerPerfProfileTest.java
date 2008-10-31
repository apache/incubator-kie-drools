package org.drools.compiler;

import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.drools.rule.Package;

public class CompilerPerfProfileTest extends TestCase {

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
        final PackageBuilder builder = new PackageBuilder();
        long start = System.currentTimeMillis();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( resource ) ) );
        final Package pkg = builder.getPackage();
        assertFalse(builder.hasErrors());
        assertNotNull(pkg);
        if (showResults) {
            System.out.print( "Time taken for " + msg + " : " + (System.currentTimeMillis() - start) );
        }
    }
    
}
