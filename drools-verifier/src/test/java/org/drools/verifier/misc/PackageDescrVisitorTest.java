package org.drools.verifier.misc;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;

import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.descr.PackageDescr;
import org.drools.verifier.Verifier;
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
        assertEquals( 49,
                      all.size() );

//        for ( VerifierComponent verifierComponent : all ) {
//            System.out.println( verifierComponent );
//        }
    }

}
