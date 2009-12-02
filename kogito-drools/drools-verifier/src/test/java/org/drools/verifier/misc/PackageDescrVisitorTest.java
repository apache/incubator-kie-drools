package org.drools.verifier.misc;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;

import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.descr.PackageDescr;
import org.drools.verifier.Verifier;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.data.VerifierReportFactory;

import junit.framework.TestCase;

public class PackageDescrVisitorTest extends TestCase {

    public void testVisit() throws DroolsParserException,
                           UnknownDescriptionException {
        PackageDescrVisitor visitor = new PackageDescrVisitor();
        VerifierData data = VerifierReportFactory.newVerifierData();

        assertNotNull( data );

        Reader drlReader = new InputStreamReader( Verifier.class.getResourceAsStream( "Misc3.drl" ) );
        PackageDescr packageDescr = new DrlParser().parse( drlReader );

        assertNotNull( packageDescr );

        visitor.addPackageDescrToData( packageDescr,
                                       data );

        Collection<VerifierComponent> all = data.getAll();

        assertNotNull( all );
        assertEquals( 50,
                      all.size() );

        //        for ( VerifierComponent verifierComponent : all ) {
        //            System.out.println( verifierComponent );
        //        }
    }

    public void testSubPatterns() throws DroolsParserException,
                                 UnknownDescriptionException {
        PackageDescrVisitor visitor = new PackageDescrVisitor();
        VerifierData data = VerifierReportFactory.newVerifierData();

        assertNotNull( data );

        Reader drlReader = new InputStreamReader( getClass().getResourceAsStream( "SubPattern.drl" ) );
        PackageDescr packageDescr = new DrlParser().parse( drlReader );

        assertNotNull( packageDescr );

        visitor.addPackageDescrToData( packageDescr,
                                       data );

        Collection<VerifierComponent> all = data.getAll();

        assertNotNull( all );

        SubPattern test1SubPattern = null;
        SubPattern test2SubPattern = null;
        SubRule test1SubRule = null;
        SubRule test2SubRule = null;

        for ( VerifierComponent verifierComponent : all ) {
            //            System.out.println( verifierComponent );

            if ( verifierComponent.getVerifierComponentType().equals( VerifierComponentType.SUB_PATTERN ) ) {
                SubPattern subPattern = (SubPattern) verifierComponent;
                if ( "Test 1".equals( subPattern.getRuleName() ) ) {
                    assertNull( test1SubPattern );
                    test1SubPattern = subPattern;
                } else if ( "Test 2".equals( subPattern.getRuleName() ) ) {
                    assertNull( test2SubPattern );
                    test2SubPattern = subPattern;
                }
            }
            if ( verifierComponent.getVerifierComponentType().equals( VerifierComponentType.SUB_RULE ) ) {
                SubRule subRule = (SubRule) verifierComponent;
                if ( "Test 1".equals( subRule.getRuleName() ) ) {
                    assertNull( test1SubRule );
                    test1SubRule = subRule;
                } else if ( "Test 2".equals( subRule.getRuleName() ) ) {
                    assertNull( test2SubRule );
                    test2SubRule = subRule;
                }
            }
        }

        assertNotNull( test1SubPattern );
        assertEquals( 3,
                      test1SubPattern.getItems().size() );
        assertNotNull( test2SubPattern );
        assertEquals( 3,
                      test2SubPattern.getItems().size() );
        assertNotNull( test1SubRule );
        assertEquals( 1,
                      test1SubRule.getItems().size() );
        assertNotNull( test2SubRule );
        assertEquals( 1,
                      test2SubRule.getItems().size() );

    }
}
