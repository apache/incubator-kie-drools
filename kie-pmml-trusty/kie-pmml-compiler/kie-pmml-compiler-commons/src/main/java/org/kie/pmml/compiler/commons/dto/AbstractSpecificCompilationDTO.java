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
package org.kie.pmml.compiler.commons.dto;

import org.dmg.pmml.*;
import org.kie.pmml.api.compilation.PMMLCompilationContext;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.commons.model.KiePMMLMiningField;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.KiePMMLTarget;
import org.kie.pmml.commons.transformations.KiePMMLLocalTransformations;
import org.kie.pmml.commons.transformations.KiePMMLTransformationDictionary;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.commons.factories.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.kie.pmml.api.utils.SourceUtils.dumpSources;

/**
 * Abstract class to be extended by all <b>model-specific</b> compilation dtos
 */
public abstract class AbstractSpecificCompilationDTO<T extends Model> implements CompilationDTO<T> {

    private static final long serialVersionUID = -3691191496300060907L;
    private final CompilationDTO<T> source;
    private final List<Field<?>> fields;
    private List<KiePMMLMiningField> kiePMMLMiningFields;
    private List<KiePMMLOutputField> kiePMMLOutputFields;
    private List<KiePMMLTarget> kiePMMLTargets;
    private KiePMMLTransformationDictionary kiePMMLTransformationDictionary = null;
    private KiePMMLLocalTransformations kiePMMLLocalTransformations = null;

    /**
     * Protected constructor that generate a <code>CommonCompilationDTO</code> preserving given <b>packageName</b>
     * and <b>fields</b>
     *
     * @param pmml
     * @param model
     * @param pmmlContext
     * @param packageName
     */
    protected AbstractSpecificCompilationDTO(final PMML pmml,
                                             final T model,
                                             final PMMLCompilationContext pmmlContext,
                                             final String fileName,
                                             final String packageName,
                                             final List<Field<?>> fields) {
        this(CommonCompilationDTO.fromPackageNameAndFields(pmml, model, pmmlContext, fileName, packageName, fields));
    }

    /**
     * Protected constructor that use given <code>CompilationDTO</code>
     *
     * @param source
     */
    protected AbstractSpecificCompilationDTO(CompilationDTO<T> source) {
        this.source = source;
        this.fields = new ArrayList<>(source.getFields());
    }

    @Override
    public PMML getPmml() {
        return source.getPmml();
    }

    @Override
    public TransformationDictionary getTransformationDictionary() {
        return source.getTransformationDictionary();
    }

    @Override
    public T getModel() {
        return source.getModel();
    }

    @Override
    public MiningSchema getMiningSchema() {
        return source.getMiningSchema();
    }

    @Override
    public MiningFunction getMiningFunction() {
        return source.getMiningFunction();
    }

    @Override
    public LocalTransformations getLocalTransformations() {
        return source.getLocalTransformations();
    }

    @Override
    public Output getOutput() {
        return source.getOutput();
    }

    @Override
    public Targets getTargets() {
        return source.getTargets();
    }

    @Override
    public List<Field<?>> getFields() {
        return Collections.unmodifiableList(fields);
    }

    @Override
    public DataField getTargetDataField() {
        return source.getTargetDataField();
    }

    @Override
    public OpType getOpType() {
        return source.getOpType();
    }

    @Override
    public String getModelName() {
        return source.getModelName();
    }

    @Override
    public String getFileName() {
        return source.getFileName();
    }

    @Override
    public String getTargetFieldName() {
        return source.getTargetFieldName();
    }

    @Override
    public String getPackageName() {
        return source.getPackageName();
    }

    @Override
    public String getSimpleClassName() {
        return source.getSimpleClassName();
    }

    @Override
    public String getPackageCanonicalClassName() {
        return source.getPackageCanonicalClassName();
    }

