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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.drools.compiler.lang.descr.PackageDescr;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

/**
 * KIE representation of PMML model that use <b>drool</b> for implementation
 */
public abstract class KiePMMLDroolsModel extends KiePMMLModel {

    protected PackageDescr packageDescr;
    /**
     * Map between the original field name and the generated type.
     */
    protected Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;

    protected KiePMMLDroolsModel(String name, List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    public PackageDescr getPackageDescr() {
        return packageDescr;
    }

    public Map<String, KiePMMLOriginalTypeGeneratedType> getFieldTypeMap() {
        return fieldTypeMap;
    }

    @Override
    public String toString() {
        return "KiePMMLDroolsModel {" +
                "packageDescr=" + packageDescr +
                ", pmmlMODEL=" + pmmlMODEL +
                ", miningFunction=" + miningFunction +
                ", targetField='" + targetField + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        KiePMMLDroolsModel that = (KiePMMLDroolsModel) o;
        return Objects.equals(packageDescr, that.packageDescr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), packageDescr);
    }

    public abstract static class Builder<T extends KiePMMLDroolsModel> extends KiePMMLModel.Builder<T> {

        protected Builder(String prefix, PMML_MODEL pmmlMODEL, MINING_FUNCTION miningFunction, Supplier<T> supplier) {
            super(prefix, pmmlMODEL, miningFunction, supplier);
        }

        public Builder<T> withPackageDescr(PackageDescr packageDescr) {
            toBuild.packageDescr = packageDescr;
            return this;
        }

        public Builder<T> withFieldTypeMap(Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
            toBuild.fieldTypeMap = fieldTypeMap;
            return this;
        }
    }
}
