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
package org.kie.pmml.models.mining.model.segmentation;

import java.util.List;
import java.util.Objects;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;
import org.kie.pmml.commons.model.predicates.KiePMMLPredicate;

public class KiePMMLSegment extends AbstractKiePMMLComponent {

    private final KiePMMLPredicate kiePMMLPredicate;
    private final KiePMMLModel model;
    protected double weight = 1;

    protected KiePMMLSegment(String name, List<KiePMMLExtension> extensions, KiePMMLPredicate kiePMMLPredicate, KiePMMLModel model) {
        super(name, extensions);
        this.kiePMMLPredicate = kiePMMLPredicate;
        this.model = model;
    }

    /**
     * Builder to auto-generate the <b>id</b>
     * @return
     */
    public static Builder builder(String name, List<KiePMMLExtension> extensions, KiePMMLPredicate kiePMMLPredicate, KiePMMLModel model) {
        return new Builder(name, extensions, kiePMMLPredicate, model);
    }

    public double getWeight() {
        return weight;
    }

    public KiePMMLPredicate getKiePMMLPredicate() {
        return kiePMMLPredicate;
    }

    public KiePMMLModel getModel() {
        return model;
    }

    @Override
    public String toString() {
        return "KiePMMLSegment{" +
                "kiePMMLPredicate=" + kiePMMLPredicate +
                ", model=" + model +
                ", weight=" + weight +
                ", name='" + name + '\'' +
                ", extensions=" + extensions +
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
        KiePMMLSegment that = (KiePMMLSegment) o;
        return Double.compare(that.weight, weight) == 0 &&
                Objects.equals(kiePMMLPredicate, that.kiePMMLPredicate) &&
                Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kiePMMLPredicate, model, weight);
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLSegment> {

        private Builder(String name, List<KiePMMLExtension> extensions, KiePMMLPredicate kiePMMLPredicate, KiePMMLModel model) {
            super("Segmentation-", () -> new KiePMMLSegment(name, extensions, kiePMMLPredicate, model));
            kiePMMLPredicate.setParentId(toBuild.id);
            model.setParentId(toBuild.id);
        }

        public Builder withWeight(double weight) {
            toBuild.weight = weight;
            return this;
        }
    }
}
