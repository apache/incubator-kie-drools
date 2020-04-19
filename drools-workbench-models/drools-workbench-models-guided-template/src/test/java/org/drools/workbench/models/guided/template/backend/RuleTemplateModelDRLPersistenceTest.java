/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.guided.template.backend;

import org.assertj.core.api.Assertions;
import org.drools.workbench.models.commons.backend.rule.RuleModelPersistence;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.ActionUpdateField;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.junit.Before;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.DataType;

import static org.junit.Assert.assertNotNull;

public class RuleTemplateModelDRLPersistenceTest {

    private RuleModelPersistence ruleModelPersistence;

    @Before
    public void setUp() throws Exception {
        ruleModelPersistence = RuleTemplateModelDRLPersistenceImpl.getInstance();
    }

    private void checkMarshall(String expected,
                               RuleModel m) {
        String drl = ruleModelPersistence.marshal(m);
        assertNotNull(drl);
        if (expected != null) {
            assertEqualsIgnoreWhitespace(expected, drl);
        }
    }

    @Test
    public void testInWithIntegerValues() {
        final TemplateModel m = new TemplateModel();
        m.name = "t1";

        final FactPattern p = new FactPattern("Person");
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        con.setFieldName("field1");
        con.setOperator("in");
        con.setValue("$f1");
        con.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p.addConstraint(con);

        m.addLhsItem(p);

        m.addRow(new String[]{"1,2"});
        m.addRow(new String[]{"\"3\",\"4\""});
        m.addRow(new String[]{"(5,6)"});

        final String expected =
                "rule \"t1_0\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "  Person( field1 in (1, 2) )" +
                "then \n" +
                "end" +
                "rule \"t1_1\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "  Person( field1 in (3, 4) )" +
                "then \n" +
                "end" +
                "rule \"t1_2\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "  Person( field1 in (5, 6) )" +
                "then \n" +
                "end";

        checkMarshall(expected, m);
    }

    @Test
    public void testInWithSimpleSingleLiteralValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType(DataType.TYPE_STRING);
        con.setFieldName("field1");
        con.setOperator("in");
        con.setValue("$f1");
        con.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p.addConstraint(con);

        m.addLhsItem(p);

        m.addRow(new String[]{"ak1,mk1"});
        m.addRow(new String[]{"(ak2,mk2)"});
        m.addRow(new String[]{"( ak3, mk3 )"});
        m.addRow(new String[]{"( \"ak4\", \"mk4\" )"});
        m.addRow(new String[]{"( \"ak5 \", \" mk5\" )"});

