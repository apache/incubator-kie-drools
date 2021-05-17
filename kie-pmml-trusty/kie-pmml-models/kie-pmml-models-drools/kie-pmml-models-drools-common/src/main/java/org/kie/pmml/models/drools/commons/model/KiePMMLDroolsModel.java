/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.models.drools.commons.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Supplier;

import org.kie.api.KieBase;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.IsDrools;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.kie.pmml.models.drools.utils.KiePMMLSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.models.drools.utils.KiePMMLAgendaListenerUtils.getAgendaEventListener;

/**
 * KIE representation of PMML model that use <b>drool</b> for implementation
 */
public abstract class KiePMMLDroolsModel extends KiePMMLModel implements IsDrools {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLDroolsModel.class);

    private static final AgendaEventListener agendaEventListener = getAgendaEventListener(logger);

    /**
     * Map between the original field name and the generated type.
     */
    protected Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();

    protected KiePMMLDroolsModel(final String modelName,
                                 final List<KiePMMLExtension> extensions) {
        super(modelName, extensions);
    }

    public Map<String, KiePMMLOriginalTypeGeneratedType> getFieldTypeMap() {
        return fieldTypeMap;
    }

    @Override
    public Object evaluate(final Object knowledgeBase, Map<String, Object> requestData) {
        logger.trace("evaluate {} {}", knowledgeBase, requestData);
        if (!(knowledgeBase instanceof KieBase)) {
            throw new KiePMMLException(String.format("Expecting KieBase, received %s",
                                                     knowledgeBase.getClass().getName()));
        }
        final PMML4Result toReturn = getPMML4Result(targetField);
        String fullClassName = this.getClass().getName();
        String packageName =  fullClassName.contains(".") ? fullClassName.substring(0, fullClassName.lastIndexOf('.')) : "";
        KiePMMLSessionUtils.Builder builder = KiePMMLSessionUtils.builder((KieBase) knowledgeBase, name, packageName,
                                                                          toReturn)
                .withObjectsInSession(requestData, fieldTypeMap)
                .withOutputFieldsMap(outputFieldsMap);
        if (logger.isDebugEnabled()) {
            builder = builder.withAgendaEventListener(agendaEventListener);
        }
        final KiePMMLSessionUtils kiePMMLSessionUtils = builder.build();
        kiePMMLSessionUtils.fireAllRules();
        return toReturn;
    }

    @Override
    public String getKModulePackageName() {
        return getSanitizedPackageName(name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KiePMMLDroolsModel.class.getSimpleName() + "[", "]")
                .add("kiePMMLOutputFields=" + kiePMMLOutputFields)
                .add("fieldTypeMap=" + fieldTypeMap)
                .add("pmmlMODEL=" + pmmlMODEL)
                .add("miningFunction=" + miningFunction)
                .add("targetField='" + targetField + "'")
                .add("outputFieldsMap=" + outputFieldsMap)
                .add("missingValueReplacementMap=" + missingValueReplacementMap)
                .add("name='" + name + "'")
                .add("extensions=" + extensions)
                .add("id='" + id + "'")
                .add("parentId='" + parentId + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KiePMMLDroolsModel that = (KiePMMLDroolsModel) o;
        return Objects.equals(kiePMMLOutputFields, that.kiePMMLOutputFields) &&
                Objects.equals(fieldTypeMap, that.fieldTypeMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kiePMMLOutputFields, fieldTypeMap);
    }

    private PMML4Result getPMML4Result(final String targetField) {
        PMML4Result toReturn = new PMML4Result();
        toReturn.setResultCode(ResultCode.FAIL.getName());
        toReturn.setResultObjectName(targetField);
        return toReturn;
    }

    public abstract static class Builder<T extends KiePMMLDroolsModel> extends KiePMMLModel.Builder<T> {

        protected Builder(String prefix, PMML_MODEL pmmlMODEL, MINING_FUNCTION miningFunction, Supplier<T> supplier) {
            super(prefix, pmmlMODEL, miningFunction, supplier);
        }

        public Builder<T> withFieldTypeMap(Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
            toBuild.fieldTypeMap = fieldTypeMap;
            return this;
        }

        public Builder<T> withOutputFields(List<KiePMMLOutputField> outputFields) {
            toBuild.kiePMMLOutputFields = outputFields;
            return this;
        }
    }
}