    @Override
    public Map<String, byte[]> compileClasses(Map<String, String> sourcesMap) {
        try {
            dumpSources(sourcesMap, source.getPMML_MODEL());
            return source.compileClasses(sourcesMap);
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    @Override
    public PMMLCompilationContext getPmmlContext() {
        return source.getPmmlContext();
    }

    @Override
    public PMML_MODEL getPMML_MODEL() {
        return source.getPMML_MODEL();
    }

    @Override
    public MINING_FUNCTION getMINING_FUNCTION() {
        return source.getMINING_FUNCTION();
    }

    @Override
    public List<MiningField> getKieMiningFields() {
        return source.getKieMiningFields();
    }

    @Override
    public List<OutputField> getKieOutputFields() {
        return source.getKieOutputFields();
    }

    /**
     * Add <code>Field</code>s to current instance, <b>eventually replacing them if already present</b>
     *
     * @param toAdd
     */
    public void addFields(final List<Field<?>> toAdd) {
        if (toAdd != null) {
            toAdd.forEach(field -> {
                fields.removeIf(e -> e.getClass().equals(field.getClass()) && e.getName().equals(field.getName()));
                fields.add(field);
            });
        }
    }

    protected CompilationDTO<T> getSource() {
        return source;
    }

    public List<KiePMMLMiningField> getKiePMMLMiningFields() {
        if (kiePMMLMiningFields == null) {
            if (getMiningSchema() != null && getMiningSchema().hasMiningFields()) {
                kiePMMLMiningFields =
                        getMiningSchema().getMiningFields().stream().map(this::getKiePMMLMiningField).collect(Collectors.toList());
            } else {
                kiePMMLMiningFields = Collections.emptyList();
            }
        }
        return Collections.unmodifiableList(kiePMMLMiningFields);
    }

    public List<KiePMMLOutputField> getKiePMMLOutputFields() {
        if (kiePMMLOutputFields == null) {
            if (getOutput() != null && getOutput().hasOutputFields()) {
                kiePMMLOutputFields =
                        getOutput().getOutputFields().stream().map(this::getKiePMMLOutputField).collect(Collectors.toList());
            } else {
                kiePMMLOutputFields = Collections.emptyList();
            }
        }
        return Collections.unmodifiableList(kiePMMLOutputFields);
    }

    public List<KiePMMLTarget> getKiePMMLTargetFields() {
        if (kiePMMLTargets == null) {
            if (getTargets() != null && getTargets().hasTargets()) {
                kiePMMLTargets =
                        getTargets().getTargets().stream().map(this::getKiePMMLTarget).collect(Collectors.toList());
            } else {
                kiePMMLTargets = Collections.emptyList();
            }
        }
        return Collections.unmodifiableList(kiePMMLTargets);
    }

    public KiePMMLTransformationDictionary getKiePMMLTransformationDictionary() {
        if (kiePMMLTransformationDictionary == null && getTransformationDictionary() != null) {
            kiePMMLTransformationDictionary = getKiePMMLTransformationDictionary(getTransformationDictionary());
        }
        return kiePMMLTransformationDictionary;
    }

    public KiePMMLLocalTransformations getKiePMMLLocalTransformations() {
        if (kiePMMLLocalTransformations == null && getLocalTransformations() != null) {
            kiePMMLLocalTransformations = getKiePMMLLocalTransformations(getLocalTransformations());
        }
        return kiePMMLLocalTransformations;
    }

    private KiePMMLMiningField getKiePMMLMiningField(org.dmg.pmml.MiningField source) {
        Field<?> field = fields.stream()
                .filter(fld -> fld.getName().equals(source.getName()))
                .findFirst()
                .orElseThrow(() -> new KiePMMLException("Cannot find " + source.getName() + " in " +
                                                                "DataDictionary"));
        return KiePMMLMiningFieldInstanceFactory.getKiePMMLMiningField(source, field);
    }

    private KiePMMLOutputField getKiePMMLOutputField(org.dmg.pmml.OutputField source) {
        return KiePMMLOutputFieldInstanceFactory.getKiePMMLOutputField(source);
    }

    private KiePMMLTarget getKiePMMLTarget(org.dmg.pmml.Target source) {
        return KiePMMLTargetInstanceFactory.getKiePMMLTarget(source);
    }

    private KiePMMLTransformationDictionary getKiePMMLTransformationDictionary(TransformationDictionary source) {
        return KiePMMLTransformationDictionaryInstanceFactory.getKiePMMLTransformationDictionary(source, getFields());
    }

    private KiePMMLLocalTransformations getKiePMMLLocalTransformations(LocalTransformations source) {
        return KiePMMLLocalTransformationsInstanceFactory.getKiePMMLLocalTransformations(source, getFields());
    }
}
