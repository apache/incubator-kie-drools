/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.xml;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.dsl.DescrFactory;
import org.drools.drl.parser.DrlParser;
import org.drools.drl.parser.DroolsParserException;
import org.drools.mvel.DrlDumper;
import org.junit.jupiter.api.Test;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test the dump/convert format utilities.
 */

public class DumperTest {

    // Xml Dumper test

    @Test
    public void testRoundTripAccumulateXml() throws Exception {
        DumperTestHelper.XmlFile("test_ParseAccumulate.xml");
    }

    @Test
    public void testRoundTripCollectXml() throws Exception {
        DumperTestHelper.XmlFile("test_ParseCollect.xml");
    }

    @Test
    public void testRoundTripExistsXml() throws Exception {
        DumperTestHelper.XmlFile("test_ParseExists.xml");
    }

    @Test
    public void testRoundTripForallXml() throws Exception {
        DumperTestHelper.XmlFile("test_ParseForall.xml");
    }

    @Test
    public void testRoundTripFromXml() throws Exception {
        DumperTestHelper.XmlFile("test_ParseFrom.xml");
    }

    @Test
    public void testRoundTripComplexRuleXml() throws Exception {
        DumperTestHelper.XmlFile("test_RoundTrip.xml");
    }

    // Drl Dumper test

    @Test
    public void testRoundTripComplexRuleDrl() throws Exception {
        DumperTestHelper.DrlFile("test_RoundTrip.drl");
    }

    @Test
    public void testRoundTripCollectDrl() throws Exception {
        DumperTestHelper.DrlFile("test_Collect.drl");
    }

    @Test
    public void testRoundTripAccumulateDrl() throws Exception {
        DumperTestHelper.DrlFile("test_accumulateall.drl");
    }

    @Test
    public void testRoundTripExistsDrl() throws Exception {
        DumperTestHelper.DrlFile("test_exists.drl");
    }

    @Test
    public void testRoundTripForallDrl() throws Exception {
        DumperTestHelper.DrlFile("test_Forall.drl");
    }

    @Test
    public void testRoundTripFromDrl() throws Exception {
        DumperTestHelper.DrlFile("test_from.drl");
    }

    @Test
    public void testRoundTripSimpleRuleDrl() throws Exception {
        DumperTestHelper.DrlFile("test_simplerule.drl");
    }

    @Test
    public void testRoundTripPComplexDrl() throws Exception {
        DumperTestHelper.DrlFile("test_complex.drl");
    }

    @Test
    public void testRoundTripDRLAnnotations() throws Exception {
        DumperTestHelper.DrlFile("test_DumpAnnotations.drl");
    }

    @Test
    public void testRoundTripDRLNamedConsequences() throws Exception {
        DumperTestHelper.DrlFile("test_NamedConsequences.drl");
    }

    @Test
    public void testRoundTripPComplexXml() throws Exception {
        DumperTestHelper.XmlFile("test_ParseComplex.xml");
    }

    @Test
    public void testRoundTripTraitDeclarations() throws Exception {
        DumperTestHelper.DrlFile("test_TraitDeclaration.drl");

        String out = DumperTestHelper.dump("test_TraitDeclaration.drl");
        assertThat(out).contains("declare trait Foo");
    }

    @Test
    public void testRoundTripEnumDeclarations() throws Exception {
        DumperTestHelper.DrlFile("test_EnumDeclaration.drl");

        String out = DumperTestHelper.dump("test_EnumDeclaration.drl");
        assertThat(out).contains("declare enum Planets", "MERCURY", "7.1492e7");
    }

    @Test
    public void testRoundTripAccumulate() throws Exception {
        String out = DumperTestHelper.dump("test_Accumulate.drl");
        assertThat(out).contains("$sum : count( $s1 )", "count( $s2 )").doesNotContain("null : count( $s2 )");
    }

