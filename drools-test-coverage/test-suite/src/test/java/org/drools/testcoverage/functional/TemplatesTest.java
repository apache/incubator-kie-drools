/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.functional;

import org.assertj.core.api.Assertions;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.drools.template.DataProviderCompiler;
import org.drools.template.ObjectDataCompiler;
import org.drools.template.objects.ArrayDataProvider;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Tests templates - providers, generating rules, performance.
 */
public class TemplatesTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplatesTest.class);
    private static final StringBuffer EXPECTED_RULES = new StringBuffer();

    static {
        final String head = "package " + TestConstants.PACKAGE_FUNCTIONAL + ";\n"
                + "import  " + TemplatesTest.class.getCanonicalName() + ".Vegetable;\n"
                + "import  " + TemplatesTest.class.getCanonicalName() + ".Taste;\n"
                + "global java.util.List list;\n\n";
        final String rule2_when = "rule \"is appropriate 2\"\n"
                + "\twhen\n"
                + "\t\tVegetable( $name : name == \"carrot\", $field : weight >= 0 && <= 1000, $price : price <= 2, "
                + "$taste : taste  == Taste.HORRIBLE )\n";
        final String rule2_then = "\tthen\n\t\tlist.add( $name );\nend\n\n";

        final String rule1_when = "rule \"is appropriate 1\"\n"
                + "\twhen\n"
                + "\t\tVegetable( $name : name == \"cucumber\", $field : length >= 20 && <= 40, $price : price <= 15, "
                + "$taste : taste  == Taste.EXCELENT )\n";
        final String rule1_then = "\tthen\n\t\tlist.add( $name );\nend\n\n";

        final String rule0_when = "rule \"is appropriate 0\"\n"
                + "\twhen\n"
                + "\t\tVegetable( $name : name == \"tomato\", $field : weight >= 200 && <= 1000, $price : price <= 6, "
                + "$taste : taste  == Taste.GOOD || == Taste.EXCELENT )\n";
        final String rule0_then = "\tthen\n\t\tlist.add( $name );\nend\n\n";

        EXPECTED_RULES.append(head);
        EXPECTED_RULES.append(rule2_when).append(rule2_then);
        EXPECTED_RULES.append(rule1_when).append(rule1_then);
        EXPECTED_RULES.append(rule0_when).append(rule0_then);
    }

    @Test
    public void loadingFromDLRObjsCorrectnessCheck() throws IOException {
        final KieServices kieServices = KieServices.Factory.get();
        final Collection<ParamSet> cfl = new ArrayList<ParamSet>();
        cfl.add(new ParamSet("tomato", "weight", 200, 1000, 6, EnumSet.of(Taste.GOOD, Taste.EXCELENT)));
        cfl.add(new ParamSet("cucumber", "length", 20, 40, 15, EnumSet.of(Taste.EXCELENT)));
        cfl.add(new ParamSet("carrot", "weight", 0, 1000, 2, EnumSet.of(Taste.HORRIBLE)));

        final ObjectDataCompiler converter = new ObjectDataCompiler();
        final String drl = converter.compile(cfl, kieServices.getResources().newClassPathResource("template_1.drl", getClass())
                .getInputStream());

        // prints rules generated from template
        LOGGER.debug(drl);

        assertEqualsIgnoreWhitespace(EXPECTED_RULES.toString(), drl);

        testCorrectnessCheck(drl);
    }

    @Test
    public void loadingFromDLRMapsCorrectnessCheck() throws IOException {
        final KieServices kieServices = KieServices.Factory.get();

        final ObjectDataCompiler converter = new ObjectDataCompiler();
        final String drl = converter.compile(
                getMapsParam(),
                kieServices.getResources().newClassPathResource("template_1.drl", getClass()).getInputStream());

        // prints rules generated from template
        LOGGER.debug(drl);

        assertEqualsIgnoreWhitespace(EXPECTED_RULES.toString(), drl);

        testCorrectnessCheck(drl);
    }

    @Test
    public void loadingFromDLRArrayCorrectnessCheck() throws Exception {
        final String[][] rows = new String[3][6];
        rows[0] = new String[] { "tomato", "weight", "200", "1000", "6", "== Taste.GOOD || == Taste.EXCELENT" };
        rows[1] = new String[] { "cucumber", "length", "20", "40", "15", "== Taste.EXCELENT" };
        rows[2] = new String[] { "carrot", "weight", "0", "1000", "2", "== Taste.HORRIBLE" };

        final ArrayDataProvider adp = new ArrayDataProvider(rows);

        final DataProviderCompiler converter = new DataProviderCompiler();
        final String drl = converter.compile(
                adp,
                KieServices.Factory.get().getResources().newClassPathResource("template_1.drl", getClass()).getInputStream());

        // prints rules generated from template
        LOGGER.debug(drl);

        assertEqualsIgnoreWhitespace(EXPECTED_RULES.toString(), drl);

        testCorrectnessCheck(drl);
    }

    @Test
    public void loadingFromDLRSpreadsheetCorrectnessCheck() throws Exception {
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();

        final KieServices kieServices = KieServices.Factory.get();
        // the data we are interested in starts at row 1, column 1 (e.g. A1)
        final String drl = converter.compile(
                kieServices.getResources().newClassPathResource("template1_spreadsheet.xls", getClass()).getInputStream(),
                kieServices.getResources().newClassPathResource("template_1.drl", getClass()).getInputStream(), 1, 1);

        // prints rules generated from template
        LOGGER.debug(drl);

        assertEqualsIgnoreWhitespace(EXPECTED_RULES.toString(), drl);

        testCorrectnessCheck(drl);
    }

    @Test
    public void OneRuleManyRows() throws IOException {
        final KieServices kieServices = KieServices.Factory.get();
        final Collection<ParamSet> cfl = new ArrayList<ParamSet>();
        cfl.add(new ParamSet("tomato", "weight", 200, 1000, 6, EnumSet.of(Taste.GOOD, Taste.EXCELENT)));

        final ObjectDataCompiler converter = new ObjectDataCompiler();
        final String drl = converter.compile(
                cfl,
                kieServices.getResources().newClassPathResource("template_1.drl", getClass()).getInputStream());

        // prints rules generated from template
        LOGGER.debug(drl);

        testManyRows(drl, 0, 1);
    }

    @Test
    public void TenRulesManyRows() throws IOException {
        final KieServices kieServices = KieServices.Factory.get();
        final ObjectDataCompiler converter = new ObjectDataCompiler();
        final String drl = converter.compile(
                generateParamSetCollection(1),
                kieServices.getResources().newClassPathResource("template_1.drl", getClass()).getInputStream());

        // prints rules generated from template
        LOGGER.debug(drl);

        testManyRows(drl, 500, 10);
    }

    @Test
    public void OneTemplateManyRules() throws IOException {
        final KieServices kieServices = KieServices.Factory.get();
        final ObjectDataCompiler converter = new ObjectDataCompiler();
        final String drl = converter.compile(
                generateParamSetCollection(50),
                kieServices.getResources().newClassPathResource("template_1.drl", getClass())
                .getInputStream());

        // prints rules generated from template
        LOGGER.debug(drl);

        testManyRules(drl, 500);
    }

    @Test
    public void TenTemplatesManyRules() throws IOException {
        final KieServices kieServices = KieServices.Factory.get();
        final ObjectDataCompiler converter = new ObjectDataCompiler();
        final String drl = converter.compile(
                generateParamSetCollection(50),
                kieServices.getResources().newClassPathResource("template_2.drl", getClass()).getInputStream());

        // prints rules generated from template
        LOGGER.debug(drl);

        testManyRules(drl, 5000);
    }

    private void testCorrectnessCheck(final String drl) {
        final Resource drlResource = KieServices.Factory.get().getResources().newReaderResource(new StringReader(drl));
        drlResource.setTargetPath(TestConstants.DRL_TEST_TARGET_PATH);
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, drlResource);

        final KieSession session = kbase.newKieSession();

        final List<String> list = new ArrayList<String>();
        session.setGlobal("list", list);

        session.insert(new Vegetable("tomato", 350, 8, 3, Taste.GOOD));
        session.insert(new Vegetable("tomato", 150, 8, 3, Taste.BAD));
        session.insert(new Vegetable("tomato", 350, 8, 7, Taste.GOOD));
        session.insert(new Vegetable("tomato", 1000, 8, 6, Taste.EXCELENT));
        session.insert(new Vegetable("cucumber", 1500, 19, 5, Taste.EXCELENT));
        session.insert(new Vegetable("cucumber", 1500, 21, 5, Taste.EXCELENT));
        session.insert(new Vegetable("carrot", 1000, 8, 6, Taste.EXCELENT));
        session.insert(new Vegetable("carrot", 200, 8, 1, Taste.HORRIBLE));
        session.insert(new Vegetable("onion", 500, 7, 4, Taste.EXCELENT));

        session.fireAllRules();

        // check of size of satisfying items
        Assertions.assertThat(list.size()).isEqualTo(4);

        final Collection<KiePackage> pkgs = kbase.getKiePackages();
        Assertions.assertThat(pkgs.size()).isEqualTo(1);
        final KiePackage pkg = pkgs.iterator().next();

        // check of generated rules size from template
        Assertions.assertThat(pkg.getRules().size()).isEqualTo(3);
    }

    private void testManyRows(final String drl, final int expectedResultListSize, final int expectedRulesCount) {
        final Resource drlResource = KieServices.Factory.get().getResources().newReaderResource(new StringReader(drl));
        drlResource.setTargetPath(TestConstants.DRL_TEST_TARGET_PATH);
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, drlResource);

        final KieSession session = kbase.newKieSession();

        final List<String> list = new ArrayList<String>();
        session.setGlobal("list", list);

        for (int i = 0; i < 500; i++) {
            session.insert(new Vegetable("tomato", 350, 8, 3, Taste.BAD));
        }

        session.fireAllRules();

        // check of size of satisfying items
        Assertions.assertThat(list.size()).isEqualTo(expectedResultListSize);

        final Collection<KiePackage> pkgs = kbase.getKiePackages();
        Assertions.assertThat(pkgs.size()).isEqualTo(1);
        final KiePackage pkg = pkgs.iterator().next();

        // check of generated rules size from template
        Assertions.assertThat(pkg.getRules().size()).isEqualTo(expectedRulesCount);
    }

    private void testManyRules(final String drl, final int expectedRulesCount) {
        final Resource drlResource = KieServices.Factory.get().getResources().newReaderResource(new StringReader(drl));
        drlResource.setTargetPath(TestConstants.DRL_TEST_TARGET_PATH);
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, drlResource);

        Collection<KiePackage> pkgs = kbase.getKiePackages();
        Assertions.assertThat(pkgs.size()).isEqualTo(1);
        KiePackage pkg = pkgs.iterator().next();

        // check of generated rules size from template
        Assertions.assertThat(pkg.getRules().size()).isEqualTo(expectedRulesCount);
    }

    private Collection<Map<String, Object>> getMapsParam() {
        final Collection<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();

        final Map<String, Object> mapTomato = new HashMap<String, Object>();
        mapTomato.put("name", "tomato");
        mapTomato.put("field", "weight");
        mapTomato.put("fieldLower", 200);
        mapTomato.put("fieldUpper", 1000);
        mapTomato.put("price", 6);
        mapTomato.put("tastes", "== Taste.GOOD || == Taste.EXCELENT");
        maps.add(mapTomato);

        final Map<String, Object> mapCucumber = new HashMap<String, Object>();
        mapCucumber.put("name", "cucumber");
        mapCucumber.put("field", "length");
        mapCucumber.put("fieldLower", 20);
        mapCucumber.put("fieldUpper", 40);
        mapCucumber.put("price", 15);
        mapCucumber.put("tastes", "== Taste.EXCELENT");
        maps.add(mapCucumber);

        final Map<String, Object> mapCarrot = new HashMap<String, Object>();
        mapCarrot.put("name", "carrot");
        mapCarrot.put("field", "weight");
        mapCarrot.put("fieldLower", 0);
        mapCarrot.put("fieldUpper", 1000);
        mapCarrot.put("price", 2);
        mapCarrot.put("tastes", "== Taste.HORRIBLE");
        maps.add(mapCarrot);
        return maps;
    }

    private Collection<ParamSet> generateParamSetCollection(final int loops) {
        final Collection<ParamSet> result = new ArrayList<>();
        for (int i = 0; i < loops; i++) {
            result.add(new ParamSet("tomato", "weight", 200, (1000 + i), 6, EnumSet.of(Taste.GOOD, Taste.EXCELENT)));
            result.add(new ParamSet("tomato", "weight", 100, (500 + i), 6, EnumSet.of(Taste.BAD)));
            result.add(new ParamSet("tomato", "length", 5, (10 + i), 6, EnumSet.of(Taste.AVERAGE)));
            result.add(new ParamSet("tomato", "weight", 300, (4000 + i), 6, EnumSet.of(Taste.GOOD, Taste.EXCELENT)));
            result.add(new ParamSet("tomato", "weight", 200, (6000 + i), 6, EnumSet.of(Taste.EXCELENT)));
            result.add(new ParamSet("tomato", "length", 2, (4 + i), 6, EnumSet.of(Taste.AVERAGE, Taste.GOOD)));
            result.add(new ParamSet("tomato", "weight", 100, (1000 + i), 6, EnumSet.of(Taste.GOOD)));
            result.add(new ParamSet("tomato", "weight", 500, (1000 + i), 6, EnumSet.of(Taste.EXCELENT)));
            result.add(new ParamSet("tomato", "length", 4, (15 + i), 6, EnumSet.of(Taste.AVERAGE, Taste.EXCELENT)));
            result.add(new ParamSet("tomato", "weight", 200, (1000 + i), 6, EnumSet.of(Taste.GOOD, Taste.EXCELENT)));
        }
        return result;
    }

    public static class ParamSet {

        private String name;
        private String field;
        private int fieldLower;
        private int fieldUpper;
        private int price;

        private EnumSet<Taste> tasteSet;

        public ParamSet(String name, String field, int fieldLower, int fieldUpper, int price, EnumSet<Taste> tasteSet) {
            this.name = name;
            this.field = field;
            this.fieldLower = fieldLower;
            this.fieldUpper = fieldUpper;
            this.tasteSet = tasteSet;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public String getField() {
            return field;
        }

        public int getFieldLower() {
            return fieldLower;
        }

        public int getFieldUpper() {
            return fieldUpper;
        }

        public int getPrice() {
            return price;
        }

        public String getTastes() {
            StringBuilder sb = new StringBuilder();
            String conn = "";
            for (Taste t : tasteSet) {
                sb.append(conn).append(" == Taste.").append(t);
                conn = " ||";
            }
            return sb.toString();
        }

    }

    public class Vegetable {

        private String name;
        private int weight;
        private int length;
        private int price;
        private Taste taste;

        public Vegetable(String name, int weight, int length, int price, Taste taste) {
            this.name = name;
            this.weight = weight;
            this.length = length;
            this.taste = taste;
            this.price = price;
        }

        public String getName() {
            return this.name;
        }

        public int getWeight() {
            return this.weight;
        }

        public int getPrice() {
            return this.price;
        }

        public int getLength() {
            return this.length;
        }

        public Taste getTaste() {
            return this.taste;
        }
    }

    public enum Taste {
        HORRIBLE, BAD, AVERAGE, GOOD, EXCELENT;
    }

    private static void assertEqualsIgnoreWhitespace(final String expected, final String actual) {
        final String cleanExpected = expected.replaceAll("\\s+", "");
        final String cleanActual = actual.replaceAll("\\s+", "");
        // System.out.println(cleanExpected);
        // System.out.println(cleanActual);
        Assertions.assertThat(cleanExpected).isEqualTo(cleanActual);
    }

}
