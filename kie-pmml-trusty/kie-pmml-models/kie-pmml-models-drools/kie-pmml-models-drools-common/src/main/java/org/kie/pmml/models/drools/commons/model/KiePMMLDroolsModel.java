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
package org.kie.pmml.models.drools.commons.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.pmml.PMML4Result;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoMapInputDTO;
import org.kie.efesto.runtimemanager.api.model.EfestoOriginalTypeGeneratedType;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.identifiers.KiePmmlComponentRoot;
import org.kie.pmml.api.identifiers.LocalComponentIdRedirectPmml;
import org.kie.pmml.api.identifiers.PmmlIdRedirectFactory;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.IsDrools;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.models.drools.executor.KiePMMLStatusHolder;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.runtimemanager.api.utils.SPIUtils.getRuntimeManager;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.OUTPUTFIELDS_MAP_IDENTIFIER;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.PMML4_RESULT_IDENTIFIER;
import static org.kie.pmml.models.drools.utils.KiePMMLAgendaListenerUtils.getAgendaEventListener;

/**
 * KIE representation of PMML model that use <b>drool</b> for implementation
 */
public abstract class KiePMMLDroolsModel extends KiePMMLModel implements IsDrools {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLDroolsModel.class);

    private static final AgendaEventListener agendaEventListener = getAgendaEventListener(logger);
    private static final long serialVersionUID = 5471400949048174357L;

    protected String kModulePackageName;

    /**
     * Map between the original field name and the generated type.
     */
    protected Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();

    protected KiePMMLDroolsModel(final String fileName,
                                 final String modelName,
                                 final List<KiePMMLExtension> extensions) {
        super(fileName, modelName, extensions);
    }

    public Map<String, KiePMMLOriginalTypeGeneratedType> getFieldTypeMap() {
        return fieldTypeMap;
    }

    @Override
    public Object evaluate(final Map<String, Object> requestData,
                           final PMMLRuntimeContext context) {
        logger.trace("evaluate {}", requestData);
        final PMML4Result toReturn = getPMML4Result(targetField);

        List<Object> inserts = List.of(new KiePMMLStatusHolder());
        final Map<String, Object> globals = new HashMap<>();
        globals.put(PMML4_RESULT_IDENTIFIER, toReturn);
        globals.put(OUTPUTFIELDS_MAP_IDENTIFIER, context.getOutputFieldsMap());

        Map<String, EfestoOriginalTypeGeneratedType> convertedFieldTypeMap = fieldTypeMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                                          entry -> new EfestoOriginalTypeGeneratedType(entry.getValue().getOriginalType(),
                                                                                       entry.getValue().getGeneratedType())));
        EfestoMapInputDTO darMapInputDTO = new EfestoMapInputDTO(inserts, globals, requestData, convertedFieldTypeMap
                , this.getName(), this.getKModulePackageName());

        LocalComponentIdRedirectPmml modelLocalUriId = new EfestoAppRoot()
                .get(KiePmmlComponentRoot.class)
                .get(PmmlIdRedirectFactory.class)
                .get("drl", context.getFileNameNoSuffix(), getSanitizedClassName(this.getName()));

        BaseEfestoInput<EfestoMapInputDTO> input = new BaseEfestoInput<>(modelLocalUriId,
                                                                                      darMapInputDTO);

        Optional<RuntimeManager> runtimeManager = getRuntimeManager(true);
        if (runtimeManager.isEmpty()) {
            throw new KieRuntimeServiceException("Cannot find RuntimeManager");
        }

        Collection<EfestoOutput> output = runtimeManager.get().evaluateInput(context, input);
        // TODO manage for different kind of retrieved output
        if (output.isEmpty()) {
            throw new KiePMMLException("Failed to retrieve value for " + this.getName());
        }
        return toReturn;
    }

    @Override
    public String getKModulePackageName() {
        return kModulePackageName;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KiePMMLDroolsModel.class.getSimpleName() + "[", "]")
                .add("kiePMMLOutputFields=" + kiePMMLOutputFields)
                .add("fieldTypeMap=" + fieldTypeMap)
                .add("pmmlMODEL=" + pmmlMODEL)
                .add("miningFunction=" + miningFunction)
                .add("targetField='" + targetField + "'")
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

    @SuppressWarnings("unchecked")
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

    }
}