    private void checkRoundtrip(String drl) throws DroolsParserException {
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
        final PackageDescr pkgOriginal = parser.parse(false, drl);
        final DrlDumper dumper = new DrlDumper();
        String out = dumper.dump(pkgOriginal);
        assertThat(drl).isEqualToIgnoringWhitespace(out);
    }

    @Test
    public void testRoundTripDRLAccumulate() throws Exception {
        // RHDM-254
        String drl =
                "package org.test\n" +
                        "\n" +
                        "rule \"last flown date\"\n" +
                        "when\n" +
                        "    $customer : Profile( $ceid : id )\n" +
                        "    accumulate(\n" +
                        "    Flight( status == \"Flown\", $dptDate: departureDate.time ) from $customer.flights,\n" +
                        "        $cnt : count( $dptDate );\n" +
                        "        $cnt > 0 )\n" +
                        "then\n" +
                        "end";

        checkRoundtrip(drl);
    }

    @Test
    public void testRoundTripDRLAccumulateWith2Patterns() throws Exception {
        // DROOLS-5607
        String drl =
                "package org.example\n" +
                        "declare  Flight \n" +
                        "    pnrRecordLocator : String  \n" +
                        "    flightSegmentDepartureIataCode : String  \n" +
                        "    flightSegmentArrivalIataCode : String  \n" +
                        "    id : int  \n" +
                        "end\n" +
                        "\n" +
                        "rule \"round trips accumulate\"\n" +
                        "when\n" +
                        "  $roundTripSet : java.util.Set( size >= 1 ) from accumulate (\n" +
                        "    $f1:Flight()\n" +
                        "    and\n" +
                        "    $f2:Flight(\n" +
                        "        id > $f1.id,\n" +
                        "        pnrRecordLocator==$f1.pnrRecordLocator,\n" +
                        "        flightSegmentDepartureIataCode==$f1.flightSegmentArrivalIataCode,\n" +
                        "        flightSegmentArrivalIataCode==$f1.flightSegmentDepartureIataCode\n" +
                        "    )" +
                        "  , collectSet($f1.getId()) )\n" +
                        "then\n" +
                        "    System.out.println($roundTripSet);\n" +
                        "end";

        checkRoundtrip(drl);
    }

    @Test
    public void testAccumulateWithCustomImport() throws Exception {
        // DROOLS-5870
        String drl =
                "package org.example\n" +
                        "import org.drools.Adult\n" +
                        "import org.drools.Child\n" +
                        "import org.drools.Result\n" +
                        "import accumulate org.drools.TestFunction accfunc\n" +
                        "rule \"R\" when\n" +
                        "  accumulate( $c : Child( age < 10 ) and $a : Adult( name == $c.parent ), $parentAge : accfunc($a.getAge()) )\n" +
                        "then\n" +
                        "  insert(new Result($parentAge));\n" +
                        "end";

        checkRoundtrip(drl);
    }

    @Test
    public void testAccumulateWithoutConstraint() throws Exception {
        // DROOLS-5872

        String expectedDrl =
                "package example \n" +
                        "\n" +
                        "import java.math.BigDecimal\n" +
                        "\n" +
                        "rule \"Test Rule\"\n" +
                        "when\n" +
                        "    accumulate(     \n" +
                        "    $target : example.RuleTest.Fact(  )  ,\n" +
                        "        $cnt : count(  ) \n" +
                        "         ) \n" +
                        "then\n" +
                        "System.out.println($cnt);\n" +
                        "\n" +
                        "end";

        PackageDescr packageDescr = DescrFactory.newPackage().name("example")
                .newImport().target("java.math.BigDecimal").end()
                .newRule().name("Test Rule")
                .lhs()
                .accumulate()
                .source().pattern().type("example.RuleTest.Fact").id("$target", false)
                .end()
                .end()
                .function("count", "$cnt", false)
                .end()
                .end()
                .rhs("System.out.println($cnt);")
                .end()
                .end().getDescr();

        String drl = new DrlDumper().dump(packageDescr);

        assertThat(drl).isEqualToIgnoringWhitespace(expectedDrl);
    }
}
