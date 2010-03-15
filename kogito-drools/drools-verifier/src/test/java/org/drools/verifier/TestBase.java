package org.drools.verifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatelessSession;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.misc.PackageDescrVisitor;
import org.drools.verifier.report.components.Cause;

/**
 * 
 * @author Toni Rikkola
 * 
 */
abstract public class TestBase extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        System.setProperty( "drools.dateformat",
                            "dd-MMM-yyyy" );
    }

    public StatelessSession getStatelessSession(InputStream stream) throws Exception {
        // read in the source
        Reader source = new InputStreamReader( stream );

        PackageBuilder builder = new PackageBuilder();

        builder.addPackageFromDrl( source );

        Package pkg = builder.getPackage();

        if ( builder.hasErrors() ) {
            for ( KnowledgeBuilderError error : builder.getErrors() ) {
                System.out.println( error.getMessage() );
            }
            fail( "Builder has errors" );
        }

        assertTrue( "Package was null.",
                    pkg != null );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        return ruleBase.newStatelessSession();
    }

    /**
     * Returns true if map contains redundancy where ruleName1 is redundant to
     * ruleName2.
     * 
     * @param map
     * @param ruleName1
     * @param ruleName2
     * @return True if redundancy exists.
     */
    protected static boolean mapContains(Map<String, Set<String>> map,
                                         String ruleName1,
                                         String ruleName2) {
        if ( map.containsKey( ruleName1 ) ) {
            Set<String> set = map.get( ruleName1 );
            boolean exists = set.remove( ruleName2 );

            // If set is empty remove key from map.
            if ( set.isEmpty() ) {
                map.remove( ruleName1 );
            }
            return exists;
        }
        return false;
    }

    /**
     * Returns true if map contains redundancy where cause1 is redundant to
     * cause2.
     * 
     * @param map
     * @param cause1
     * @param cause2
     * @return True if redundancy exists.
     */
    protected static boolean causeMapContains(Map<Cause, Set<Cause>> map,
                                              Cause cause1,
                                              Cause cause2) {
        if ( map.containsKey( cause1 ) ) {
            Set<Cause> set = map.get( cause1 );
            boolean exists = set.remove( cause2 );

            // If set is empty remove key from map.
            if ( set.isEmpty() ) {
                map.remove( cause1 );
            }
            return exists;
        }
        return false;
    }

    public Collection< ? extends Object> getTestData(InputStream stream,
                                                     VerifierData data) throws Exception {
        Reader drlReader = new InputStreamReader( stream );
        PackageDescr descr = new DrlParser().parse( drlReader );

        PackageDescrVisitor ruleFlattener = new PackageDescrVisitor();

        ruleFlattener.addPackageDescrToData( descr,
                                             Collections.EMPTY_LIST,
                                             data );

        // Rules with relations
        return data.getAll();
    }
}
