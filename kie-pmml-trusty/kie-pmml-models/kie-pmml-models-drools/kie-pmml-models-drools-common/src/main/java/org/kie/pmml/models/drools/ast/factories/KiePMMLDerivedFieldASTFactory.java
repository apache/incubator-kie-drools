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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dmg.pmml.DerivedField;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

/**
 * Class used to generate <code>KiePMMLDroolsType</code>s out of a <code>DerivedField</code>
 */
public class KiePMMLDerivedFieldASTFactory {

    private final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;

    private KiePMMLDerivedFieldASTFactory(final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        this.fieldTypeMap = fieldTypeMap;
    }

    /**
     * @param fieldTypeMap the <code>Map&lt;String, KiePMMLOriginalTypeGeneratedType&gt;</code> to be populated with mapping between original field' name and <b>original type/generated type</b> tuple
     * @return
     */
    public static KiePMMLDerivedFieldASTFactory factory(final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        return new KiePMMLDerivedFieldASTFactory(fieldTypeMap);
    }

    /**
     * Create a <code>List&lt;KiePMMLDroolsType&gt;</code> out of original <code>List&lt;DerivedField&gt;</code>s,
     * and <b>populate</b> the <b>fieldNameTypeNameMap</b> with mapping between original field' name and <b>original type/generated type</b> tuple
     * @param derivedFields
     */
    public List<KiePMMLDroolsType> declareTypes(final List<DerivedField> derivedFields) {
        return derivedFields.stream().map(this::declareType).collect(Collectors.toList());
    }

    /**
     * Create a <code>KiePMMLDroolsType</code> out of original <code>DerivedField</code>,
     * and <b>populate</b> the <b>fieldNameTypeNameMap</b> with mapping between original field' name and <b>original type/generated type</b> tuple
     * @param derivedField
     */
    public KiePMMLDroolsType declareType(DerivedField derivedField) {
        String generatedType = getSanitizedClassName(derivedField.getName().toUpperCase());
        String fieldName =derivedField.getName();
        String fieldType = derivedField.getDataType().value();
        fieldTypeMap.put(fieldName, new KiePMMLOriginalTypeGeneratedType(fieldType, generatedType));
        return new KiePMMLDroolsType(generatedType, DATA_TYPE.byName(fieldType).getMappedClass().getSimpleName());
    }
}
