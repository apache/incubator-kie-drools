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
package org.drools.workbench.models.commons.backend.oracle;

import static org.junit.Assert.*;
import static org.drools.workbench.models.commons.backend.oracle.ProjectDataModelOracleUtils.getFieldFullyQualifiedClassName;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.ModelField.FIELD_CLASS_TYPE;
import org.drools.workbench.models.datamodel.oracle.ModelField.FIELD_ORIGIN;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.junit.Test;

public class ProjectDataModelOracleUtilsTest {

    @Test
    public void getFieldFullyQualifiedClassNameTest() {

        ProjectDataModelOracle mockedDMO = mock(ProjectDataModelOracle.class);
        Map<String, ModelField[]> projectModelFields = new HashMap<>();

        // non-existent field for unknown class
        when(mockedDMO.getProjectModelFields()).thenReturn(projectModelFields);
        String fullyQualifiedClassName = this.getClass().getName();
        String fieldName = "nonExistentField";

        String fqnFieldClassName  = getFieldFullyQualifiedClassName(mockedDMO, fullyQualifiedClassName, fieldName);
        assertNull( "Expected a null FQN field class name", fqnFieldClassName);

        // non-existent field for known class
        projectModelFields.put(fullyQualifiedClassName, new ModelField[] {
          new ModelField("existentField",
                         String.class.getName(),
                         FIELD_CLASS_TYPE.REGULAR_CLASS,
                         FIELD_ORIGIN.DECLARED,
                         FieldAccessorsAndMutators.ACCESSOR,
                         null)// forgot what goes in here?
        });
        when(mockedDMO.getProjectModelFields()).thenReturn(projectModelFields);

        fqnFieldClassName  = getFieldFullyQualifiedClassName(mockedDMO, fullyQualifiedClassName, fieldName);
        assertNull( "Expected a null FQN field class name", fqnFieldClassName);

        // existent field for known class
        fieldName = "testField";
        String fieldType = "org.acme.test.field.type";
        projectModelFields.put(fullyQualifiedClassName, new ModelField[] {
          new ModelField("existentField",
                         String.class.getName(),
                         FIELD_CLASS_TYPE.REGULAR_CLASS,
                         FIELD_ORIGIN.DECLARED,
                         FieldAccessorsAndMutators.ACCESSOR,
                         null),// forgot what goes in here?
          new ModelField(fieldName,
                         fieldType,
                         FIELD_CLASS_TYPE.REGULAR_CLASS,
                         FIELD_ORIGIN.DECLARED,
                         FieldAccessorsAndMutators.ACCESSOR,
                         null) // forgot what goes in here?
        });
        when(mockedDMO.getProjectModelFields()).thenReturn(projectModelFields);

        fqnFieldClassName  = getFieldFullyQualifiedClassName(mockedDMO, fullyQualifiedClassName, fieldName);
        assertEquals( "Expected a null FQN field class name", fieldType, fqnFieldClassName);
    }
}
