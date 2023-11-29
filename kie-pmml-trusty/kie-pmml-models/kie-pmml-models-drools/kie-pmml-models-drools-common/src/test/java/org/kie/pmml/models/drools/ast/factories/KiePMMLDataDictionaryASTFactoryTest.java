/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.models.drools.ast.factories;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getFieldsFromDataDictionary;
import static org.kie.pmml.models.drools.utils.KiePMMLASTTestUtils.getDottedTypeDataField;
import static org.kie.pmml.models.drools.utils.KiePMMLASTTestUtils.getTypeDataField;

public class KiePMMLDataDictionaryASTFactoryTest {

    @Test
    void declareTypes() {
        List<DataField> dataFields = Arrays.asList(getTypeDataField(), getDottedTypeDataField(), getTypeDataField(), getDottedTypeDataField());
        DataDictionary dataDictionary = new DataDictionary().addDataFields(dataFields.toArray(new org.dmg.pmml.DataField[0]));
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        List<KiePMMLDroolsType> retrieved = KiePMMLDataDictionaryASTFactory.factory(fieldTypeMap).declareTypes(getFieldsFromDataDictionary(dataDictionary));
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSameSizeAs(dataFields);
        IntStream.range(0, dataFields.size()).forEach(i -> commonVerifyTypeDeclarationDescr(dataFields.get(i), fieldTypeMap, retrieved.get(i)));
    }

    @Test
    void declareType() {
        DataField dataField = getTypeDataField();
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        KiePMMLDroolsType retrieved = KiePMMLDataDictionaryASTFactory.factory(fieldTypeMap).declareType(dataField);
        assertThat(retrieved).isNotNull();
        commonVerifyTypeDeclarationDescr(dataField, fieldTypeMap, retrieved);
    }

    private void commonVerifyTypeDeclarationDescr(DataField dataField, Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final KiePMMLDroolsType kiePMMLDroolsType) {
        String expectedGeneratedType = getSanitizedClassName(dataField.getName());
        String expectedMappedOriginalType = DATA_TYPE.byName(dataField.getDataType().value()).getMappedClass().getSimpleName();
        assertThat(kiePMMLDroolsType.getName()).startsWith(expectedGeneratedType);
        assertThat(kiePMMLDroolsType.getType()).isEqualTo(expectedMappedOriginalType);
        assertThat(fieldTypeMap).containsKey(dataField.getName());
        KiePMMLOriginalTypeGeneratedType kiePMMLOriginalTypeGeneratedType = fieldTypeMap.get(dataField.getName());
        assertThat(kiePMMLOriginalTypeGeneratedType.getOriginalType()).isEqualTo(dataField.getDataType().value());
        assertThat(kiePMMLOriginalTypeGeneratedType.getGeneratedType()).startsWith(expectedGeneratedType);
    }
}