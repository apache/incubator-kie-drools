/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import org.junit.After;
import org.junit.Before;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.MethodInfo;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;

import static org.mockito.Mockito.*;

public abstract class BaseRuleModelTest {

    protected PackageDataModelOracle dmo;
    protected Map<String, ModelField[]> packageModelFields = new HashMap<>();
    protected Map<String, String[]> projectJavaEnumDefinitions = new HashMap<>();
    protected Map<String, List<MethodInfo>> projectMethodInformation = new HashMap<>();

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

    protected void addModelField(final String factName,
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
            final List<ModelField> existingModelFields = new ArrayList<>(Arrays.asList(packageModelFields.get(factName)));
            existingModelFields.add(modelFields[0]);
            modelFields = existingModelFields.toArray(modelFields);
        }
        packageModelFields.put(factName,
                               modelFields);
    }

    protected void addJavaEnumDefinition(final String factName,
                                         final String fieldName,
                                         final String[] values) {
        final String key = factName + "#" + fieldName;
        projectJavaEnumDefinitions.put(key,
                                       values);
    }

    protected void addMethodInformation(final String factName,
                                        final String name,
                                        final List<String> params,
                                        final String returnType,
                                        final String parametricReturnType,
                                        final String genericType) {
        MethodInfo mi = new MethodInfo(name,
                                       params,
                                       returnType,
                                       parametricReturnType,
                                       genericType);

        List<MethodInfo> existingMethodInfo = projectMethodInformation.get(factName);
        if (existingMethodInfo == null) {
            existingMethodInfo = new ArrayList<>();
            projectMethodInformation.put(factName,
                                         existingMethodInfo);
        }
        existingMethodInfo.add(mi);
    }
}
