package org.drools.compiler.xml.rules;

import junit.framework.TestCase;

/**
 * Test the dump/convert format utilities.
 * 
 * @author Michael Neale
 * @author Fernando Meyer
 */

public class DumperTest extends TestCase {

    // Xml Dumper test
    
    public void testRoundTripAccumulateXml() throws Exception {
        DumperTestHelper.XmlFile( "test_ParseAccumulate.xml" );
    }

    public void testRoundTripCollectXml() throws Exception {
        DumperTestHelper.XmlFile( "test_ParseCollect.xml" );
    }
    
    public void testRoundTripExistsXml() throws Exception {
        DumperTestHelper.XmlFile( "test_ParseExists.xml" );
    }

    public void testRoundTripForallXml() throws Exception {
        DumperTestHelper.XmlFile( "test_ParseForall.xml" );
    }

    public void testRoundTripFromXml() throws Exception {
        DumperTestHelper.XmlFile( "test_ParseFrom.xml" );
    }

    public void testRoundTripComplexRuleXml() throws Exception {
        DumperTestHelper.XmlFile( "test_RoundTrip.xml" );
    }
    
    // Drl Dumper test

    public void testRoundTripComplexRuleDrl() throws Exception {
        DumperTestHelper.DrlFile( "test_RoundTrip.drl" );
    }
    
    public void testRoundTripCollectDrl() throws Exception {
        DumperTestHelper.DrlFile( "../../integrationtests/test_Collect.drl" );
    }
    
    public void testRoundTripAccumulateDrl() throws Exception {
        DumperTestHelper.DrlFile( "test_accumulateall.drl" );
    }
    
    public void testRoundTripExistsDrl() throws Exception {
        DumperTestHelper.DrlFile( "../../integrationtests/test_exists.drl" );
    }

    public void testRoundTripForallDrl() throws Exception {
        DumperTestHelper.DrlFile( "../../integrationtests/test_Forall.drl" );
    }

    public void testRoundTripFromDrl() throws Exception {
        DumperTestHelper.DrlFile( "test_from.drl" );
    }

    public void testRoundTripSimpleRuleDrl() throws Exception {
        DumperTestHelper.DrlFile( "test_simplerule.drl" );
    }

    public void testRoundTripPComplexDrl() throws Exception {
        DumperTestHelper.DrlFile( "test_complex.drl" );
    }
    
    public void testRoundTripPComplexXml() throws Exception {
        DumperTestHelper.XmlFile( "test_ParseComplex.xml" );
    }
    
    public static void testStaticMethod1() {
        System.out.println( "testStaticMethod1" ) ;
    }
    
    public static void testStaticMethod2() {
        System.out.println( "testStaticMethod2" ) ;
    }
    
    public static void testStaticMethod3() {
        System.out.println( "testStaticMethod3" ) ;
    }    
    
}