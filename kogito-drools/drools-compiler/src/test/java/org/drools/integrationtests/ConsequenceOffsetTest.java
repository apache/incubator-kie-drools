package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.DRLLexer;
import org.drools.lang.DRLParser;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;

public class ConsequenceOffsetTest extends TestCase {
    
    public void testConsequenceOffset() throws Exception {
        int offset = -1;
        Reader reader = new InputStreamReader( ConsequenceOffsetTest.class.getResourceAsStream( "test_consequenceOffset.drl" ) );
        DRLParser parser = new DRLParser( new CommonTokenStream( new DRLLexer( new ANTLRReaderStream( reader ) ) ) );
        parser.compilation_unit();
        PackageDescr packageDescr = parser.getPackageDescr();
        PackageBuilder packageBuilder = new PackageBuilder();
        packageBuilder.addPackage(packageDescr);
        assertEquals(false, packageBuilder.hasErrors());
        for (Object o: packageDescr.getRules()) {
            RuleDescr rule = (RuleDescr) o;
            if (rule.getName().equals("test")) {
                offset = rule.getConsequenceOffset();
            }
        }
        
        reader = new InputStreamReader( ConsequenceOffsetTest.class.getResourceAsStream( "test_consequenceOffset2.drl" ) );
        parser = new DRLParser( new CommonTokenStream( new DRLLexer( new ANTLRReaderStream( reader ) ) ) );
        parser.compilation_unit();
        packageDescr = parser.getPackageDescr();
        packageBuilder = new PackageBuilder();
        packageBuilder.addPackage(packageDescr);
        reader = new InputStreamReader( ConsequenceOffsetTest.class.getResourceAsStream( "test_consequenceOffset.drl" ) );
        parser = new DRLParser( new CommonTokenStream( new DRLLexer( new ANTLRReaderStream( reader ) ) ) );
        parser.compilation_unit();
        packageDescr = parser.getPackageDescr();
        packageBuilder.addPackage(packageDescr);
        assertEquals(false, packageBuilder.hasErrors());
        for (Object o: packageDescr.getRules()) {
            RuleDescr rule = (RuleDescr) o;
            if (rule.getName().equals("test")) {
                assertEquals(offset, rule.getConsequenceOffset());
                return;
            }
        }
        fail();
    }
    
    public void testLargeSetOfImports() throws Exception {
        Reader reader = new InputStreamReader( ConsequenceOffsetTest.class.getResourceAsStream( "test_consequenceOffsetImports.drl" ) );
        DRLParser parser = new DRLParser( new CommonTokenStream( new DRLLexer( new ANTLRReaderStream( reader ) ) ) );
        parser.compilation_unit();
        PackageDescr packageDescr = parser.getPackageDescr();
        PackageBuilder packageBuilder = new PackageBuilder();
        packageBuilder.addPackage(packageDescr);
        assertEquals(false, packageBuilder.hasErrors());
    }
    
}
