package org.drools.compiler.compiler.xml.rules;

import org.drools.compiler.lang.DrlDumper;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test the dump/convert format utilities.
 */

public class DumperTest {

    // Xml Dumper test

    @Test
    public void testRoundTripAccumulateXml() throws Exception {
        DumperTestHelper.XmlFile( "test_ParseAccumulate.xml" );
    }

    @Test
    public void testRoundTripCollectXml() throws Exception {
        DumperTestHelper.XmlFile( "test_ParseCollect.xml" );
    }

    @Test
    public void testRoundTripExistsXml() throws Exception {
        DumperTestHelper.XmlFile( "test_ParseExists.xml" );
    }

    @Test
    public void testRoundTripForallXml() throws Exception {
        DumperTestHelper.XmlFile( "test_ParseForall.xml" );
    }

    @Test
    public void testRoundTripFromXml() throws Exception {
        DumperTestHelper.XmlFile( "test_ParseFrom.xml" );
    }

    @Test
    public void testRoundTripComplexRuleXml() throws Exception {
        DumperTestHelper.XmlFile( "test_RoundTrip.xml" );
    }

    // Drl Dumper test

    @Test
    public void testRoundTripComplexRuleDrl() throws Exception {
        DumperTestHelper.DrlFile( "test_RoundTrip.drl" );
    }

    @Test
    public void testRoundTripCollectDrl() throws Exception {
        DumperTestHelper.DrlFile( "../../../integrationtests/test_Collect.drl" );
    }

    @Test
    public void testRoundTripAccumulateDrl() throws Exception {
        DumperTestHelper.DrlFile( "test_accumulateall.drl" );
    }

    @Test
    public void testRoundTripExistsDrl() throws Exception {
        DumperTestHelper.DrlFile( "../../../integrationtests/test_exists.drl" );
    }

    @Test
    public void testRoundTripForallDrl() throws Exception {
        DumperTestHelper.DrlFile( "../../../integrationtests/test_Forall.drl" );
    }

    @Test
    public void testRoundTripFromDrl() throws Exception {
        DumperTestHelper.DrlFile( "test_from.drl" );
    }

    @Test
    public void testRoundTripSimpleRuleDrl() throws Exception {
        DumperTestHelper.DrlFile( "test_simplerule.drl" );
    }

    @Test
    public void testRoundTripPComplexDrl() throws Exception {
        DumperTestHelper.DrlFile( "test_complex.drl" );
    }

    @Test
    public void testRoundTripDRLAnnotations() throws Exception {
        DumperTestHelper.DrlFile( "test_DumpAnnotations.drl" );
    }

    @Test
    public void testRoundTripDRLNamedConsequences() throws Exception {
        DumperTestHelper.DrlFile( "test_NamedConsequences.drl" );
    }

    @Test
    public void testRoundTripPComplexXml() throws Exception {
        DumperTestHelper.XmlFile( "test_ParseComplex.xml" );
    }

    @Test
    public void testRoundTripTraitDeclarations() throws Exception {
        DumperTestHelper.DrlFile( "test_TraitDeclaration.drl" );

        String out = DumperTestHelper.dump( "test_TraitDeclaration.drl" );
        assertTrue( out.contains( "declare trait Foo" ) );
    }

}
