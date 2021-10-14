/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.compiler.api.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.DataField;
import org.dmg.pmml.Field;
import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.Model;
import org.dmg.pmml.OpType;
import org.dmg.pmml.Output;
import org.dmg.pmml.PMML;
import org.dmg.pmml.Targets;
import org.dmg.pmml.TransformationDictionary;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.HasClassLoader;

/**
 * Abstract class to be extended by all <b>model-specific</b> compilation dtos
 */
public abstract class AbstractSpecificCompilationDTO<T extends Model> implements CompilationDTO<T> {

    private static final long serialVersionUID = -3691191496300060907L;
    private final CompilationDTO<T> source;
    private final List<Field<?>> fields;

    /**
     * Protected constructor that generate a <code>CommonCompilationDTO</code> preserving given <b>packageName</b>
     * and <b>fields</b>
     * @param pmml
     * @param model
     * @param hasClassloader
     * @param packageName
     */
    protected AbstractSpecificCompilationDTO(final PMML pmml,
                                             final T model,
                                             final HasClassLoader hasClassloader,
                                             final String packageName,
                                             final List<Field<?>> fields) {
        this(CommonCompilationDTO.fromPackageNameAndFields(pmml, model, hasClassloader, packageName, fields));
    }

    /**
     * Protected constructor that use given <code>CompilationDTO</code>
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
    public Class<?> compileAndLoadClass(Map<String, String> sourcesMap) {
        return source.compileAndLoadClass(sourcesMap);
    }

    @Override
    public HasClassLoader getHasClassloader() {
        return source.getHasClassloader();
    }

    @Override
    public PMML_MODEL getPMML_MODEL() {
        return source.getPMML_MODEL();
    }

    @Override
    public MINING_FUNCTION getMINING_FUNCTION() {
        return source.getMINING_FUNCTION();
    }

    /**
     * Add <code>Field</code>s to current instance, <b>eventually replacing them if already present</b>
     * @param toAdd
     */
    public void addFields(final List<Field<?>> toAdd) {
        if (toAdd != null) {
            toAdd.forEach(field -> {
                fields.removeIf(e -> e.getName().equals(field.getName()));
                fields.add(field);
            });
        }
    }

    protected CompilationDTO<T> getSource() {
        return source;
    }
}