        String expected =
                "rule \"t1_0\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "  Person( field1 in (\"ak1\",\"mk1\") )" +
                "then \n" +
                "end" +
                "rule \"t1_1\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "  Person( field1 in (\"ak2\",\"mk2\") )" +
                "then \n" +
                "end" +
                "rule \"t1_2\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "  Person( field1 in (\"ak3\",\"mk3\") )" +
                "then \n" +
                "end" +
                "rule \"t1_3\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "  Person( field1 in (\"ak4\",\"mk4\") )" +
                "then \n" +
                "end" +
                "rule \"t1_4\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "  Person( field1 in (\"ak5 \",\" mk5\") )" +
                "then \n" +
                "end";

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testOnlyRemoveSurroundingBrackets() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType(DataType.TYPE_STRING);
        con.setFieldName("field1");
        con.setOperator("in");
        con.setValue("$f1");
        con.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p.addConstraint(con);

        m.addLhsItem(p);

        m.addRow(new String[]{"\"John\", \"John, the John\", \"John (jr)\""});

        String expected = "rule \"t1_0\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "  Person( field1 in (\"John\", \"John, the John\", \" John (jr)\") )" +
                "then \n" +
                "end";

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testSimpleSingleValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType(DataType.TYPE_STRING);
        con.setFieldName("field1");
        con.setOperator("==");
        con.setValue("$f1");
        con.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p.addConstraint(con);

        m.addLhsItem(p);

        m.addRow(new String[]{"foo"});

        String expected = "rule \"t1_0\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "Person( field1 == \"foo\" )" +
                "then \n" +
                "end";

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testAppendAndInsert() {
        final TemplateModel m = new TemplateModel();
        m.name = "t1";

        final FactPattern p = new FactPattern("Person");
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType(DataType.TYPE_STRING);
        con.setFieldName("field1");
        con.setOperator("==");
        con.setValue("$f1");
        con.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p.addConstraint(con);

        m.addLhsItem(p);

        m.addRow(new String[]{"foo1"});
        m.addRow(new String[]{"foo2"});
        m.addRow(1, new String[]{"foo3"});

        final String expected =
                "rule \"t1_0\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "  Person( field1 == \"foo1\" )" +
                "then \n" +
                "end\n" +
                "rule \"t1_1\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "  Person( field1 == \"foo3\" )" +
                "then \n" +
                "end" +
                "rule \"t1_2\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "  Person( field1 == \"foo2\" )" +
                "then \n" +
                "end";

        checkMarshall(expected, m);
    }

    @Test
    public void testRHSInsert() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";
        FactPattern p = new FactPattern("Person");
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType(DataType.TYPE_STRING);
        con.setFieldName("field1");
        con.setOperator("==");
        con.setValue("$f1");
        con.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p.addConstraint(con);

        m.addLhsItem(p);

        ActionInsertFact actionInsertFact = new ActionInsertFact();
        actionInsertFact.setFactType("Applicant");
        ActionFieldValue actionFieldValue = new ActionFieldValue("age", "123", "Integer");
        actionFieldValue.setNature(SingleFieldConstraint.TYPE_LITERAL);
        actionInsertFact.addFieldValue(actionFieldValue);

        m.addRhsItem(actionInsertFact);

        m.addRow(new String[]{"foo"});

        String expected = "rule \"t1_0\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "Person( field1 == \"foo\" )\n" +
                "then \n" +
                "  Applicant fact0 = new Applicant(); \n" +
                "  fact0.setAge(123); \n" +
                "  insert(fact0); \n" +
                "end";

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testSimpleSingleTemplateValueSingleLiteralValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType(DataType.TYPE_STRING);
        con.setFieldName("field1");
        con.setOperator("==");
        con.setValue("$f1");
        con.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p.addConstraint(con);

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con.setFieldType(DataType.TYPE_STRING);
        con2.setFieldName("field2");
        con2.setOperator("==");
        con2.setValue("bar");
        con2.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        p.addConstraint(con2);

        m.addLhsItem(p);

        m.addRow(new String[]{"foo"});

        String expected = "rule \"t1_0\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "Person( field1 == \"foo\", field2 == \"bar\" )" +
                "then \n" +
                "end";

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testSimpleBothValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType(DataType.TYPE_STRING);
        con.setFieldName("field1");
        con.setOperator("==");
        con.setValue("$f1");
        con.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p.addConstraint(con);

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con.setFieldType(DataType.TYPE_STRING);
        con2.setFieldName("field2");
        con2.setOperator("==");
        con2.setValue("$f2");
        con2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p.addConstraint(con2);

        m.addLhsItem(p);

        m.addRow(new String[]{"foo", "bar"});

        String expected = "rule \"t1_0\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "Person( field1 == \"foo\", field2 == \"bar\" )" +
                "then \n" +
                "end";

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testSimpleFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType(DataType.TYPE_STRING);
        con.setFieldName("field1");
        con.setOperator("==");
        con.setValue("$f1");
        con.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p.addConstraint(con);

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con.setFieldType(DataType.TYPE_STRING);
        con2.setFieldName("field2");
        con2.setOperator("==");
        con2.setValue("$f2");
        con2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p.addConstraint(con2);

        m.addLhsItem(p);

        m.addRow(new String[]{"foo", null});

        String expected = "rule \"t1_0\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "Person( field1 == \"foo\" )" +
                "then \n" +
                "end";

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testSimpleSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType(DataType.TYPE_STRING);
        con.setFieldName("field1");
        con.setOperator("==");
        con.setValue("$f1");
        con.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p.addConstraint(con);

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con.setFieldType(DataType.TYPE_STRING);
        con2.setFieldName("field2");
        con2.setOperator("==");
        con2.setValue("$f2");
        con2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p.addConstraint(con2);

        m.addLhsItem(p);

        m.addRow(new String[]{null, "bar"});

        String expected = "rule \"t1_0\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "Person( field2 == \"bar\" )" +
                "then \n" +
                "end";

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testGeneratorFactoryReuse() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType(DataType.TYPE_STRING);
        con.setFieldName("field1");
        con.setOperator("==");
        con.setValue("$f1");
        con.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p.addConstraint(con);

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con.setFieldType(DataType.TYPE_STRING);
        con2.setFieldName("field2");
        con2.setOperator("==");
        con2.setValue("$f2");
        con2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p.addConstraint(con2);

        m.addLhsItem(p);

        m.addRow(new String[]{"foo", "bar"});

        String expected = "rule \"t1_0\"" +
                "dialect \"mvel\"\n" +
                "when \n" +
                "Person( field1 == \"foo\", field2 == \"bar\" )" +
                "then \n" +
                "end";

        checkMarshall(expected,
                      m);

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testCompositeConstraintsBothValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern("Person");
        m.addLhsItem(p1);

        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p1.addConstraint(comp);

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName("field1");
        X.setFieldType(DataType.TYPE_STRING);
        X.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        X.setValue("$f1");
        X.setOperator("==");
        comp.addConstraint(X);

        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName("field2");
        Y.setFieldType(DataType.TYPE_STRING);
        Y.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        Y.setValue("$f2");
        Y.setOperator("==");
        comp.addConstraint(Y);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" || field2 == \"bar\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testCompositeConstraintsFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern("Person");
        m.addLhsItem(p1);

        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p1.addConstraint(comp);

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName("field1");
        X.setFieldType(DataType.TYPE_STRING);
        X.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        X.setValue("$f1");
        X.setOperator("==");
        comp.addConstraint(X);

        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName("field2");
        Y.setFieldType(DataType.TYPE_STRING);
        Y.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        Y.setValue("$f2");
        Y.setOperator("==");
        comp.addConstraint(Y);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testCompositeConstraintsSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern("Person");
        m.addLhsItem(p1);

        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p1.addConstraint(comp);

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName("field1");
        X.setFieldType(DataType.TYPE_STRING);
        X.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        X.setValue("$f1");
        X.setOperator("==");
        comp.addConstraint(X);

        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName("field2");
        Y.setFieldType(DataType.TYPE_STRING);
        Y.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        Y.setValue("$f2");
        Y.setOperator("==");
        comp.addConstraint(Y);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field2 == \"bar\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{null, "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testCompositeConstraintWithConnectiveConstraintBothValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern("Person");
        m.addLhsItem(p1);

        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p1.addConstraint(comp);

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName("field1");
        X.setFieldType(DataType.TYPE_STRING);
        X.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        X.setValue("$f1");
        X.setOperator("==");
        comp.addConstraint(X);

        ConnectiveConstraint connective = new ConnectiveConstraint();
        connective.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        connective.setFieldType(DataType.TYPE_STRING);
        connective.setOperator("|| ==");
        connective.setValue("goo");

        X.setConnectives(new ConnectiveConstraint[1]);
        X.getConnectives()[0] = connective;

        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName("field2");
        Y.setFieldType(DataType.TYPE_STRING);
        Y.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        Y.setValue("$f2");
        Y.setOperator("==");
        comp.addConstraint(Y);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" || == \"goo\" || field2 == \"bar\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testCompositeConstraintWithConnectiveConstraintFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern("Person");
        m.addLhsItem(p1);

        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p1.addConstraint(comp);

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName("field1");
        X.setFieldType(DataType.TYPE_STRING);
        X.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        X.setValue("$f1");
        X.setOperator("==");
        comp.addConstraint(X);

        ConnectiveConstraint connective = new ConnectiveConstraint();
        connective.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        connective.setFieldType(DataType.TYPE_STRING);
        connective.setOperator("|| ==");
        connective.setValue("goo");

        X.setConnectives(new ConnectiveConstraint[1]);
        X.getConnectives()[0] = connective;

        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName("field2");
        Y.setFieldType(DataType.TYPE_STRING);
        Y.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        Y.setValue("$f2");
        Y.setOperator("==");
        comp.addConstraint(Y);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" || == \"goo\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testCompositeConstraintWithConnectiveConstraintSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern("Person");
        m.addLhsItem(p1);

        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p1.addConstraint(comp);

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName("field1");
        X.setFieldType(DataType.TYPE_STRING);
        X.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        X.setValue("$f1");
        X.setOperator("==");
        comp.addConstraint(X);

        ConnectiveConstraint connective = new ConnectiveConstraint();
        connective.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        connective.setFieldType(DataType.TYPE_STRING);
        connective.setOperator("|| ==");
        connective.setValue("goo");

        X.setConnectives(new ConnectiveConstraint[1]);
        X.getConnectives()[0] = connective;

        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName("field2");
        Y.setFieldType(DataType.TYPE_STRING);
        Y.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        Y.setValue("$f2");
        Y.setOperator("==");
        comp.addConstraint(Y);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field2 == \"bar\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{null, "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testCompositeFactPatternBothValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        CompositeFactPattern cp = new CompositeFactPattern(CompositeFactPattern.COMPOSITE_TYPE_OR);
        FactPattern p1 = new FactPattern("Person");
        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");
        sfc1.setOperator("==");
        p1.addConstraint(sfc1);

        FactPattern p2 = new FactPattern("Person");
        SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setFieldName("field2");
        sfc2.setFieldType(DataType.TYPE_STRING);
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setValue("$f2");
        sfc2.setOperator("==");
        p2.addConstraint(sfc2);

        cp.addFactPattern(p1);
        cp.addFactPattern(p2);

        m.addLhsItem(cp);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "(\n" +
                "Person( field1 == \"foo\" )\n" +
                "or\n" +
                "Person( field2 == \"bar\" )\n" +
                ")\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testCompositeFactPatternFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        CompositeFactPattern cp = new CompositeFactPattern(CompositeFactPattern.COMPOSITE_TYPE_OR);
        FactPattern p1 = new FactPattern("Person");
        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");
        sfc1.setOperator("==");
        p1.addConstraint(sfc1);

        FactPattern p2 = new FactPattern("Person");
        SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setFieldName("field2");
        sfc2.setFieldType(DataType.TYPE_STRING);
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setValue("$f2");
        sfc2.setOperator("==");
        p2.addConstraint(sfc2);

        cp.addFactPattern(p1);
        cp.addFactPattern(p2);

        m.addLhsItem(cp);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "(\n" +
                "Person( field1 == \"foo\" )\n" +
                "or\n" +
                "Person( )\n" +
                ")\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testCompositeFactPatternSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        CompositeFactPattern cp = new CompositeFactPattern(CompositeFactPattern.COMPOSITE_TYPE_OR);
        FactPattern p1 = new FactPattern("Person");
        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");
        sfc1.setOperator("==");
        p1.addConstraint(sfc1);

        FactPattern p2 = new FactPattern("Person");
        SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setFieldName("field2");
        sfc2.setFieldType(DataType.TYPE_STRING);
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setValue("$f2");
        sfc2.setOperator("==");
        p2.addConstraint(sfc2);

        cp.addFactPattern(p1);
        cp.addFactPattern(p2);

        m.addLhsItem(cp);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "(\n" +
                "Person( )\n" +
                "or\n" +
                "Person( field2 == \"bar\" )\n" +
                ")\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{null, "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraintsAllValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(comp);

        m.addLhsItem(p);

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");
        sfc1.setOperator("==");
        comp.addConstraint(sfc1);

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_AND);
        final SingleFieldConstraint comp2sfc1 = new SingleFieldConstraint();
        comp2sfc1.setFieldType(DataType.TYPE_STRING);
        comp2sfc1.setFieldName("field2");
        comp2sfc1.setOperator("==");
        comp2sfc1.setValue("$f2");
        comp2sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        comp2.addConstraint(comp2sfc1);

        final SingleFieldConstraint comp2sfc2 = new SingleFieldConstraint();
        comp2sfc2.setFieldType(DataType.TYPE_STRING);
        comp2sfc2.setFieldName("field3");
        comp2sfc2.setOperator("==");
        comp2sfc2.setValue("$f3");
        comp2sfc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        comp2.addConstraint(comp2sfc2);

        comp.addConstraint(comp2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" || ( field2 == \"bar\" && field3 == \"goo\" ) )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", "bar", "goo"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraintsFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(comp);

        m.addLhsItem(p);

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");
        sfc1.setOperator("==");
        comp.addConstraint(sfc1);

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_AND);
        final SingleFieldConstraint comp2sfc1 = new SingleFieldConstraint();
        comp2sfc1.setFieldType(DataType.TYPE_STRING);
        comp2sfc1.setFieldName("field2");
        comp2sfc1.setOperator("==");
        comp2sfc1.setValue("$f2");
        comp2sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        comp2.addConstraint(comp2sfc1);

        final SingleFieldConstraint comp2sfc2 = new SingleFieldConstraint();
        comp2sfc2.setFieldType(DataType.TYPE_STRING);
        comp2sfc2.setFieldName("field3");
        comp2sfc2.setOperator("==");
        comp2sfc2.setValue("$f3");
        comp2sfc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        comp2.addConstraint(comp2sfc2);

        comp.addConstraint(comp2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", null, null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraintsSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(comp);

        m.addLhsItem(p);

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");
        sfc1.setOperator("==");
        comp.addConstraint(sfc1);

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_AND);
        final SingleFieldConstraint comp2sfc1 = new SingleFieldConstraint();
        comp2sfc1.setFieldType(DataType.TYPE_STRING);
        comp2sfc1.setFieldName("field2");
        comp2sfc1.setOperator("==");
        comp2sfc1.setValue("$f2");
        comp2sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        comp2.addConstraint(comp2sfc1);

        final SingleFieldConstraint comp2sfc2 = new SingleFieldConstraint();
        comp2sfc2.setFieldType(DataType.TYPE_STRING);
        comp2sfc2.setFieldName("field3");
        comp2sfc2.setOperator("==");
        comp2sfc2.setValue("$f3");
        comp2sfc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        comp2.addConstraint(comp2sfc2);

        comp.addConstraint(comp2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( ( field2 == \"bar\" ) )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{null, "bar", null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraintsThirdValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(comp);

        m.addLhsItem(p);

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");
        sfc1.setOperator("==");
        comp.addConstraint(sfc1);

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_AND);
        final SingleFieldConstraint comp2sfc1 = new SingleFieldConstraint();
        comp2sfc1.setFieldType(DataType.TYPE_STRING);
        comp2sfc1.setFieldName("field2");
        comp2sfc1.setOperator("==");
        comp2sfc1.setValue("$f2");
        comp2sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        comp2.addConstraint(comp2sfc1);

        final SingleFieldConstraint comp2sfc2 = new SingleFieldConstraint();
        comp2sfc2.setFieldType(DataType.TYPE_STRING);
        comp2sfc2.setFieldName("field3");
        comp2sfc2.setOperator("==");
        comp2sfc2.setValue("$f3");
        comp2sfc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        comp2.addConstraint(comp2sfc2);

        comp.addConstraint(comp2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( ( field3 == \"goo\" ) )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{null, null, "goo"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints2AllValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(comp);

        m.addLhsItem(p);

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");
        sfc1.setOperator("==");
        comp.addConstraint(sfc1);

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_AND);
        final SingleFieldConstraint comp2sfc1 = new SingleFieldConstraint();
        comp2sfc1.setFieldType(DataType.TYPE_STRING);
        comp2sfc1.setFieldName("field2");
        comp2sfc1.setOperator("==");
        comp2sfc1.setValue("$f2");
        comp2sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        comp2.addConstraint(comp2sfc1);

        comp.addConstraint(comp2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" || ( field2 == \"bar\" ) )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints2FirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(comp);

        m.addLhsItem(p);

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");
        sfc1.setOperator("==");
        comp.addConstraint(sfc1);

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_AND);
        final SingleFieldConstraint comp2sfc1 = new SingleFieldConstraint();
        comp2sfc1.setFieldType(DataType.TYPE_STRING);
        comp2sfc1.setFieldName("field2");
        comp2sfc1.setOperator("==");
        comp2sfc1.setValue("$f2");
        comp2sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        comp2.addConstraint(comp2sfc1);

        comp.addConstraint(comp2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints2SecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(comp);

        m.addLhsItem(p);

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");
        sfc1.setOperator("==");
        comp.addConstraint(sfc1);

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_AND);
        final SingleFieldConstraint comp2sfc1 = new SingleFieldConstraint();
        comp2sfc1.setFieldType(DataType.TYPE_STRING);
        comp2sfc1.setFieldName("field2");
        comp2sfc1.setOperator("==");
        comp2sfc1.setValue("$f2");
        comp2sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        comp2.addConstraint(comp2sfc1);

        comp.addConstraint(comp2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( ( field2 == \"bar\" ) )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{null, "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints3AllValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(comp);

        m.addLhsItem(p);

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");
        sfc1.setOperator("==");
        comp.addConstraint(sfc1);

        final SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setFieldName("field2");
        sfc2.setFieldType(DataType.TYPE_STRING);
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setValue("$f2");
        sfc2.setOperator("==");
        comp.addConstraint(sfc2);

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_AND);
        final SingleFieldConstraint comp2sfc1 = new SingleFieldConstraint();
        comp2sfc1.setFieldType(DataType.TYPE_STRING);
        comp2sfc1.setFieldName("field3");
        comp2sfc1.setOperator("==");
        comp2sfc1.setValue("$f3");
        comp2sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        comp2.addConstraint(comp2sfc1);

        comp.addConstraint(comp2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" || field2== \"bar\" || ( field3 == \"goo\" ) )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", "bar", "goo"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints3FirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(comp);

        m.addLhsItem(p);

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");
        sfc1.setOperator("==");
        comp.addConstraint(sfc1);

        final SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setFieldName("field2");
        sfc2.setFieldType(DataType.TYPE_STRING);
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setValue("$f2");
        sfc2.setOperator("==");
        comp.addConstraint(sfc2);

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_AND);
        final SingleFieldConstraint comp2sfc1 = new SingleFieldConstraint();
        comp2sfc1.setFieldType(DataType.TYPE_STRING);
        comp2sfc1.setFieldName("field3");
        comp2sfc1.setOperator("==");
        comp2sfc1.setValue("$f3");
        comp2sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        comp2.addConstraint(comp2sfc1);

        comp.addConstraint(comp2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", null, null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints3SecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(comp);

        m.addLhsItem(p);

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");
        sfc1.setOperator("==");
        comp.addConstraint(sfc1);

        final SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setFieldName("field2");
        sfc2.setFieldType(DataType.TYPE_STRING);
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setValue("$f2");
        sfc2.setOperator("==");
        comp.addConstraint(sfc2);

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_AND);
        final SingleFieldConstraint comp2sfc1 = new SingleFieldConstraint();
        comp2sfc1.setFieldType(DataType.TYPE_STRING);
        comp2sfc1.setFieldName("field3");
        comp2sfc1.setOperator("==");
        comp2sfc1.setValue("$f3");
        comp2sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        comp2.addConstraint(comp2sfc1);

        comp.addConstraint(comp2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field2== \"bar\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{null, "bar", null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints3ThirdValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(comp);

        m.addLhsItem(p);

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");
        sfc1.setOperator("==");
        comp.addConstraint(sfc1);

        final SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setFieldName("field2");
        sfc2.setFieldType(DataType.TYPE_STRING);
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setValue("$f2");
        sfc2.setOperator("==");
        comp.addConstraint(sfc2);

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_AND);
        final SingleFieldConstraint comp2sfc1 = new SingleFieldConstraint();
        comp2sfc1.setFieldType(DataType.TYPE_STRING);
        comp2sfc1.setFieldName("field3");
        comp2sfc1.setOperator("==");
        comp2sfc1.setValue("$f3");
        comp2sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        comp2.addConstraint(comp2sfc1);

        comp.addConstraint(comp2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( ( field3 == \"goo\" ) )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{null, null, "goo"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints4AllValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");

        m.addLhsItem(p);

        CompositeFieldConstraint comp1 = new CompositeFieldConstraint();
        comp1.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);

        final SingleFieldConstraint comp1sfc1 = new SingleFieldConstraint();
        comp1sfc1.setFieldName("field1");
        comp1sfc1.setFieldType(DataType.TYPE_STRING);
        comp1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc1.setValue("$f1");
        comp1sfc1.setOperator("==");
        comp1.addConstraint(comp1sfc1);

        final SingleFieldConstraint comp1sfc2 = new SingleFieldConstraint();
        comp1sfc2.setFieldName("field2");
        comp1sfc2.setFieldType(DataType.TYPE_STRING);
        comp1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc2.setValue("$f2");
        comp1sfc2.setOperator("==");
        comp1.addConstraint(comp1sfc2);

        p.addConstraint(comp1);

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);

        final SingleFieldConstraint comp2sfc1 = new SingleFieldConstraint();
        comp2sfc1.setFieldType(DataType.TYPE_STRING);
        comp2sfc1.setFieldName("field3");
        comp2sfc1.setOperator("==");
        comp2sfc1.setValue("$f3");
        comp2sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        comp2.addConstraint(comp2sfc1);

        final SingleFieldConstraint comp2sfc2 = new SingleFieldConstraint();
        comp2sfc2.setFieldName("field4");
        comp2sfc2.setFieldType(DataType.TYPE_STRING);
        comp2sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp2sfc2.setValue("$f4");
        comp2sfc2.setOperator("==");
        comp2.addConstraint(comp2sfc2);

        p.addConstraint(comp2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" || field2== \"bar\", field3 == \"goo\" || field4 == \"boo\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", "bar", "goo", "boo"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints4FirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");

        m.addLhsItem(p);

        CompositeFieldConstraint comp1 = new CompositeFieldConstraint();
        comp1.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);

        final SingleFieldConstraint comp1sfc1 = new SingleFieldConstraint();
        comp1sfc1.setFieldName("field1");
        comp1sfc1.setFieldType(DataType.TYPE_STRING);
        comp1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc1.setValue("$f1");
        comp1sfc1.setOperator("==");
        comp1.addConstraint(comp1sfc1);

        final SingleFieldConstraint comp1sfc2 = new SingleFieldConstraint();
        comp1sfc2.setFieldName("field2");
        comp1sfc2.setFieldType(DataType.TYPE_STRING);
        comp1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc2.setValue("$f2");
        comp1sfc2.setOperator("==");
        comp1.addConstraint(comp1sfc2);

        p.addConstraint(comp1);

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);

        final SingleFieldConstraint comp2sfc1 = new SingleFieldConstraint();
        comp2sfc1.setFieldType(DataType.TYPE_STRING);
        comp2sfc1.setFieldName("field3");
        comp2sfc1.setOperator("==");
        comp2sfc1.setValue("$f3");
        comp2sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        comp2.addConstraint(comp2sfc1);

        final SingleFieldConstraint comp2sfc2 = new SingleFieldConstraint();
        comp2sfc2.setFieldName("field4");
        comp2sfc2.setFieldType(DataType.TYPE_STRING);
        comp2sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp2sfc2.setValue("$f4");
        comp2sfc2.setOperator("==");
        comp2.addConstraint(comp2sfc2);

        p.addConstraint(comp2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", null, null, null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints4SecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");

        m.addLhsItem(p);

        CompositeFieldConstraint comp1 = new CompositeFieldConstraint();
        comp1.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);

        final SingleFieldConstraint comp1sfc1 = new SingleFieldConstraint();
        comp1sfc1.setFieldName("field1");
        comp1sfc1.setFieldType(DataType.TYPE_STRING);
        comp1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc1.setValue("$f1");
        comp1sfc1.setOperator("==");
        comp1.addConstraint(comp1sfc1);

        final SingleFieldConstraint comp1sfc2 = new SingleFieldConstraint();
        comp1sfc2.setFieldName("field2");
        comp1sfc2.setFieldType(DataType.TYPE_STRING);
        comp1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc2.setValue("$f2");
        comp1sfc2.setOperator("==");
        comp1.addConstraint(comp1sfc2);

        p.addConstraint(comp1);

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);

        final SingleFieldConstraint comp2sfc1 = new SingleFieldConstraint();
        comp2sfc1.setFieldType(DataType.TYPE_STRING);
        comp2sfc1.setFieldName("field3");
        comp2sfc1.setOperator("==");
        comp2sfc1.setValue("$f3");
        comp2sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        comp2.addConstraint(comp2sfc1);

        final SingleFieldConstraint comp2sfc2 = new SingleFieldConstraint();
        comp2sfc2.setFieldName("field4");
        comp2sfc2.setFieldType(DataType.TYPE_STRING);
        comp2sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp2sfc2.setValue("$f4");
        comp2sfc2.setOperator("==");
        comp2.addConstraint(comp2sfc2);

        p.addConstraint(comp2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field2== \"bar\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{null, "bar", null, null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints4ThirdValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");

        m.addLhsItem(p);

        CompositeFieldConstraint comp1 = new CompositeFieldConstraint();
        comp1.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);

        final SingleFieldConstraint comp1sfc1 = new SingleFieldConstraint();
        comp1sfc1.setFieldName("field1");
        comp1sfc1.setFieldType(DataType.TYPE_STRING);
        comp1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc1.setValue("$f1");
        comp1sfc1.setOperator("==");
        comp1.addConstraint(comp1sfc1);

        final SingleFieldConstraint comp1sfc2 = new SingleFieldConstraint();
        comp1sfc2.setFieldName("field2");
        comp1sfc2.setFieldType(DataType.TYPE_STRING);
        comp1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc2.setValue("$f2");
        comp1sfc2.setOperator("==");
        comp1.addConstraint(comp1sfc2);

        p.addConstraint(comp1);

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);

        final SingleFieldConstraint comp2sfc1 = new SingleFieldConstraint();
        comp2sfc1.setFieldType(DataType.TYPE_STRING);
        comp2sfc1.setFieldName("field3");
        comp2sfc1.setOperator("==");
        comp2sfc1.setValue("$f3");
        comp2sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        comp2.addConstraint(comp2sfc1);

        final SingleFieldConstraint comp2sfc2 = new SingleFieldConstraint();
        comp2sfc2.setFieldName("field4");
        comp2sfc2.setFieldType(DataType.TYPE_STRING);
        comp2sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp2sfc2.setValue("$f4");
        comp2sfc2.setOperator("==");
        comp2.addConstraint(comp2sfc2);

        p.addConstraint(comp2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field3 == \"goo\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{null, null, "goo", null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints4FourthValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");

        m.addLhsItem(p);

        CompositeFieldConstraint comp1 = new CompositeFieldConstraint();
        comp1.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);

        final SingleFieldConstraint comp1sfc1 = new SingleFieldConstraint();
        comp1sfc1.setFieldName("field1");
        comp1sfc1.setFieldType(DataType.TYPE_STRING);
        comp1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc1.setValue("$f1");
        comp1sfc1.setOperator("==");
        comp1.addConstraint(comp1sfc1);

        final SingleFieldConstraint comp1sfc2 = new SingleFieldConstraint();
        comp1sfc2.setFieldName("field2");
        comp1sfc2.setFieldType(DataType.TYPE_STRING);
        comp1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc2.setValue("$f2");
        comp1sfc2.setOperator("==");
        comp1.addConstraint(comp1sfc2);

        p.addConstraint(comp1);

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);

        final SingleFieldConstraint comp2sfc1 = new SingleFieldConstraint();
        comp2sfc1.setFieldType(DataType.TYPE_STRING);
        comp2sfc1.setFieldName("field3");
        comp2sfc1.setOperator("==");
        comp2sfc1.setValue("$f3");
        comp2sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        comp2.addConstraint(comp2sfc1);

        final SingleFieldConstraint comp2sfc2 = new SingleFieldConstraint();
        comp2sfc2.setFieldName("field4");
        comp2sfc2.setFieldType(DataType.TYPE_STRING);
        comp2sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp2sfc2.setValue("$f4");
        comp2sfc2.setOperator("==");
        comp2.addConstraint(comp2sfc2);

        p.addConstraint(comp2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field4 == \"boo\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{null, null, null, "boo"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints5AllValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(comp);

        m.addLhsItem(p);

        final SingleFieldConstraint comp1sfc1 = new SingleFieldConstraint();
        comp1sfc1.setFieldName("field1");
        comp1sfc1.setFieldType(DataType.TYPE_STRING);
        comp1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc1.setValue("$f1");
        comp1sfc1.setOperator("==");
        comp.addConstraint(comp1sfc1);

        final SingleFieldConstraint comp1sfc2 = new SingleFieldConstraint();
        comp1sfc2.setFieldName("field2");
        comp1sfc2.setFieldType(DataType.TYPE_STRING);
        comp1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc2.setValue("$f2");
        comp1sfc2.setOperator("==");
        comp.addConstraint(comp1sfc2);

        final SingleFieldConstraint comp1sfc3 = new SingleFieldConstraint();
        comp1sfc3.setFieldName("field3");
        comp1sfc3.setFieldType(DataType.TYPE_STRING);
        comp1sfc3.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc3.setValue("$f3");
        comp1sfc3.setOperator("==");
        comp.addConstraint(comp1sfc3);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" || field2 == \"bar\" || field3 == \"goo\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", "bar", "goo"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints5FirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(comp);

        m.addLhsItem(p);

        final SingleFieldConstraint comp1sfc1 = new SingleFieldConstraint();
        comp1sfc1.setFieldName("field1");
        comp1sfc1.setFieldType(DataType.TYPE_STRING);
        comp1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc1.setValue("$f1");
        comp1sfc1.setOperator("==");
        comp.addConstraint(comp1sfc1);

        final SingleFieldConstraint comp1sfc2 = new SingleFieldConstraint();
        comp1sfc2.setFieldName("field2");
        comp1sfc2.setFieldType(DataType.TYPE_STRING);
        comp1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc2.setValue("$f2");
        comp1sfc2.setOperator("==");
        comp.addConstraint(comp1sfc2);

        final SingleFieldConstraint comp1sfc3 = new SingleFieldConstraint();
        comp1sfc3.setFieldName("field3");
        comp1sfc3.setFieldType(DataType.TYPE_STRING);
        comp1sfc3.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc3.setValue("$f3");
        comp1sfc3.setOperator("==");
        comp.addConstraint(comp1sfc3);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", null, null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints5SecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(comp);

        m.addLhsItem(p);

        final SingleFieldConstraint comp1sfc1 = new SingleFieldConstraint();
        comp1sfc1.setFieldName("field1");
        comp1sfc1.setFieldType(DataType.TYPE_STRING);
        comp1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc1.setValue("$f1");
        comp1sfc1.setOperator("==");
        comp.addConstraint(comp1sfc1);

        final SingleFieldConstraint comp1sfc2 = new SingleFieldConstraint();
        comp1sfc2.setFieldName("field2");
        comp1sfc2.setFieldType(DataType.TYPE_STRING);
        comp1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc2.setValue("$f2");
        comp1sfc2.setOperator("==");
        comp.addConstraint(comp1sfc2);

        final SingleFieldConstraint comp1sfc3 = new SingleFieldConstraint();
        comp1sfc3.setFieldName("field3");
        comp1sfc3.setFieldType(DataType.TYPE_STRING);
        comp1sfc3.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc3.setValue("$f3");
        comp1sfc3.setOperator("==");
        comp.addConstraint(comp1sfc3);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field2 == \"bar\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{null, "bar", null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints5ThirdValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(comp);

        m.addLhsItem(p);

        final SingleFieldConstraint comp1sfc1 = new SingleFieldConstraint();
        comp1sfc1.setFieldName("field1");
        comp1sfc1.setFieldType(DataType.TYPE_STRING);
        comp1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc1.setValue("$f1");
        comp1sfc1.setOperator("==");
        comp.addConstraint(comp1sfc1);

        final SingleFieldConstraint comp1sfc2 = new SingleFieldConstraint();
        comp1sfc2.setFieldName("field2");
        comp1sfc2.setFieldType(DataType.TYPE_STRING);
        comp1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc2.setValue("$f2");
        comp1sfc2.setOperator("==");
        comp.addConstraint(comp1sfc2);

        final SingleFieldConstraint comp1sfc3 = new SingleFieldConstraint();
        comp1sfc3.setFieldName("field3");
        comp1sfc3.setFieldType(DataType.TYPE_STRING);
        comp1sfc3.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        comp1sfc3.setValue("$f3");
        comp1sfc3.setOperator("==");
        comp.addConstraint(comp1sfc3);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field3 == \"goo\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{null, null, "goo"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints6AllValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");

        m.addLhsItem(p);

        final CompositeFieldConstraint cfc1 = new CompositeFieldConstraint();
        cfc1.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc1);
        final SingleFieldConstraint cfc1sfc1 = new SingleFieldConstraint();
        cfc1sfc1.setFieldName("field1");
        cfc1sfc1.setFieldType(DataType.TYPE_STRING);
        cfc1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc1sfc1.setValue("$f1");
        cfc1sfc1.setOperator("==");
        cfc1.addConstraint(cfc1sfc1);
        final SingleFieldConstraint cfc1sfc2 = new SingleFieldConstraint();
        cfc1sfc2.setFieldName("field2");
        cfc1sfc2.setFieldType(DataType.TYPE_STRING);
        cfc1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc1sfc2.setValue("$f2");
        cfc1sfc2.setOperator("==");
        cfc1.addConstraint(cfc1sfc2);

        final CompositeFieldConstraint cfc2 = new CompositeFieldConstraint();
        cfc2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc2);
        final SingleFieldConstraint cfc2sfc1 = new SingleFieldConstraint();
        cfc2sfc1.setFieldName("field3");
        cfc2sfc1.setFieldType(DataType.TYPE_STRING);
        cfc2sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc2sfc1.setValue("$f3");
        cfc2sfc1.setOperator("==");
        cfc2.addConstraint(cfc2sfc1);
        final SingleFieldConstraint cfc2sfc2 = new SingleFieldConstraint();
        cfc2sfc2.setFieldName("field4");
        cfc2sfc2.setFieldType(DataType.TYPE_STRING);
        cfc2sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc2sfc2.setValue("$f4");
        cfc2sfc2.setOperator("==");
        cfc2.addConstraint(cfc2sfc2);

        final CompositeFieldConstraint cfc3 = new CompositeFieldConstraint();
        cfc3.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc3);
        final SingleFieldConstraint cfc3sfc1 = new SingleFieldConstraint();
        cfc3sfc1.setFieldName("field5");
        cfc3sfc1.setFieldType(DataType.TYPE_STRING);
        cfc3sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc3sfc1.setValue("$f5");
        cfc3sfc1.setOperator("==");
        cfc3.addConstraint(cfc3sfc1);
        final SingleFieldConstraint cfc3sfc2 = new SingleFieldConstraint();
        cfc3sfc2.setFieldName("field6");
        cfc3sfc2.setFieldType(DataType.TYPE_STRING);
        cfc3sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc3sfc2.setValue("$f6");
        cfc3sfc2.setOperator("==");
        cfc3.addConstraint(cfc3sfc2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"v1\" || field2 == \"v2\", field3 == \"v3\" || field4 == \"v4\", field5 == \"v5\" || field6 == \"v6\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"v1", "v2", "v3", "v4", "v5", "v6"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints6NoValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");

        m.addLhsItem(p);

        final CompositeFieldConstraint cfc1 = new CompositeFieldConstraint();
        cfc1.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc1);
        final SingleFieldConstraint cfc1sfc1 = new SingleFieldConstraint();
        cfc1sfc1.setFieldName("field1");
        cfc1sfc1.setFieldType(DataType.TYPE_STRING);
        cfc1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc1sfc1.setValue("$f1");
        cfc1sfc1.setOperator("==");
        cfc1.addConstraint(cfc1sfc1);
        final SingleFieldConstraint cfc1sfc2 = new SingleFieldConstraint();
        cfc1sfc2.setFieldName("field2");
        cfc1sfc2.setFieldType(DataType.TYPE_STRING);
        cfc1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc1sfc2.setValue("$f2");
        cfc1sfc2.setOperator("==");
        cfc1.addConstraint(cfc1sfc2);

        final CompositeFieldConstraint cfc2 = new CompositeFieldConstraint();
        cfc2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc2);
        final SingleFieldConstraint cfc2sfc1 = new SingleFieldConstraint();
        cfc2sfc1.setFieldName("field3");
        cfc2sfc1.setFieldType(DataType.TYPE_STRING);
        cfc2sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc2sfc1.setValue("$f3");
        cfc2sfc1.setOperator("==");
        cfc2.addConstraint(cfc2sfc1);
        final SingleFieldConstraint cfc2sfc2 = new SingleFieldConstraint();
        cfc2sfc2.setFieldName("field4");
        cfc2sfc2.setFieldType(DataType.TYPE_STRING);
        cfc2sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc2sfc2.setValue("$f4");
        cfc2sfc2.setOperator("==");
        cfc2.addConstraint(cfc2sfc2);

        final CompositeFieldConstraint cfc3 = new CompositeFieldConstraint();
        cfc3.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc3);
        final SingleFieldConstraint cfc3sfc1 = new SingleFieldConstraint();
        cfc3sfc1.setFieldName("field5");
        cfc3sfc1.setFieldType(DataType.TYPE_STRING);
        cfc3sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc3sfc1.setValue("$f5");
        cfc3sfc1.setOperator("==");
        cfc3.addConstraint(cfc3sfc1);
        final SingleFieldConstraint cfc3sfc2 = new SingleFieldConstraint();
        cfc3sfc2.setFieldName("field6");
        cfc3sfc2.setFieldType(DataType.TYPE_STRING);
        cfc3sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc3sfc2.setValue("$f6");
        cfc3sfc2.setOperator("==");
        cfc3.addConstraint(cfc3sfc2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{null, null, null, null, null, null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints6VariablesAndLiterals() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");

        m.addLhsItem(p);

        final CompositeFieldConstraint cfc1 = new CompositeFieldConstraint();
        cfc1.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc1);
        final SingleFieldConstraint cfc1sfc1 = new SingleFieldConstraint();
        cfc1sfc1.setFieldName("field1");
        cfc1sfc1.setFieldType(DataType.TYPE_STRING);
        cfc1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc1sfc1.setValue("$f1");
        cfc1sfc1.setOperator("==");
        cfc1.addConstraint(cfc1sfc1);
        final SingleFieldConstraint cfc1sfc2 = new SingleFieldConstraint();
        cfc1sfc2.setFieldName("field2");
        cfc1sfc2.setFieldType(DataType.TYPE_STRING);
        cfc1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        cfc1sfc2.setValue("v2");
        cfc1sfc2.setOperator("==");
        cfc1.addConstraint(cfc1sfc2);

        final CompositeFieldConstraint cfc2 = new CompositeFieldConstraint();
        cfc2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc2);
        final SingleFieldConstraint cfc2sfc1 = new SingleFieldConstraint();
        cfc2sfc1.setFieldName("field3");
        cfc2sfc1.setFieldType(DataType.TYPE_STRING);
        cfc2sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc2sfc1.setValue("$f3");
        cfc2sfc1.setOperator("==");
        cfc2.addConstraint(cfc2sfc1);
        final SingleFieldConstraint cfc2sfc2 = new SingleFieldConstraint();
        cfc2sfc2.setFieldName("field4");
        cfc2sfc2.setFieldType(DataType.TYPE_STRING);
        cfc2sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        cfc2sfc2.setValue("v4");
        cfc2sfc2.setOperator("==");
        cfc2.addConstraint(cfc2sfc2);

        final CompositeFieldConstraint cfc3 = new CompositeFieldConstraint();
        cfc3.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc3);
        final SingleFieldConstraint cfc3sfc1 = new SingleFieldConstraint();
        cfc3sfc1.setFieldName("field5");
        cfc3sfc1.setFieldType(DataType.TYPE_STRING);
        cfc3sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc3sfc1.setValue("$f5");
        cfc3sfc1.setOperator("==");
        cfc3.addConstraint(cfc3sfc1);
        final SingleFieldConstraint cfc3sfc2 = new SingleFieldConstraint();
        cfc3sfc2.setFieldName("field6");
        cfc3sfc2.setFieldType(DataType.TYPE_STRING);
        cfc3sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        cfc3sfc2.setValue("v6");
        cfc3sfc2.setOperator("==");
        cfc3.addConstraint(cfc3sfc2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"v1\" || field2 == \"v2\", field3 == \"v3\" || field4 == \"v4\", field5 == \"v5\" || field6 == \"v6\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"v1", "v3", "v5"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints6VariablesNoValuesAndLiterals() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");

        m.addLhsItem(p);

        final CompositeFieldConstraint cfc1 = new CompositeFieldConstraint();
        cfc1.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc1);
        final SingleFieldConstraint cfc1sfc1 = new SingleFieldConstraint();
        cfc1sfc1.setFieldName("field1");
        cfc1sfc1.setFieldType(DataType.TYPE_STRING);
        cfc1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc1sfc1.setValue("$f1");
        cfc1sfc1.setOperator("==");
        cfc1.addConstraint(cfc1sfc1);
        final SingleFieldConstraint cfc1sfc2 = new SingleFieldConstraint();
        cfc1sfc2.setFieldName("field2");
        cfc1sfc2.setFieldType(DataType.TYPE_STRING);
        cfc1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        cfc1sfc2.setValue("v2");
        cfc1sfc2.setOperator("==");
        cfc1.addConstraint(cfc1sfc2);

        final CompositeFieldConstraint cfc2 = new CompositeFieldConstraint();
        cfc2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc2);
        final SingleFieldConstraint cfc2sfc1 = new SingleFieldConstraint();
        cfc2sfc1.setFieldName("field3");
        cfc2sfc1.setFieldType(DataType.TYPE_STRING);
        cfc2sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc2sfc1.setValue("$f3");
        cfc2sfc1.setOperator("==");
        cfc2.addConstraint(cfc2sfc1);
        final SingleFieldConstraint cfc2sfc2 = new SingleFieldConstraint();
        cfc2sfc2.setFieldName("field4");
        cfc2sfc2.setFieldType(DataType.TYPE_STRING);
        cfc2sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        cfc2sfc2.setValue("v4");
        cfc2sfc2.setOperator("==");
        cfc2.addConstraint(cfc2sfc2);

        final CompositeFieldConstraint cfc3 = new CompositeFieldConstraint();
        cfc3.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc3);
        final SingleFieldConstraint cfc3sfc1 = new SingleFieldConstraint();
        cfc3sfc1.setFieldName("field5");
        cfc3sfc1.setFieldType(DataType.TYPE_STRING);
        cfc3sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc3sfc1.setValue("$f5");
        cfc3sfc1.setOperator("==");
        cfc3.addConstraint(cfc3sfc1);
        final SingleFieldConstraint cfc3sfc2 = new SingleFieldConstraint();
        cfc3sfc2.setFieldName("field6");
        cfc3sfc2.setFieldType(DataType.TYPE_STRING);
        cfc3sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        cfc3sfc2.setValue("v6");
        cfc3sfc2.setOperator("==");
        cfc3.addConstraint(cfc3sfc2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field2 == \"v2\", field4 == \"v4\", field6 == \"v6\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{null, null, null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints6VariablesWithValuesAndLiterals() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");

        m.addLhsItem(p);

        final CompositeFieldConstraint cfc1 = new CompositeFieldConstraint();
        cfc1.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc1);
        final SingleFieldConstraint cfc1sfc1 = new SingleFieldConstraint();
        cfc1sfc1.setFieldName("field1");
        cfc1sfc1.setFieldType(DataType.TYPE_STRING);
        cfc1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc1sfc1.setValue("$f1");
        cfc1sfc1.setOperator("==");
        cfc1.addConstraint(cfc1sfc1);
        final SingleFieldConstraint cfc1sfc2 = new SingleFieldConstraint();
        cfc1sfc2.setFieldName("field2");
        cfc1sfc2.setFieldType(DataType.TYPE_STRING);
        cfc1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        cfc1sfc2.setValue("v2");
        cfc1sfc2.setOperator("==");
        cfc1.addConstraint(cfc1sfc2);

        final CompositeFieldConstraint cfc2 = new CompositeFieldConstraint();
        cfc2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc2);
        final SingleFieldConstraint cfc2sfc1 = new SingleFieldConstraint();
        cfc2sfc1.setFieldName("field3");
        cfc2sfc1.setFieldType(DataType.TYPE_STRING);
        cfc2sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc2sfc1.setValue("$f3");
        cfc2sfc1.setOperator("==");
        cfc2.addConstraint(cfc2sfc1);
        final SingleFieldConstraint cfc2sfc2 = new SingleFieldConstraint();
        cfc2sfc2.setFieldName("field4");
        cfc2sfc2.setFieldType(DataType.TYPE_STRING);
        cfc2sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        cfc2sfc2.setValue("v4");
        cfc2sfc2.setOperator("==");
        cfc2.addConstraint(cfc2sfc2);

        final CompositeFieldConstraint cfc3 = new CompositeFieldConstraint();
        cfc3.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc3);
        final SingleFieldConstraint cfc3sfc1 = new SingleFieldConstraint();
        cfc3sfc1.setFieldName("field5");
        cfc3sfc1.setFieldType(DataType.TYPE_STRING);
        cfc3sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc3sfc1.setValue("$f5");
        cfc3sfc1.setOperator("==");
        cfc3.addConstraint(cfc3sfc1);
        final SingleFieldConstraint cfc3sfc2 = new SingleFieldConstraint();
        cfc3sfc2.setFieldName("field6");
        cfc3sfc2.setFieldType(DataType.TYPE_STRING);
        cfc3sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        cfc3sfc2.setValue("v6");
        cfc3sfc2.setOperator("==");
        cfc3.addConstraint(cfc3sfc2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"v1\" || field2 == \"v2\", field3 == \"v3\" || field4 == \"v4\", field5 == \"v5\" || field6 == \"v6\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"v1", "v3", "v5"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints6LiteralsAndVariablesNoValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");

        m.addLhsItem(p);

        final CompositeFieldConstraint cfc1 = new CompositeFieldConstraint();
        cfc1.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc1);
        final SingleFieldConstraint cfc1sfc1 = new SingleFieldConstraint();
        cfc1sfc1.setFieldName("field1");
        cfc1sfc1.setFieldType(DataType.TYPE_STRING);
        cfc1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        cfc1sfc1.setValue("v1");
        cfc1sfc1.setOperator("==");
        cfc1.addConstraint(cfc1sfc1);
        final SingleFieldConstraint cfc1sfc2 = new SingleFieldConstraint();
        cfc1sfc2.setFieldName("field2");
        cfc1sfc2.setFieldType(DataType.TYPE_STRING);
        cfc1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc1sfc2.setValue("$f2");
        cfc1sfc2.setOperator("==");
        cfc1.addConstraint(cfc1sfc2);

        final CompositeFieldConstraint cfc2 = new CompositeFieldConstraint();
        cfc2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc2);
        final SingleFieldConstraint cfc2sfc1 = new SingleFieldConstraint();
        cfc2sfc1.setFieldName("field3");
        cfc2sfc1.setFieldType(DataType.TYPE_STRING);
        cfc2sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        cfc2sfc1.setValue("v3");
        cfc2sfc1.setOperator("==");
        cfc2.addConstraint(cfc2sfc1);
        final SingleFieldConstraint cfc2sfc2 = new SingleFieldConstraint();
        cfc2sfc2.setFieldName("field4");
        cfc2sfc2.setFieldType(DataType.TYPE_STRING);
        cfc2sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc2sfc2.setValue("$f4");
        cfc2sfc2.setOperator("==");
        cfc2.addConstraint(cfc2sfc2);

        final CompositeFieldConstraint cfc3 = new CompositeFieldConstraint();
        cfc3.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc3);
        final SingleFieldConstraint cfc3sfc1 = new SingleFieldConstraint();
        cfc3sfc1.setFieldName("field5");
        cfc3sfc1.setFieldType(DataType.TYPE_STRING);
        cfc3sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        cfc3sfc1.setValue("v5");
        cfc3sfc1.setOperator("==");
        cfc3.addConstraint(cfc3sfc1);
        final SingleFieldConstraint cfc3sfc2 = new SingleFieldConstraint();
        cfc3sfc2.setFieldName("field6");
        cfc3sfc2.setFieldType(DataType.TYPE_STRING);
        cfc3sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc3sfc2.setValue("$f6");
        cfc3sfc2.setOperator("==");
        cfc3.addConstraint(cfc3sfc2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"v1\", field3 == \"v3\", field5 == \"v5\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{null, null, null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testNestedCompositeConstraints6LiteralsAndVariablesWithValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p = new FactPattern("Person");

        m.addLhsItem(p);

        final CompositeFieldConstraint cfc1 = new CompositeFieldConstraint();
        cfc1.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc1);
        final SingleFieldConstraint cfc1sfc1 = new SingleFieldConstraint();
        cfc1sfc1.setFieldName("field1");
        cfc1sfc1.setFieldType(DataType.TYPE_STRING);
        cfc1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        cfc1sfc1.setValue("v1");
        cfc1sfc1.setOperator("==");
        cfc1.addConstraint(cfc1sfc1);
        final SingleFieldConstraint cfc1sfc2 = new SingleFieldConstraint();
        cfc1sfc2.setFieldName("field2");
        cfc1sfc2.setFieldType(DataType.TYPE_STRING);
        cfc1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc1sfc2.setValue("$f2");
        cfc1sfc2.setOperator("==");
        cfc1.addConstraint(cfc1sfc2);

        final CompositeFieldConstraint cfc2 = new CompositeFieldConstraint();
        cfc2.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc2);
        final SingleFieldConstraint cfc2sfc1 = new SingleFieldConstraint();
        cfc2sfc1.setFieldName("field3");
        cfc2sfc1.setFieldType(DataType.TYPE_STRING);
        cfc2sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        cfc2sfc1.setValue("v3");
        cfc2sfc1.setOperator("==");
        cfc2.addConstraint(cfc2sfc1);
        final SingleFieldConstraint cfc2sfc2 = new SingleFieldConstraint();
        cfc2sfc2.setFieldName("field4");
        cfc2sfc2.setFieldType(DataType.TYPE_STRING);
        cfc2sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc2sfc2.setValue("$f4");
        cfc2sfc2.setOperator("==");
        cfc2.addConstraint(cfc2sfc2);

        final CompositeFieldConstraint cfc3 = new CompositeFieldConstraint();
        cfc3.setCompositeJunctionType(CompositeFieldConstraint.COMPOSITE_TYPE_OR);
        p.addConstraint(cfc3);
        final SingleFieldConstraint cfc3sfc1 = new SingleFieldConstraint();
        cfc3sfc1.setFieldName("field5");
        cfc3sfc1.setFieldType(DataType.TYPE_STRING);
        cfc3sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        cfc3sfc1.setValue("v5");
        cfc3sfc1.setOperator("==");
        cfc3.addConstraint(cfc3sfc1);
        final SingleFieldConstraint cfc3sfc2 = new SingleFieldConstraint();
        cfc3sfc2.setFieldName("field6");
        cfc3sfc2.setFieldType(DataType.TYPE_STRING);
        cfc3sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        cfc3sfc2.setValue("$f6");
        cfc3sfc2.setOperator("==");
        cfc3.addConstraint(cfc3sfc2);

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"v1\" || field2 == \"v2\", field3 == \"v3\" || field4 == \"v4\", field5 == \"v5\" || field6 == \"v6\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"v2", "v4", "v6"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testConnectiveConstraintBothValues() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern("Person");
        m.addLhsItem(p1);

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName("field1");
        X.setFieldType(DataType.TYPE_STRING);
        X.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        X.setValue("$f1");
        X.setOperator("==");
        p1.addConstraint(X);

        ConnectiveConstraint connective = new ConnectiveConstraint();
        connective.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        connective.setFieldType(DataType.TYPE_STRING);
        connective.setOperator("|| ==");
        connective.setValue("$f2");

        X.setConnectives(new ConnectiveConstraint[1]);
        X.getConnectives()[0] = connective;

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" || == \"bar\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testConnectiveConstraintFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern("Person");
        m.addLhsItem(p1);

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName("field1");
        X.setFieldType(DataType.TYPE_STRING);
        X.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        X.setValue("$f1");
        X.setOperator("==");
        p1.addConstraint(X);

        ConnectiveConstraint connective = new ConnectiveConstraint();
        connective.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        connective.setFieldType(DataType.TYPE_STRING);
        connective.setOperator("|| ==");
        connective.setValue("$f2");

        X.setConnectives(new ConnectiveConstraint[1]);
        X.getConnectives()[0] = connective;

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{"foo", null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testConnectiveConstraintSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "t1";

        FactPattern p1 = new FactPattern("Person");
        m.addLhsItem(p1);

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName("field1");
        X.setFieldType(DataType.TYPE_STRING);
        X.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        X.setValue("$f1");
        X.setOperator("==");
        p1.addConstraint(X);

        ConnectiveConstraint connective = new ConnectiveConstraint();
        connective.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        connective.setFieldType(DataType.TYPE_STRING);
        connective.setOperator("|| ==");
        connective.setValue("$f2");

        X.setConnectives(new ConnectiveConstraint[1]);
        X.getConnectives()[0] = connective;

        String expected = "rule \"t1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"bar\" )\n" +
                "then\n" +
                "end\n";

        m.addRow(new String[]{null, "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testSimpleFromCollect() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");

        SingleFieldConstraint sfc = new SingleFieldConstraint("field1");
        sfc.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc.setFieldType(DataType.TYPE_STRING);
        sfc.setOperator("==");
        sfc.setValue("$f1");

        fp.addConstraint(sfc);

        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern(fp);
        fac.setFactPattern(new FactPattern("java.util.List"));
        m.addLhsItem(fac);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "java.util.List( ) from collect ( Person( field1 == \"foo\" ) ) \n" +
                "then\n" +
                "end";

        m.addRow(new String[]{"foo"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testSimpleFromCollectBothValues() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        SingleFieldConstraint sfc = new SingleFieldConstraint("field1");
        sfc.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc.setFieldType(DataType.TYPE_STRING);
        sfc.setOperator("==");
        sfc.setValue("$f1");
        fp.addConstraint(sfc);

        FactPattern fp2 = new FactPattern("java.util.List");
        SingleFieldConstraint sfc2 = new SingleFieldConstraint("size");
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        sfc2.setOperator(">");
        sfc2.setValue("$f2");
        fp2.addConstraint(sfc2);

        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern(fp);
        fac.setFactPattern(fp2);
        m.addLhsItem(fac);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "java.util.List( size > 1 ) from collect ( Person( field1 == \"foo\" ) ) \n" +
                "then\n" +
                "end";

        m.addRow(new String[]{"1", "foo"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testSimpleFromCollectFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        SingleFieldConstraint sfc = new SingleFieldConstraint("field1");
        sfc.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc.setFieldType(DataType.TYPE_STRING);
        sfc.setOperator("==");
        sfc.setValue("$f1");
        fp.addConstraint(sfc);

        FactPattern fp2 = new FactPattern("java.util.List");
        SingleFieldConstraint sfc2 = new SingleFieldConstraint("size");
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        sfc2.setOperator(">");
        sfc2.setValue("$f2");
        fp2.addConstraint(sfc2);

        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern(fp);
        fac.setFactPattern(fp2);
        m.addLhsItem(fac);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "java.util.List( size > 1 ) from collect ( Person( ) )\n" +
                "then\n" +
                "end";

        m.addRow(new String[]{"1", null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testSimpleFromCollectSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        SingleFieldConstraint sfc = new SingleFieldConstraint("field1");
        sfc.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc.setFieldType(DataType.TYPE_STRING);
        sfc.setOperator("==");
        sfc.setValue("$f1");
        fp.addConstraint(sfc);

        FactPattern fp2 = new FactPattern("java.util.List");
        SingleFieldConstraint sfc2 = new SingleFieldConstraint("size");
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        sfc2.setOperator(">");
        sfc2.setValue("$f2");
        fp2.addConstraint(sfc2);

        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern(fp);
        fac.setFactPattern(fp2);
        m.addLhsItem(fac);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "java.util.List() from collect ( Person( field1 == \"foo\" ) )" +
                "then\n" +
                "end";

        m.addRow(new String[]{null, "foo"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testFromCollectFreeFormLineBothValues() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FreeFormLine ffl = new FreeFormLine();
        ffl.setText("Person( field1 == \"@{f1}\", field2 == \"@{f2}\" )");

        FactPattern fp = new FactPattern("java.util.List");

        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern(ffl);
        fac.setFactPattern(fp);
        m.addLhsItem(fac);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "java.util.List() from collect ( Person( field1 == \"foo\", field2 == \"bar\" ) ) \n" +
                "then\n" +
                "end";

        m.addRow(new String[]{"foo", "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testFromCollectFreeFormLineFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FreeFormLine ffl = new FreeFormLine();
        ffl.setText("Person( field1 == \"@{f1}\", field2 == \"@{f2}\" )");

        FactPattern fp = new FactPattern("java.util.List");

        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern(ffl);
        fac.setFactPattern(fp);
        m.addLhsItem(fac);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "then\n" +
                "end";

        m.addRow(new String[]{"foo", null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testFromCollectFreeFormLineSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FreeFormLine ffl = new FreeFormLine();
        ffl.setText("Person( field1 == \"@{f1}\", field2 == \"@{f2}\" )");

        FactPattern fp = new FactPattern("java.util.List");

        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern(ffl);
        fac.setFactPattern(fp);
        m.addLhsItem(fac);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "then\n" +
                "end";

        m.addRow(new String[]{null, "foo"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testSimpleFromCollectMultipleSubPatternValues() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        SingleFieldConstraint sfc = new SingleFieldConstraint("field1");
        sfc.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc.setFieldType(DataType.TYPE_STRING);
        sfc.setOperator("==");
        sfc.setValue("$f1");
        fp.addConstraint(sfc);

        SingleFieldConstraint sfc1 = new SingleFieldConstraint("field2");
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setOperator("==");
        sfc1.setValue("$f2");
        fp.addConstraint(sfc1);

        FactPattern fp2 = new FactPattern("java.util.List");
        SingleFieldConstraint sfc2 = new SingleFieldConstraint("size");
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        sfc2.setOperator(">");
        sfc2.setValue("$f3");
        fp2.addConstraint(sfc2);

        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern(fp);
        fac.setFactPattern(fp2);
        m.addLhsItem(fac);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "java.util.List( size > 1 ) from collect ( Person( field1 == \"foo\", field2 == \"bar\" ) ) \n" +
                "then\n" +
                "end";

        m.addRow(new String[]{"1", "foo", "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testSimpleFromCollectMultipleSubPatternValuesFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        SingleFieldConstraint sfc = new SingleFieldConstraint("field1");
        sfc.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc.setFieldType(DataType.TYPE_STRING);
        sfc.setOperator("==");
        sfc.setValue("$f1");
        fp.addConstraint(sfc);

        SingleFieldConstraint sfc1 = new SingleFieldConstraint("field2");
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setOperator("==");
        sfc1.setValue("$f2");
        fp.addConstraint(sfc1);

        FactPattern fp2 = new FactPattern("java.util.List");
        SingleFieldConstraint sfc2 = new SingleFieldConstraint("size");
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        sfc2.setOperator(">");
        sfc2.setValue("$f3");
        fp2.addConstraint(sfc2);

        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern(fp);
        fac.setFactPattern(fp2);
        m.addLhsItem(fac);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "java.util.List( size > 1 ) from collect ( Person( field1 == \"foo\" ) ) \n" +
                "then\n" +
                "end";

        m.addRow(new String[]{"1", "foo", null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testSimpleFromCollectMultipleSubPatternValuesSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        SingleFieldConstraint sfc = new SingleFieldConstraint("field1");
        sfc.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc.setFieldType(DataType.TYPE_STRING);
        sfc.setOperator("==");
        sfc.setValue("$f1");
        fp.addConstraint(sfc);

        SingleFieldConstraint sfc1 = new SingleFieldConstraint("field2");
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setOperator("==");
        sfc1.setValue("$f2");
        fp.addConstraint(sfc1);

        FactPattern fp2 = new FactPattern("java.util.List");
        SingleFieldConstraint sfc2 = new SingleFieldConstraint("size");
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        sfc2.setOperator(">");
        sfc2.setValue("$f3");
        fp2.addConstraint(sfc2);

        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern(fp);
        fac.setFactPattern(fp2);
        m.addLhsItem(fac);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "java.util.List( size > 1 ) from collect ( Person( field2 == \"bar\" ) ) \n" +
                "then\n" +
                "end";

        m.addRow(new String[]{"1", null, "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testFreeFormLineBothValues() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FreeFormLine ffl = new FreeFormLine();
        ffl.setText("Person( field1 == \"@{f1}\", field2 == \"@{f2}\" )");
        m.addLhsItem(ffl);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Person( field1 == \"foo\", field2 == \"bar\" ) \n" +
                "then\n" +
                "end";

        m.addRow(new String[]{"foo", "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testFreeFormLineFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FreeFormLine ffl = new FreeFormLine();
        ffl.setText("Person( field1 == \"@{f1}\", field2 == \"@{f2}\" )");
        m.addLhsItem(ffl);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "then\n" +
                "end";

        m.addRow(new String[]{"foo", null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testFreeFormLineSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FreeFormLine ffl = new FreeFormLine();
        ffl.setText("Person( field1 == \"@{f1}\", field2 == \"@{f2}\" )");
        m.addLhsItem(ffl);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "then\n" +
                "end";

        m.addRow(new String[]{null, "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionFreeFormLineBothValues() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FreeFormLine ffl = new FreeFormLine();
        ffl.setText("System.println( \"@{f1}\" + \"@{f2}\" );");
        m.addRhsItem(ffl);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "then\n" +
                "System.println( \"foo\" + \"bar\" );" +
                "end";

        m.addRow(new String[]{"foo", "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionFreeFormLineFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FreeFormLine ffl = new FreeFormLine();
        ffl.setText("System.println( \"@{f1}\" + \"@{f2}\" );");
        m.addRhsItem(ffl);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "then\n" +
                "end";

        m.addRow(new String[]{"foo", null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionFreeFormLineSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FreeFormLine ffl = new FreeFormLine();
        ffl.setText("System.println( \"@{f1}\" + \"@{f2}\" );");
        m.addRhsItem(ffl);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "then\n" +
                "end";

        m.addRow(new String[]{null, "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionInsertFactBothValues() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionInsertFact aif = new ActionInsertFact("Present");
        aif.setBoundName("f0");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setField("field1");
        afv0.setValue("$f1");
        aif.addFieldValue(afv0);
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("field2");
        afv1.setValue("$f2");
        aif.addFieldValue(afv1);

        m.addRhsItem(aif);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "$p : Person()\n" +
                "then\n" +
                "Present f0 = new Present();\n" +
                "f0.setField1(\"foo\");\n" +
                "f0.setField2(\"bar\");\n" +
                "insert(f0);\n" +
                "end";

        m.addRow(new String[]{"foo", "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionInsertFactZeroValues() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionInsertFact aif = new ActionInsertFact("Present");
        aif.setBoundName("f0");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setField("field1");
        afv0.setValue("$f1");
        aif.addFieldValue(afv0);
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("field2");
        afv1.setValue("$f2");
        aif.addFieldValue(afv1);

        m.addRhsItem(aif);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "$p : Person()\n" +
                "then\n" +
                "Present f0 = new Present();\n" +
                "insert(f0);\n" +
                "end";

        m.addRow(new String[]{null, null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionInsertFactFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionInsertFact aif = new ActionInsertFact("Present");
        aif.setBoundName("f0");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setField("field1");
        afv0.setValue("$f1");
        aif.addFieldValue(afv0);
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("field2");
        afv1.setValue("$f2");
        aif.addFieldValue(afv1);

        m.addRhsItem(aif);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "$p : Person()\n" +
                "then\n" +
                "Present f0 = new Present();\n" +
                "f0.setField1(\"foo\");\n" +
                "insert(f0);\n" +
                "end";

        m.addRow(new String[]{"foo", null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionInsertFactSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionInsertFact aif = new ActionInsertFact("Present");
        aif.setBoundName("f0");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setField("field1");
        afv0.setValue("$f1");
        aif.addFieldValue(afv0);
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("field2");
        afv1.setValue("$f2");
        aif.addFieldValue(afv1);

        m.addRhsItem(aif);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "$p : Person()\n" +
                "then\n" +
                "Present f0 = new Present();\n" +
                "f0.setField2(\"bar\");\n" +
                "insert(f0);\n" +
                "end";

        m.addRow(new String[]{null, "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionUpdateFactBothValues() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionSetField asf = new ActionSetField("$p");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setField("field1");
        afv0.setValue("$f1");
        asf.addFieldValue(afv0);
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("field2");
        afv1.setValue("$f2");
        asf.addFieldValue(afv1);

        m.addRhsItem(asf);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "$p : Person()\n" +
                "then\n" +
                "$p.setField1(\"foo\");\n" +
                "$p.setField2(\"bar\");\n" +
                "end";

        m.addRow(new String[]{"foo", "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionUpdateFactZeroValues() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionSetField asf = new ActionSetField("$p");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setField("field1");
        afv0.setValue("$f1");
        asf.addFieldValue(afv0);
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("field2");
        afv1.setValue("$f2");
        asf.addFieldValue(afv1);

        m.addRhsItem(asf);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "$p : Person()\n" +
                "then\n" +
                "end";

        m.addRow(new String[]{null, null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionUpdateFactFirstValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionSetField asf = new ActionSetField("$p");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setField("field1");
        afv0.setValue("$f1");
        asf.addFieldValue(afv0);
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("field2");
        afv1.setValue("$f2");
        asf.addFieldValue(afv1);

        m.addRhsItem(asf);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "$p : Person()\n" +
                "then\n" +
                "$p.setField1(\"foo\");\n" +
                "end";

        m.addRow(new String[]{"foo", null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionUpdateFactSecondValue() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionSetField asf = new ActionSetField("$p");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setField("field1");
        afv0.setValue("$f1");
        asf.addFieldValue(afv0);
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("field2");
        afv1.setValue("$f2");
        asf.addFieldValue(afv1);

        m.addRhsItem(asf);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "$p : Person()\n" +
                "then\n" +
                "$p.setField2(\"bar\");\n" +
                "end";

        m.addRow(new String[]{null, "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testEmptyFreeForm() {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1058247
        TemplateModel m = new TemplateModel();
        m.name = "Empty FreeFormLine";

        FreeFormLine fl = new FreeFormLine();
        m.addLhsItem(fl);

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");
        sfc1.setOperator("==");
        fp.addConstraint(sfc1);
        m.addLhsItem(fp);

        FreeFormLine fr = new FreeFormLine();
        m.addRhsItem(fr);

        ActionSetField asf = new ActionSetField("$p");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setType(DataType.TYPE_STRING);
        afv0.setField("field1");
        afv0.setValue("$asf1");
        asf.addFieldValue(afv0);
        m.addRhsItem(asf);

        String expected = "rule \"Empty FreeFormLine_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "$p : Person( field1 == \"foo\" )\n" +
                "then\n" +
                "$p.setField1(\"bar\");\n" +
                "end";

        m.addRow(new String[]{"foo", "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionModifyTwoFieldsFirstTemplateSecondTemplate1() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionUpdateField auf1 = new ActionUpdateField("$p");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setField("field1");
        afv0.setValue("$f1");
        auf1.addFieldValue(afv0);
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("field2");
        afv1.setValue("$f2");
        auf1.addFieldValue(afv1);

        m.addRhsItem(auf1);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify( $p ) {\n" +
                "    setField1(\"foo\"),\n" +
                "    setField2(\"bar\")\n" +
                "  }\n" +
                "end";

        m.addRow(new String[]{"foo", "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionModifyTwoFieldsFirstTemplateSecondTemplate2() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionUpdateField auf1 = new ActionUpdateField("$p");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setField("field1");
        afv0.setValue("$f1");
        auf1.addFieldValue(afv0);
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("field2");
        afv1.setValue("$f2");
        auf1.addFieldValue(afv1);

        m.addRhsItem(auf1);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify( $p ) {\n" +
                "    setField1(\"foo\")\n" +
                "  }\n" +
                "end";

        m.addRow(new String[]{"foo", null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionModifyTwoFieldsFirstTemplateSecondTemplate3() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionUpdateField auf1 = new ActionUpdateField("$p");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setField("field1");
        afv0.setValue("$f1");
        auf1.addFieldValue(afv0);
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("field2");
        afv1.setValue("$f2");
        auf1.addFieldValue(afv1);

        m.addRhsItem(auf1);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify( $p ) {\n" +
                "    setField2(\"bar\")\n" +
                "  }\n" +
                "end";

        m.addRow(new String[]{null, "bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionModifyTwoFieldsFirstTemplateSecondLiteral1() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionUpdateField auf1 = new ActionUpdateField("$p");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setField("field1");
        afv0.setValue("$f1");
        auf1.addFieldValue(afv0);
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_LITERAL);
        afv1.setField("field2");
        afv1.setValue("bar");
        auf1.addFieldValue(afv1);

        m.addRhsItem(auf1);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify( $p ) {\n" +
                "    setField1(\"foo\"),\n" +
                "    setField2(\"bar\")\n" +
                "  }\n" +
                "end";

        m.addRow(new String[]{"foo"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionModifyTwoFieldsFirstTemplateSecondLiteral2() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionUpdateField auf1 = new ActionUpdateField("$p");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setField("field1");
        afv0.setValue("$f1");
        auf1.addFieldValue(afv0);
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_LITERAL);
        afv1.setField("field2");
        afv1.setValue("bar");
        auf1.addFieldValue(afv1);

        m.addRhsItem(auf1);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify( $p ) {\n" +
                "    setField2(\"bar\")\n" +
                "  }\n" +
                "end";

        m.addRow(new String[]{null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionModifyTwoFieldsFirstLiteralSecondTemplate1() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionUpdateField auf1 = new ActionUpdateField("$p");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_LITERAL);
        afv0.setField("field1");
        afv0.setValue("foo");
        auf1.addFieldValue(afv0);
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("field2");
        afv1.setValue("$f2");
        auf1.addFieldValue(afv1);

        m.addRhsItem(auf1);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify( $p ) {\n" +
                "    setField1(\"foo\"),\n" +
                "    setField2(\"bar\")\n" +
                "  }\n" +
                "end";

        m.addRow(new String[]{"bar"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testActionModifyTwoFieldsFirstLiteralSecondTemplate2() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionUpdateField auf1 = new ActionUpdateField("$p");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_LITERAL);
        afv0.setField("field1");
        afv0.setValue("foo");
        auf1.addFieldValue(afv0);
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("field2");
        afv1.setValue("$f2");
        auf1.addFieldValue(afv1);

        m.addRhsItem(auf1);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify( $p ) {\n" +
                "    setField1(\"foo\")\n" +
                "  }\n" +
                "end";

        m.addRow(new String[]{null});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testUpdateModifyMultipleFieldsWithMultipleSkipped1() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionUpdateField auf1 = new ActionUpdateField("$p");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setField("field1");
        afv0.setValue("$f1");
        auf1.addFieldValue(afv0);

        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("field2");
        afv1.setValue("$f2");
        auf1.addFieldValue(afv1);

        ActionFieldValue afv2 = new ActionFieldValue();
        afv2.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv2.setField("field3");
        afv2.setValue("$f3");
        auf1.addFieldValue(afv2);

        m.addRhsItem(auf1);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify( $p ) {\n" +
                "    setField1(\"v1\"),\n" +
                "    setField2(\"v2\"),\n" +
                "    setField3(\"v3\")\n" +
                "  }\n" +
                "end";

        m.addRow(new String[]{"v1", "v2", "v3"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testUpdateModifyMultipleFieldsWithMultipleSkipped2() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionUpdateField auf1 = new ActionUpdateField("$p");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setField("field1");
        afv0.setValue("$f1");
        auf1.addFieldValue(afv0);

        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("field2");
        afv1.setValue("$f2");
        auf1.addFieldValue(afv1);

        ActionFieldValue afv2 = new ActionFieldValue();
        afv2.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv2.setField("field3");
        afv2.setValue("$f3");
        auf1.addFieldValue(afv2);

        m.addRhsItem(auf1);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify( $p ) {\n" +
                "    setField2(\"v2\"),\n" +
                "    setField3(\"v3\")\n" +
                "  }\n" +
                "end";

        m.addRow(new String[]{null, "v2", "v3"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testUpdateModifyMultipleFieldsWithMultipleSkipped3() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionUpdateField auf1 = new ActionUpdateField("$p");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setField("field1");
        afv0.setValue("$f1");
        auf1.addFieldValue(afv0);

        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("field2");
        afv1.setValue("$f2");
        auf1.addFieldValue(afv1);

        ActionFieldValue afv2 = new ActionFieldValue();
        afv2.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv2.setField("field3");
        afv2.setValue("$f3");
        auf1.addFieldValue(afv2);

        m.addRhsItem(auf1);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify( $p ) {\n" +
                "    setField3(\"v3\")\n" +
                "  }\n" +
                "end";

        m.addRow(new String[]{null, null, "v3"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testUpdateModifyMultipleFieldsWithMultipleSkipped4() {
        TemplateModel m = new TemplateModel();
        m.name = "r1";

        FactPattern fp = new FactPattern("Person");
        fp.setBoundName("$p");
        m.addLhsItem(fp);

        ActionUpdateField auf1 = new ActionUpdateField("$p");
        ActionFieldValue afv0 = new ActionFieldValue();
        afv0.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv0.setField("field1");
        afv0.setValue("$f1");
        auf1.addFieldValue(afv0);

        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("field2");
        afv1.setValue("$f2");
        auf1.addFieldValue(afv1);

        ActionFieldValue afv2 = new ActionFieldValue();
        afv2.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv2.setField("field3");
        afv2.setValue("$f3");
        auf1.addFieldValue(afv2);

        m.addRhsItem(auf1);

        String expected = "rule \"r1_0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify( $p ) {\n" +
                "    setField1(\"v1\"),\n" +
                "    setField3(\"v3\")\n" +
                "  }\n" +
                "end";

        m.addRow(new String[]{"v1", null, "v3"});

        checkMarshall(expected,
                      m);
    }

    @Test
    public void testLHSNonEmptyStringValues() {

        FactPattern fp = new FactPattern("Smurf");
        fp.setBoundName("p1");

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setOperator("==");
        sfc1.setFactType("Smurf");
        sfc1.setFieldName("name");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");

        SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setOperator("==");
        sfc2.setFactType("Smurf");
        sfc2.setFieldName("age");
        sfc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setValue("$f2");

        fp.addConstraint(sfc1);
        fp.addConstraint(sfc2);

        //Test 1
        TemplateModel m1 = new TemplateModel();
        m1.addLhsItem(fp);
        m1.name = "r1";

        m1.addRow(new String[]{null, null});

        final String expected1 = "rule \"r1_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end";

        checkMarshall(expected1,
                      m1);

        //Test 2
        TemplateModel m2 = new TemplateModel();
        m2.addLhsItem(fp);
        m2.name = "r2";

        m2.addRow(new String[]{"   ", "35"});

        final String expected2 = "rule \"r2_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( age == 35 )\n" +
                "  then\n" +
                "end";

        checkMarshall(expected2,
                      m2);

        //Test 3
        TemplateModel m3 = new TemplateModel();
        m3.addLhsItem(fp);
        m3.name = "r3";

        m3.addRow(new String[]{"", null});

        final String expected3 = "rule \"r3_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end";

        checkMarshall(expected3,
                      m3);

        //Test 4
        TemplateModel m4 = new TemplateModel();
        m4.addLhsItem(fp);
        m4.name = "r4";

        m4.addRow(new String[]{"", "35"});

        final String expected4 = "rule \"r4_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( age == 35 )\n" +
                "  then\n" +
                "end";

        checkMarshall(expected4,
                      m4);
    }

    @Test
    public void testLHSDelimitedNonEmptyStringValues() {

        FactPattern fp = new FactPattern("Smurf");
        fp.setBoundName("p1");

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setOperator("==");
        sfc1.setFactType("Smurf");
        sfc1.setFieldName("name");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");

        SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setOperator("==");
        sfc2.setFactType("Smurf");
        sfc2.setFieldName("age");
        sfc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setValue("$f2");

        fp.addConstraint(sfc1);
        fp.addConstraint(sfc2);

        //Test 1
        TemplateModel m1 = new TemplateModel();
        m1.addLhsItem(fp);
        m1.name = "r1";

        m1.addRow(new String[]{null, null});

        final String expected1 = "rule \"r1_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end";

        checkMarshall(expected1,
                      m1);

        //Test 2
        TemplateModel m2 = new TemplateModel();
        m2.addLhsItem(fp);
        m2.name = "r2";

        m2.addRow(new String[]{"\"   \"", "35"});

        final String expected2 = "rule \"r2_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( name == \"   \", age == 35 )\n" +
                "  then\n" +
                "end";

        checkMarshall(expected2,
                      m2);

        //Test 3
        TemplateModel m3 = new TemplateModel();
        m3.addLhsItem(fp);
        m3.name = "r3";

        m3.addRow(new String[]{"\"\"", null});

        final String expected3 = "rule \"r3_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( name == \"\" )\n" +
                "  then\n" +
                "end";

        checkMarshall(expected3,
                      m3);

        //Test 4
        TemplateModel m4 = new TemplateModel();
        m4.addLhsItem(fp);
        m4.name = "r4";

        m4.addRow(new String[]{"\"\"", "35"});

        final String expected4 = "rule \"r4_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( name == \"\", age == 35 )\n" +
                "  then\n" +
                "end";

        checkMarshall(expected4,
                      m4);
    }

    @Test
    public void testRHSNonEmptyStringValues() {
        FactPattern fp = new FactPattern("Smurf");
        fp.setBoundName("p1");

        ActionUpdateField auf1 = new ActionUpdateField("p1");
        auf1.addFieldValue(new ActionFieldValue("name",
                                                "$name",
                                                DataType.TYPE_STRING));
        auf1.getFieldValues()[0].setNature(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        ActionUpdateField auf2 = new ActionUpdateField("p1");
        auf2.addFieldValue(new ActionFieldValue("age",
                                                "$age",
                                                DataType.TYPE_NUMERIC_INTEGER));
        auf2.getFieldValues()[0].setNature(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        //Test 1
        TemplateModel m1 = new TemplateModel();
        m1.addLhsItem(fp);
        m1.addRhsItem(auf1);
        m1.addRhsItem(auf2);
        m1.name = "r1";

        m1.addRow(new String[]{null, null});

        final String expected1 = "rule \"r1_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf()\n" +
                "  then\n" +
                "end";

        checkMarshall(expected1,
                      m1);

        //Test 2
        TemplateModel m2 = new TemplateModel();
        m2.addLhsItem(fp);
        m2.addRhsItem(auf1);
        m2.addRhsItem(auf2);
        m2.name = "r2";

        m2.addRow(new String[]{"   ", "35"});

        final String expected2 = "rule \"r2_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf()\n" +
                "  then\n" +
                "    modify( p1 ) {\n" +
                "      setAge( 35 )\n" +
                "    }\n" +
                "end";

        checkMarshall(expected2,
                      m2);

        //Test 3
        TemplateModel m3 = new TemplateModel();
        m3.addLhsItem(fp);
        m3.addRhsItem(auf1);
        m3.addRhsItem(auf2);
        m3.name = "r3";

        m3.addRow(new String[]{"", null});

        final String expected3 = "rule \"r3_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf()\n" +
                "  then\n" +
                "end";

        checkMarshall(expected3,
                      m3);

        //Test 4
        TemplateModel m4 = new TemplateModel();
        m4.addLhsItem(fp);
        m4.addRhsItem(auf1);
        m4.addRhsItem(auf2);
        m4.name = "r4";

        m4.addRow(new String[]{"", "35"});

        final String expected4 = "rule \"r4_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf()\n" +
                "  then\n" +
                "    modify( p1 ) {\n" +
                "      setAge( 35 )\n" +
                "    }\n" +
                "end";

        checkMarshall(expected4,
                      m4);
    }

    @Test
    public void testRHSDelimitedNonEmptyStringValues() {
        FactPattern fp = new FactPattern("Smurf");
        fp.setBoundName("p1");

        ActionUpdateField auf1 = new ActionUpdateField("p1");
        auf1.addFieldValue(new ActionFieldValue("name",
                                                "$name",
                                                DataType.TYPE_STRING));
        auf1.getFieldValues()[0].setNature(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        ActionUpdateField auf2 = new ActionUpdateField("p1");
        auf2.addFieldValue(new ActionFieldValue("age",
                                                "$age",
                                                DataType.TYPE_NUMERIC_INTEGER));
        auf2.getFieldValues()[0].setNature(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        //Test 1
        TemplateModel m1 = new TemplateModel();
        m1.addLhsItem(fp);
        m1.addRhsItem(auf1);
        m1.addRhsItem(auf2);
        m1.name = "r1";

        m1.addRow(new String[]{null, null});

        final String expected1 = "rule \"r1_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf()\n" +
                "  then\n" +
                "end";

        checkMarshall(expected1,
                      m1);

        //Test 2
        TemplateModel m2 = new TemplateModel();
        m2.addLhsItem(fp);
        m2.addRhsItem(auf1);
        m2.addRhsItem(auf2);
        m2.name = "r2";

        m2.addRow(new String[]{"\"   \"", "35"});

        final String expected2 = "rule \"r2_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf()\n" +
                "  then\n" +
                "    modify( p1 ) {\n" +
                "      setName( \"   \" ),\n" +
                "      setAge( 35 )\n" +
                "    }\n" +
                "end";

        checkMarshall(expected2,
                      m2);

        //Test 3
        TemplateModel m3 = new TemplateModel();
        m3.addLhsItem(fp);
        m3.addRhsItem(auf1);
        m3.addRhsItem(auf2);
        m3.name = "r3";

        m3.addRow(new String[]{"\"\"", null});

        final String expected3 = "rule \"r3_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf()\n" +
                "  then\n" +
                "    modify( p1 ) {\n" +
                "      setName( \"\" )\n" +
                "    }\n" +
                "end";

        checkMarshall(expected3,
                      m3);

        //Test 4
        TemplateModel m4 = new TemplateModel();
        m4.addLhsItem(fp);
        m4.addRhsItem(auf1);
        m4.addRhsItem(auf2);
        m4.name = "r4";

        m4.addRow(new String[]{"\"\"", "35"});

        final String expected4 = "rule \"r4_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf()\n" +
                "  then\n" +
                "    modify( p1 ) {\n" +
                "      setName( \"\" ),\n" +
                "      setAge( 35 )\n" +
                "    }\n" +
                "end";

        checkMarshall(expected4,
                      m4);
    }

    @Test
    public void checkLHSConstraintSeparatorWithTemplateKeyAndLiteral() {
        FactPattern fp = new FactPattern("Smurf");
        fp.setBoundName("p1");

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setOperator("==");
        sfc1.setFactType("Smurf");
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");

        SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setOperator("==");
        sfc2.setFactType("Smurf");
        sfc2.setFieldName("field1");
        sfc2.setFieldType(DataType.TYPE_STRING);
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setValue("$f2");

        SingleFieldConstraint sfc3 = new SingleFieldConstraint();
        sfc3.setOperator("==");
        sfc3.setFactType("Smurf");
        sfc3.setFieldName("field1");
        sfc3.setFieldType(DataType.TYPE_STRING);
        sfc3.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        sfc3.setValue("value");

        fp.addConstraint(sfc1);
        fp.addConstraint(sfc2);
        fp.addConstraint(sfc3);

        //Test 1
        TemplateModel m1 = new TemplateModel();
        m1.addLhsItem(fp);
        m1.name = "r1";

        m1.addRow(new String[]{null, null});

        final String expected1 = "rule \"r1_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( field1 == \"value\" )\n" +
                "  then\n" +
                "end";

        checkMarshall(expected1,
                      m1);

        //Test 2
        TemplateModel m2 = new TemplateModel();
        m2.addLhsItem(fp);
        m2.name = "r2";

        m2.addRow(new String[]{"t1", "t2"});

        final String expected2 = "rule \"r2_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( field1 == \"t1\", field1 == \"t2\", field1 == \"value\" )\n" +
                "  then\n" +
                "end";

        checkMarshall(expected2,
                      m2);

        //Test 3
        TemplateModel m3 = new TemplateModel();
        m3.addLhsItem(fp);
        m3.name = "r3";

        m3.addRow(new String[]{"t1", null});

        final String expected3 = "rule \"r3_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( field1 == \"t1\", field1 == \"value\" )\n" +
                "  then\n" +
                "end";

        checkMarshall(expected3,
                      m3);

        //Test 4
        TemplateModel m4 = new TemplateModel();
        m4.addLhsItem(fp);
        m4.name = "r4";

        m4.addRow(new String[]{null, "t2"});

        final String expected4 = "rule \"r4_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( field1 == \"t2\", field1 == \"value\" )\n" +
                "  then\n" +
                "end";

        checkMarshall(expected4,
                      m4);
    }

    @Test
    public void checkLHSConstraintSeparatorWithEmptyTemplateKeyAndLiteralAndNonEmptyTemplateKey() {
        FactPattern fp = new FactPattern("Smurf");
        fp.setBoundName("p1");

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setOperator("==");
        sfc1.setFactType("Smurf");
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue("$f1");

        SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setOperator("==");
        sfc2.setFactType("Smurf");
        sfc2.setFieldName("field1");
        sfc2.setFieldType(DataType.TYPE_STRING);
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        sfc2.setValue("value");

        SingleFieldConstraint sfc3 = new SingleFieldConstraint();
        sfc3.setOperator("==");
        sfc3.setFactType("Smurf");
        sfc3.setFieldName("field1");
        sfc3.setFieldType(DataType.TYPE_STRING);
        sfc3.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc3.setValue("$f2");

        fp.addConstraint(sfc1);
        fp.addConstraint(sfc2);
        fp.addConstraint(sfc3);

        //Test 1
        TemplateModel m1 = new TemplateModel();
        m1.addLhsItem(fp);
        m1.name = "r1";

        m1.addRow(new String[]{null, null});

        final String expected1 = "rule \"r1_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( field1 == \"value\" )\n" +
                "  then\n" +
                "end";

        checkMarshall(expected1,
                      m1);

        //Test 2
        TemplateModel m2 = new TemplateModel();
        m2.addLhsItem(fp);
        m2.name = "r2";

        m2.addRow(new String[]{"t1", "t2"});

        final String expected2 = "rule \"r2_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( field1 == \"t1\", field1 == \"value\", field1 == \"t2\" )\n" +
                "  then\n" +
                "end";

        checkMarshall(expected2,
                      m2);

        //Test 3
        TemplateModel m3 = new TemplateModel();
        m3.addLhsItem(fp);
        m3.name = "r3";

        m3.addRow(new String[]{"t1", null});

        final String expected3 = "rule \"r3_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( field1 == \"t1\", field1 == \"value\" )\n" +
                "  then\n" +
                "end";

        checkMarshall(expected3,
                      m3);

        //Test 4
        TemplateModel m4 = new TemplateModel();
        m4.addLhsItem(fp);
        m4.name = "r4";

        m4.addRow(new String[]{null, "t2"});

        final String expected4 = "rule \"r4_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( field1 == \"value\", field1 == \"t2\" )\n" +
                "  then\n" +
                "end";

        checkMarshall(expected4,
                      m4);
    }

    @Test
    public void checkLHSMultipleFactPatternsWhenPattern1LiteralPattern2Template() {
        FactPattern fp1 = new FactPattern("Smurf");
        fp1.setBoundName("p1");

        SingleFieldConstraint p1sfc1 = new SingleFieldConstraint();
        p1sfc1.setOperator("==");
        p1sfc1.setFactType("Smurf");
        p1sfc1.setFieldName("field1");
        p1sfc1.setFieldType(DataType.TYPE_STRING);
        p1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        p1sfc1.setValue("value1");

        SingleFieldConstraint p1sfc2 = new SingleFieldConstraint();
        p1sfc2.setOperator("==");
        p1sfc2.setFactType("Smurf");
        p1sfc2.setFieldName("field2");
        p1sfc2.setFieldType(DataType.TYPE_STRING);
        p1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        p1sfc2.setValue("value2");

        fp1.addConstraint(p1sfc1);
        fp1.addConstraint(p1sfc2);

        FactPattern fp2 = new FactPattern("Smurf");
        fp2.setBoundName("p2");

        SingleFieldConstraint p2sfc1 = new SingleFieldConstraint();
        p2sfc1.setOperator("==");
        p2sfc1.setFactType("Smurf");
        p2sfc1.setFieldName("field3");
        p2sfc1.setFieldType(DataType.TYPE_STRING);
        p2sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p2sfc1.setValue("$key");

        fp2.addConstraint(p2sfc1);

        //Test 1
        TemplateModel m1 = new TemplateModel();
        m1.addLhsItem(fp1);
        m1.addLhsItem(fp2);
        m1.name = "r1";

        m1.addRow(new String[]{null});

        final String expected1 = "rule \"r1_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( field1 == \"value1\", field2 == \"value2\" )\n" +
                "  then\n" +
                "end";

        checkMarshall(expected1,
                      m1);

        //Test 2
        TemplateModel m2 = new TemplateModel();
        m2.addLhsItem(fp1);
        m2.addLhsItem(fp2);
        m2.name = "r2";

        m2.addRow(new String[]{"value3"});

        final String expected2 = "rule \"r2_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( field1 == \"value1\", field2 == \"value2\" )\n" +
                "    p2 : Smurf( field3 == \"value3\" )\n" +
                "  then\n" +
                "end";

        checkMarshall(expected2,
                      m2);
    }

    @Test
    public void checkPattern1StrictlyLiteralPattern2StrictlyTemplate() {
        FactPattern fp1 = new FactPattern("Smurf");
        fp1.setBoundName("p1");

        SingleFieldConstraint p1sfc1 = new SingleFieldConstraint();
        p1sfc1.setOperator("==");
        p1sfc1.setFactType("Smurf");
        p1sfc1.setFieldName("field1");
        p1sfc1.setFieldType(DataType.TYPE_STRING);
        p1sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        p1sfc1.setValue("value1");

        SingleFieldConstraint p1sfc2 = new SingleFieldConstraint();
        p1sfc2.setOperator("==");
        p1sfc2.setFactType("Smurf");
        p1sfc2.setFieldName("field2");
        p1sfc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        p1sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_LITERAL);
        p1sfc2.setValue("123");

        ActionUpdateField p1auf1 = new ActionUpdateField("p1");
        p1auf1.addFieldValue(new ActionFieldValue("field1",
                                                  "newValue",
                                                  DataType.TYPE_STRING));
        p1auf1.getFieldValues()[0].setNature(BaseSingleFieldConstraint.TYPE_LITERAL);
        ActionUpdateField p1auf2 = new ActionUpdateField("p1");
        p1auf2.addFieldValue(new ActionFieldValue("field2",
                                                  "12345",
                                                  DataType.TYPE_NUMERIC_INTEGER));
        p1auf2.getFieldValues()[0].setNature(BaseSingleFieldConstraint.TYPE_LITERAL);

        fp1.addConstraint(p1sfc1);
        fp1.addConstraint(p1sfc2);

        FactPattern fp2 = new FactPattern("Smurf");
        fp2.setBoundName("p2");

        SingleFieldConstraint p2sfc1 = new SingleFieldConstraint();
        p2sfc1.setOperator("==");
        p2sfc1.setFactType("Smurf");
        p2sfc1.setFieldName("field1");
        p2sfc1.setFieldType(DataType.TYPE_STRING);
        p2sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p2sfc1.setValue("$oldField1");

        SingleFieldConstraint p2sfc2 = new SingleFieldConstraint();
        p2sfc2.setOperator("==");
        p2sfc2.setFactType("Smurf");
        p2sfc2.setFieldName("field2");
        p2sfc2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        p2sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        p2sfc2.setValue("$oldField2");

        ActionUpdateField p2auf1 = new ActionUpdateField("p2");
        p2auf1.addFieldValue(new ActionFieldValue("field1",
                                                  "$newField1",
                                                  DataType.TYPE_STRING));
        p2auf1.getFieldValues()[0].setNature(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        ActionUpdateField p2auf2 = new ActionUpdateField("p2");
        p2auf2.addFieldValue(new ActionFieldValue("field2",
                                                  "$newField2",
                                                  DataType.TYPE_NUMERIC_INTEGER));
        p2auf2.getFieldValues()[0].setNature(BaseSingleFieldConstraint.TYPE_TEMPLATE);

        fp2.addConstraint(p2sfc1);
        fp2.addConstraint(p2sfc2);

        //Test 1
        TemplateModel m1 = new TemplateModel();
        m1.addLhsItem(fp1);
        m1.addLhsItem(fp2);
        m1.addRhsItem(p1auf1);
        m1.addRhsItem(p1auf2);
        m1.addRhsItem(p2auf1);
        m1.addRhsItem(p2auf2);
        m1.name = "r1";

        m1.addRow(new String[]{null, null, null, null});

        final String expected1 = "rule \"r1_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( field1 == \"value1\", field2 == 123 )\n" +
                "  then\n" +
                "    modify( p1 ) {" +
                "       setField1( \"newValue\" )," +
                "       setField2( 12345 )" +
                "    }" +
                "end";

        checkMarshall(expected1,
                      m1);

        m1.addRow(new String[]{"abc", "0", "def", "1"});

        final String expected2 = "rule \"r1_1\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( field1 == \"value1\", field2 == 123 )\n" +
                "    p2 : Smurf( field1 == \"abc\", field2 == 0 )\n" +
                "  then\n" +
                "    modify( p1 ) {" +
                "       setField1( \"newValue\" )," +
                "       setField2( 12345 )" +
                "    }" +
                "    modify( p2 ) {" +
                "       setField1( \"def\" )," +
                "       setField2( 1 )" +
                "    }" +
                "end";

        checkMarshall(expected1 + expected2,
                      m1);
    }

    @Test
    public void checkOneTemplateForDifferentFields() {
        String templateKeyOne = "$template_key1";
        String templateKeyTwo = "$template_key2";

        FactPattern fp = new FactPattern("Smurf");
        fp.setBoundName("p1");

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setOperator("==");
        sfc1.setFactType("Smurf");
        sfc1.setFieldName("field1");
        sfc1.setFieldType(DataType.TYPE_STRING);
        sfc1.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setValue(templateKeyOne);

        SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setOperator("==");
        sfc2.setFactType("Smurf");
        sfc2.setFieldName("field2");
        sfc2.setFieldType(DataType.TYPE_STRING);
        sfc2.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setValue(templateKeyOne);

        SingleFieldConstraint sfc3 = new SingleFieldConstraint();
        sfc3.setOperator("==");
        sfc3.setFactType("Smurf");
        sfc3.setFieldName("field3");
        sfc3.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        sfc3.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        sfc3.setValue(templateKeyTwo);

        fp.addConstraint(sfc1);
        fp.addConstraint(sfc2);
        fp.addConstraint(sfc3);

        //Test 1
        TemplateModel model = new TemplateModel();
        model.addLhsItem(fp);
        model.name = "r1";

        model.addRow(new String[]{"value one", "2"});

        final String expected = "rule \"r1_0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( field1 == \"value one\", field2 == \"value one\", field3 == 2 )\n" +
                "  then\n" +
                "end";

        checkMarshall(expected, model);
    }

    private void assertEqualsIgnoreWhitespace(final String expected,
                                              final String actual) {
        Assertions.assertThat(expected).isEqualToIgnoringWhitespace(actual);
    }
}
