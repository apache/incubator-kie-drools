/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.commons.backend.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.MethodInfo;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RuleModelDRLPersistenceUnmarshallingI18NTest {

    private PackageDataModelOracle dmo;
    private Map<String, ModelField[]> packageModelFields = new HashMap<String, ModelField[]>();
    private Map<String, String[]> projectJavaEnumDefinitions = new HashMap<String, String[]>();
    private Map<String, List<MethodInfo>> projectMethodInformation = new HashMap<String, List<MethodInfo>>();

    @Before
    public void setUp() throws Exception {
        dmo = mock(PackageDataModelOracle.class);
        when(dmo.getModuleModelFields()).thenReturn(packageModelFields);
        when(dmo.getModuleJavaEnumDefinitions()).thenReturn(projectJavaEnumDefinitions);
        when(dmo.getModuleMethodInformation()).thenReturn(projectMethodInformation);
    }

    @After
    public void cleanUp() throws Exception {
        packageModelFields.clear();
        projectJavaEnumDefinitions.clear();
        projectMethodInformation.clear();
    }

    private void addModelField(final String factName,
                               final String fieldName,
                               final String clazz,
                               final String type) {
        ModelField[] modelFields = new ModelField[1];
        modelFields[0] = new ModelField(fieldName,
                                        clazz,
                                        ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                        ModelField.FIELD_ORIGIN.DECLARED,
                                        FieldAccessorsAndMutators.BOTH,
                                        type);
        if (packageModelFields.containsKey(factName)) {
            final List<ModelField> existingModelFields = new ArrayList<ModelField>(Arrays.asList(packageModelFields.get(factName)));
            existingModelFields.add(modelFields[0]);
            modelFields = existingModelFields.toArray(modelFields);
        }
        packageModelFields.put(factName,
                               modelFields);
    }

    @Test
    public void testI18N_US_InsertFact() {
        final String drl = "package org.test;\n" +
                "rule \"r1\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "then\n" +
                "Applicant fact0 = new Applicant();\n" +
                "fact0.setAge( 55 );\n" +
                "insert( fact0 );\n" +
                "end";

        addModelField("Applicant",
                      "age",
                      "java.lang.Integer",
                      DataType.TYPE_NUMERIC_INTEGER);

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl,
                                                                                new ArrayList<String>(),
                                                                                dmo);

        assertNotNull(m);

        assertEquals(1,
                     m.rhs.length);
        assertTrue(m.rhs[0] instanceof ActionInsertFact);
        final ActionInsertFact aif = (ActionInsertFact) m.rhs[0];
        assertEquals("Applicant",
                     aif.getFactType());
        assertEquals("fact0",
                     aif.getBoundName());

        assertEquals(1,
                     aif.getFieldValues().length);
        final ActionFieldValue afv = aif.getFieldValues()[0];
        assertEquals("age",
                     afv.getField());
        assertEquals("55",
                     afv.getValue());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     afv.getType());
        assertEquals(FieldNatureType.TYPE_LITERAL,
                     afv.getNature());
    }

    @Test
    public void testI18N_JP_InsertFact() {
        final String drl = "package org.test;\n" +
                "rule \"r1\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "then\n" +
                "帽子 fact0 = new 帽子();\n" +
                "fact0.setサイズ( 55 );\n" +
                "insert( fact0 );\n" +
                "end";

        addModelField("帽子",
                      "サイズ",
                      "java.lang.Integer",
                      DataType.TYPE_NUMERIC_INTEGER);

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl,
                                                                                new ArrayList<String>(),
                                                                                dmo);

        assertNotNull(m);

        assertEquals(1,
                     m.rhs.length);
        assertTrue(m.rhs[0] instanceof ActionInsertFact);
        final ActionInsertFact aif = (ActionInsertFact) m.rhs[0];
        assertEquals("帽子",
                     aif.getFactType());
        assertEquals("fact0",
                     aif.getBoundName());

        assertEquals(1,
                     aif.getFieldValues().length);
        final ActionFieldValue afv = aif.getFieldValues()[0];
        assertEquals("サイズ",
                     afv.getField());
        assertEquals("55",
                     afv.getValue());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     afv.getType());
        assertEquals(FieldNatureType.TYPE_LITERAL,
                     afv.getNature());
    }

    @Test
    public void testI18N_US_BoundField() {
        final String drl = "package org.test;\n" +
                "rule \"r1\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Applicant( $a : age )\n" +
                "then\n" +
                "end";

        addModelField("Applicant",
                      "age",
                      "java.lang.Integer",
                      DataType.TYPE_NUMERIC_INTEGER);

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl,
                                                                                new ArrayList<String>(),
                                                                                dmo);

        assertNotNull(m);

        assertEquals(1,
                     m.lhs.length);
        assertTrue(m.lhs[0] instanceof FactPattern);
        final FactPattern fp = (FactPattern) m.lhs[0];
        assertEquals("Applicant",
                     fp.getFactType());
        assertEquals(1,
                     fp.getNumberOfConstraints());

        assertTrue(fp.getConstraint(0) instanceof SingleFieldConstraint);
        final SingleFieldConstraint sfc = (SingleFieldConstraint) fp.getConstraint(0);
        assertEquals("age",
                     sfc.getFieldName());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     sfc.getFieldType());
        assertEquals("$a",
                     sfc.getFieldBinding());
    }

    @Test
    public void testI18N_JP_BoundField() {
        final String drl = "package org.test;\n" +
                "rule \"r1\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "Applicant( 製品番号 : age )\n" +
                "then\n" +
                "end";

        addModelField("Applicant",
                      "age",
                      "java.lang.Integer",
                      DataType.TYPE_NUMERIC_INTEGER);

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal(drl,
                                                                                new ArrayList<String>(),
                                                                                dmo);

        assertNotNull(m);

        assertEquals(1,
                     m.lhs.length);
        assertTrue(m.lhs[0] instanceof FactPattern);
        final FactPattern fp = (FactPattern) m.lhs[0];
        assertEquals("Applicant",
                     fp.getFactType());
        assertEquals(1,
                     fp.getNumberOfConstraints());

        assertTrue(fp.getConstraint(0) instanceof SingleFieldConstraint);
        final SingleFieldConstraint sfc = (SingleFieldConstraint) fp.getConstraint(0);
        assertEquals("age",
                     sfc.getFieldName());
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     sfc.getFieldType());
        assertEquals("製品番号",
                     sfc.getFieldBinding());
    }
}
