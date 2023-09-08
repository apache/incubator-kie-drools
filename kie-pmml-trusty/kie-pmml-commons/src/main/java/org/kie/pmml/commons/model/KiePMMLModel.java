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
package org.kie.pmml.commons.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.api.models.PMMLModel;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;
import org.kie.pmml.commons.transformations.KiePMMLLocalTransformations;
import org.kie.pmml.commons.transformations.KiePMMLTransformationDictionary;

import static org.kie.pmml.commons.Constants.PMML_SUFFIX;

/**
 * KIE representation of PMML model
 */
public abstract class KiePMMLModel extends AbstractKiePMMLComponent implements PMMLModel {

    private static final long serialVersionUID = 759750766311061701L;

    private final String fileName;
    protected PMML_MODEL pmmlMODEL;
    protected MINING_FUNCTION miningFunction;
    protected String targetField;
    protected List<MiningField> miningFields = new ArrayList<>();
    protected List<OutputField> outputFields = new ArrayList<>();
    protected List<KiePMMLMiningField> kiePMMLMiningFields = new ArrayList<>();
    protected List<KiePMMLOutputField> kiePMMLOutputFields = new ArrayList<>();
    protected List<KiePMMLTarget> kiePMMLTargets = new ArrayList<>();
    protected KiePMMLTransformationDictionary transformationDictionary;
    protected KiePMMLLocalTransformations localTransformations;

    protected KiePMMLModel(String fileName, String name, List<KiePMMLExtension> extensions) {
        super(name, extensions);
        if (!fileName.endsWith(PMML_SUFFIX)) {
            this.fileName = fileName + PMML_SUFFIX;
        } else {
            this.fileName = fileName;
        }
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    public PMML_MODEL getPmmlMODEL() {
        return pmmlMODEL;
    }

    public MINING_FUNCTION getMiningFunction() {
        return miningFunction;
    }

    public String getTargetField() {
        return targetField;
    }

    /**
     * Method to retrieve the <b>package</b> name to be used inside kiebase/package attribute of
     * kmodule.xml and to use for package creation inside PMMLAssemblerService
     * By default returns the package name of the current instance
     * To be eventually overridden.
     * @return
     */
    public String getKModulePackageName() {
        String className = this.getClass().getCanonicalName();
        return className.substring(0, className.lastIndexOf('.'));
    }

    @Override
    public List<MiningField> getMiningFields() {
        return miningFields;
    }

    @Override
    public List<OutputField> getOutputFields() {
        return outputFields;
    }

    public List<KiePMMLTarget> getKiePMMLTargets() {
        return kiePMMLTargets;
    }

    public List<KiePMMLMiningField> getKiePMMLMiningFields() {
        return kiePMMLMiningFields != null ? Collections.unmodifiableList(kiePMMLMiningFields) :
                Collections.emptyList();
    }

    public List<KiePMMLOutputField> getKiePMMLOutputFields() {
        return kiePMMLOutputFields != null ? Collections.unmodifiableList(kiePMMLOutputFields) :
                Collections.emptyList();
    }

    public KiePMMLTransformationDictionary getTransformationDictionary() {
        return transformationDictionary;
    }

    public KiePMMLLocalTransformations getLocalTransformations() {
        return localTransformations;
    }

    /**
     * models, so it may be <b>ignored</b> by others
     * @param requestData
     * @param context used to accumulate additional evaluated values
     * @return
     */
    public abstract Object evaluate(final Map<String, Object> requestData,
                                    final PMMLRuntimeContext context);

    public abstract static class Builder<T extends KiePMMLModel> extends AbstractKiePMMLComponent.Builder<T> {

        protected Builder(String prefix, PMML_MODEL pmmlMODEL, MINING_FUNCTION miningFunction, Supplier<T> supplier) {
            super(prefix, supplier);
            toBuild.pmmlMODEL = pmmlMODEL;
            toBuild.miningFunction = miningFunction;
        }

        public Builder<T> withTargetField(String targetField) {
            toBuild.targetField = targetField;
            return this;
        }

        public Builder<T> withMiningFields(List<MiningField> miningFields) {
            if (miningFields != null) {
                toBuild.miningFields = miningFields;
            }
            return this;
        }

        public Builder<T> withOutputFields(List<OutputField> outputFields) {
            if (outputFields != null) {
                toBuild.outputFields = outputFields;
            }
            return this;
        }

        public Builder<T> withKiePMMLMiningFields(List<KiePMMLMiningField> kiePMMLMiningFields) {
            if (kiePMMLMiningFields != null) {
                toBuild.kiePMMLMiningFields = kiePMMLMiningFields;
            }
            return this;
        }

        public Builder<T> withKiePMMLOutputFields(List<KiePMMLOutputField> kiePMMLOutputFields) {
            if (kiePMMLOutputFields != null) {
                toBuild.kiePMMLOutputFields = kiePMMLOutputFields;
            }
            return this;
        }

        public Builder<T> withKiePMMLTargets(List<KiePMMLTarget> kiePMMLTargets) {
            if (kiePMMLTargets != null) {
                toBuild.kiePMMLTargets = kiePMMLTargets;
            }
            return this;
        }

        public Builder<T> withKiePMMLTransformationDictionary(KiePMMLTransformationDictionary transformationDictionary) {
            toBuild.transformationDictionary = transformationDictionary;
            return this;
        }

        public Builder<T> withKiePMMLLocalTransformations(KiePMMLLocalTransformations localTransformations) {
            toBuild.localTransformations = localTransformations;
            return this;
        }
    }
}
