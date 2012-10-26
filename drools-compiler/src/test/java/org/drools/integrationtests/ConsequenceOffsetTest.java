package org.drools.integrationtests;

import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ConsequenceOffsetTest {
    
    @Test
    public void testConsequenceOffset() throws Exception {
        int offset = -1;
        DrlParser parser = new DrlParser(5);
        Reader reader = new InputStreamReader( ConsequenceOffsetTest.class.getResourceAsStream( "test_consequenceOffset.drl" ) );
        
        PackageDescr packageDescr = parser.parse(reader);
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
        packageDescr = parser.parse(reader);
        packageBuilder = new PackageBuilder();
        packageBuilder.addPackage(packageDescr);
        reader = new InputStreamReader( ConsequenceOffsetTest.class.getResourceAsStream( "test_consequenceOffset.drl" ) );
        packageDescr = parser.parse(reader);
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
    
    @Test
    public void testLargeSetOfImports() throws Exception {
        Reader reader = new InputStreamReader( ConsequenceOffsetTest.class.getResourceAsStream( "test_consequenceOffsetImports.drl" ) );
        DrlParser parser = new DrlParser(5);
        PackageDescr packageDescr = parser.parse(reader);
        PackageBuilder packageBuilder = new PackageBuilder();
        packageBuilder.addPackage(packageDescr);
        assertEquals(false, packageBuilder.hasErrors());
    }
    
}
