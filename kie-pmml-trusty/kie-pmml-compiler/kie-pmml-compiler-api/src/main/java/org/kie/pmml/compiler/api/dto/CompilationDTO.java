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

import java.io.Serializable;
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
 * Interface to be implemented by all concrete <b>compilation dtos</b>
 * @param <T>
 */
public interface CompilationDTO<T extends Model> extends Serializable {

    /**
     * The original <code>PMML</code>
     * @return
     */
    PMML getPmml();

    TransformationDictionary getTransformationDictionary();

    T getModel();

    MiningSchema getMiningSchema();

    MiningFunction getMiningFunction();

    LocalTransformations getLocalTransformations();

    Output getOutput();

    Targets getTargets();

    /**
     * Should contain all fields retrieved from model, i.e. DataFields from DataDictionary,
     * DerivedFields from Transformations/LocalTransformations, OutputFields
     * @return
     */
    List<Field<?>> getFields();

    DataField getTargetDataField();

    OpType getOpType();

    /**
     * Returns the <b>model name</b> of the underlying <code>Model</code>
     * @return
     */
    String getModelName();

    String getTargetFieldName();

    /**
     * The <b>sanitized</b> base package name
     * @return
     */
    String getPackageName();

    /**
     * Returns the <b>simple, sanitized</b> class name
     * @return
     */
    String getSimpleClassName();

    /**
     * Returns the <b>full, canonical, sanitized</b> class name
     * @return
     */
    String getPackageCanonicalClassName();

    /**
     * Compile the given sources and add them to given <code>Classloader</code> of the current instance.
     * Returns the <code>Class</code> with the current <b>canonicalClassName</b>
     * @param sourcesMap
     * @return
     * @see HasClassLoader#compileAndLoadClass(Map, String)
     */
    Class<?> compileAndLoadClass(Map<String, String> sourcesMap);

    HasClassLoader getHasClassloader();

    PMML_MODEL getPMML_MODEL();

    MINING_FUNCTION getMINING_FUNCTION();
}
