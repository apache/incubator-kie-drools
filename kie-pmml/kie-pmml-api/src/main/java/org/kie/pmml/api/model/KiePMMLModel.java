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
package org.kie.pmml.api.model;

import java.util.Objects;

import org.kie.pmml.api.model.abstracts.KiePMMLNamed;
import org.kie.pmml.api.model.enums.PMML_MODEL;

/**
 * KIE representation of PMML model
 */
public abstract class KiePMMLModel extends KiePMMLNamed {

    private static final long serialVersionUID = -6845971260164057040L;
    private final PMML_MODEL pmmlMODEL;

    public KiePMMLModel(String name, PMML_MODEL pmmlMODEL) {
        super(name);
        this.pmmlMODEL = pmmlMODEL;
    }

    public PMML_MODEL getPmmlMODEL() {
        return pmmlMODEL;
    }

    @Override
    public String toString() {
        return "KiePMMLModel{" +
                "name='" + name + '\'' +
                ", pmmlModelType=" + pmmlMODEL +
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
        KiePMMLModel that = (KiePMMLModel) o;
        return Objects.equals(getName(), that.getName()) &&
                getPmmlMODEL() == that.getPmmlMODEL();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getPmmlMODEL());
    }
}
