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
import java.util.Objects;

import org.dmg.pmml.DataField;
import org.dmg.pmml.Field;
import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.Model;
import org.dmg.pmml.OpType;
import org.dmg.pmml.Output;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.PMML;
import org.dmg.pmml.Targets;
import org.dmg.pmml.TransformationDictionary;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.compiler.api.utils.ModelUtils;

import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;

/**
 * DTO meant to bring around all information needed for compilation, embedding/hiding helper methods
 */
public class CommonCompilationDTO<T extends Model> implements CompilationDTO<T> {

    private static final long serialVersionUID = -9136538788329888191L;

    protected final String packageName;
    private final String packageCanonicalClassName;
    private final List<Field<?>> fields;
    private final TransformationDictionary transformationDictionary;
    private final T model;
    /**
     * Using <code>HasClassloader</code> to avoid coupling with drools
     */
    private final HasClassLoader hasClassloader;
    private final PMML pmml;
    private final PMML_MODEL pmmlModel;
    private final String simpleClassName;
    private final String targetDataFieldName;
    private final DataField targetDataField;
    private final OpType opType;

    /**
     * Private constructor that preserve given <b>packageName</b>
     * <code>CompilationDTO</code>
     * @param pmml
     * @param model
     * @param hasClassloader
     * @param packageName
     */
    private CommonCompilationDTO(final PMML pmml,
                                 final T model,
                                 final HasClassLoader hasClassloader,
                                 final String packageName) {
        this(pmml, model, hasClassloader, packageName,
             ModelUtils.getFieldsFromDataDictionaryTransformationDictionaryAndModel(pmml.getDataDictionary(),
                                                                                    pmml.getTransformationDictionary(),
                                                                                    model));
    }

    /**
     * Private constructor that preserve given <b>packageName</b> and <b>fields</b>
     * @param pmml
     * @param model
     * @param hasClassloader
     * @param packageName
     * @param fields
     */
    private CommonCompilationDTO(final PMML pmml,
                                 final T model,
                                 final HasClassLoader hasClassloader,
                                 final String packageName,
                                 final List<Field<?>> fields) {
        this.packageName = packageName;
        this.pmml = pmml;
        this.transformationDictionary = pmml.getTransformationDictionary();
        this.fields = new ArrayList<>(fields);
        this.model = model;
        this.hasClassloader = hasClassloader;
        this.pmmlModel = PMML_MODEL.byName(model.getClass().getSimpleName());
        simpleClassName = getSanitizedClassName(model.getModelName());
        packageCanonicalClassName = String.format(PACKAGE_CLASS_TEMPLATE, this.packageName, simpleClassName);
        targetDataFieldName = ModelUtils.getTargetFieldName(fields, model).orElse(null);
        if (targetDataFieldName != null) {
            targetDataField = fields.stream()
                    .filter(DataField.class::isInstance)
                    .map(DataField.class::cast)
                    .filter(field -> Objects.equals(getTargetFieldName(), field.getName().getValue()))
                    .findFirst().orElse(null);
        } else {
            targetDataField = null;
        }
        opType = targetDataField != null ? targetDataField.getOpType() : null;
    }

    /**
     * Private constructor that create the <b>packageName</b> name from the given one and retrieve <b>fields</b>
     * from <b>pmml</b> and <b>model</b>
     * @param pmml
     * @param model
     * @param hasClassloader
     * @param packageName
     */
    private CommonCompilationDTO(final String packageName,
                                 final PMML pmml,
                                 final T model,
                                 final HasClassLoader hasClassloader) {
        this(pmml, model, hasClassloader, getSanitizedPackageName(String.format(PACKAGE_CLASS_TEMPLATE, packageName,
                                                                                model.getModelName())));
    }

    /**
     * Builder that create the <b>packageName</b> name from the given one and retrieve <b>fields</b>
     * from <b>pmml</b> and <b>model</b>
     * @param pmml
     * @param model
     * @param hasClassloader
     * @param packageName
     **/
    public static <T extends Model> CommonCompilationDTO<T> fromGeneratedPackageNameAndFields(final String packageName,
                                                                                              final PMML pmml,
                                                                                              final T model,
                                                                                              final HasClassLoader hasClassloader) {
        return new CommonCompilationDTO(packageName,
                                        pmml,
                                        model,
                                        hasClassloader);
    }

    /**
     * Builder that preserve given <b>packageName</b> and <b>fields</b>
     * <code>CompilationDTO</code>
     * @param pmml
     * @param model
     * @param hasClassloader
     * @param packageName
     * @param fields
     */
    public static <T extends Model> CommonCompilationDTO<T> fromPackageNameAndFields(final PMML pmml,
                                                                                     final T model,
                                                                                     final HasClassLoader hasClassloader,
                                                                                     final String packageName,
                                                                                     final List<Field<?>> fields) {
        return new CommonCompilationDTO<>(pmml, model, hasClassloader, packageName, fields);
    }

    @Override
    public PMML getPmml() {
        return pmml;
    }

    @Override
    public TransformationDictionary getTransformationDictionary() {
        return transformationDictionary;
    }

    @Override
    public T getModel() {
        return model;
    }

    @Override
    public MiningSchema getMiningSchema() {
        return model.getMiningSchema();
    }

    @Override
    public MiningFunction getMiningFunction() {
        return model.getMiningFunction();
    }

    @Override
    public LocalTransformations getLocalTransformations() {
        return model.getLocalTransformations();
    }

    @Override
    public Output getOutput() {
        return model.getOutput();
    }

    @Override
    public Targets getTargets() {
        return model.getTargets();
    }

    @Override
    public List<Field<?>> getFields() {
        return Collections.unmodifiableList(fields);
    }

    @Override
    public DataField getTargetDataField() {
        return targetDataField;
    }

    @Override
    public OpType getOpType() {
        return opType;
    }

    @Override
    public String getModelName() {
        return model.getModelName();
    }

    @Override
    public String getTargetFieldName() {
        return targetDataFieldName;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getSimpleClassName() {
        return simpleClassName;
    }

    @Override
    public String getPackageCanonicalClassName() {
        return packageCanonicalClassName;
    }

    @Override
    public Class<?> compileAndLoadClass(Map<String, String> sourcesMap) {
        return hasClassloader.compileAndLoadClass(sourcesMap, packageCanonicalClassName);
    }

    @Override
    public HasClassLoader getHasClassloader() {
        return hasClassloader;
    }

    @Override
    public PMML_MODEL getPMML_MODEL() {
        return pmmlModel;
    }

    @Override
    public MINING_FUNCTION getMINING_FUNCTION() {
        return model.getMiningFunction() != null ? MINING_FUNCTION.byName(model.getMiningFunction().value()) : null;
    }

    public List<OutputField> getOutputFields() {
        return model.getOutput() != null && model.getOutput().hasOutputFields() ?
                model.getOutput().getOutputFields() : Collections.emptyList();
    }
}
