/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.drools.workbench.models.guided.template.shared;

import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.junit.Assert;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.DataType;

import static org.junit.Assert.assertEquals;

public class TemplateModelTest {

    @Test
    public void testAddRowInvalidColumnCount() throws Exception {
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

        try {
            m.addRow(new String[]{});
            Assert.fail("Expected IlegalArgumentException");
        } catch (IllegalArgumentException iae) {
            assertEquals("Invalid numbers of columns: 0 expected: 1", iae.getMessage());
        }
    }

    @Test
    public void sameFieldSameVariable() throws Exception {
        TemplateModel m = new TemplateModel();
        m.name = "t1";
        m.lhs = new IPattern[1];
        FactPattern factPattern = new FactPattern();
        m.lhs[0] = factPattern;
        factPattern.addConstraint(makeConstraint("Applicant", "age", "Integer", "$default", "=="));
        factPattern.addConstraint(makeConstraint("Applicant", "age", "Integer", "$default", "=="));

        assertEquals(1, m.getInterpolationVariablesList().length);
        assertEquals("Integer", m.getInterpolationVariablesList()[0].getDataType());
    }

    @Test
    public void sameVariableFroTwoDifferentTypes() throws Exception {
        TemplateModel m = new TemplateModel();
        m.name = "t1";
        m.lhs = new IPattern[1];
        FactPattern factPattern = new FactPattern();
        m.lhs[0] = factPattern;
        factPattern.addConstraint(makeConstraint("Applicant", "age", "Integer", "$default", "=="));
        factPattern.addConstraint(makeConstraint("Applicant", "name", "String", "$default", "=="));

        assertEquals(1, m.getInterpolationVariablesList().length);
        assertEquals(TemplateModel.DEFAULT_TYPE, m.getInterpolationVariablesList()[0].getDataType());
    }

    @Test
    public void sameVariableFroTwoDifferentOperatorsSameType() throws Exception {
        TemplateModel m = new TemplateModel();
        m.name = "t1";
        m.lhs = new IPattern[1];
        FactPattern factPattern = new FactPattern();
        m.lhs[0] = factPattern;
        factPattern.addConstraint(makeConstraint("Applicant", "age", "Integer", "$default", "=="));
        factPattern.addConstraint(makeConstraint("Applicant", "name", "Integer", "$default", "!="));

        assertEquals(1, m.getInterpolationVariablesList().length);
        assertEquals(TemplateModel.DEFAULT_TYPE, m.getInterpolationVariablesList()[0].getDataType());
    }

    @Test
    public void sameVariableFroTwoDifferentOperatorsDifferentType() throws Exception {
        TemplateModel m = new TemplateModel();
        m.name = "t1";
        m.lhs = new IPattern[1];
        FactPattern factPattern = new FactPattern();
        m.lhs[0] = factPattern;
        factPattern.addConstraint(makeConstraint("Applicant", "age", "Integer", "$default", "=="));
        factPattern.addConstraint(makeConstraint("Applicant", "name", "String", "$default", "!="));

        assertEquals(1, m.getInterpolationVariablesList().length);
        assertEquals(TemplateModel.DEFAULT_TYPE, m.getInterpolationVariablesList()[0].getDataType());
    }

    @Test
    public void separateVariableNames() throws Exception {
        TemplateModel m = new TemplateModel();
        m.name = "t1";
        m.lhs = new IPattern[1];
        FactPattern factPattern = new FactPattern();
        m.lhs[0] = factPattern;
        factPattern.addConstraint(makeConstraint("Applicant", "age", "Integer", "$default", "=="));
        factPattern.addConstraint(makeConstraint("Applicant", "name", "String", "$other", "=="));

        assertEquals(2, m.getInterpolationVariablesList().length);
        assertEquals("Integer", m.getInterpolationVariablesList()[0].getDataType());
        assertEquals("String", m.getInterpolationVariablesList()[1].getDataType());
    }

    private SingleFieldConstraint makeConstraint(final String factType,
                                                 final String fieldName,
                                                 final String fieldType,
                                                 final String variableName,
                                                 final String operator) {
        SingleFieldConstraint constraint = new SingleFieldConstraint(factType,
                                                                     fieldName,
                                                                     fieldType,
                                                                     null);
        constraint.setOperator(operator);
        constraint.setConstraintValueType(SingleFieldConstraint.TYPE_TEMPLATE);
        constraint.setValue(variableName);
        return constraint;
    }
}
