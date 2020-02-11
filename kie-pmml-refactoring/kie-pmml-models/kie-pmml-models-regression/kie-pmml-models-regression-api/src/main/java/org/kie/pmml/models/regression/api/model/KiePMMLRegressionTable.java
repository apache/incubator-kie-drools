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
package org.kie.pmml.models.regression.api.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.kie.pmml.api.model.KiePMMLExtension;
import org.kie.pmml.api.model.abstracts.KiePMMLIDed;
import org.kie.pmml.api.model.abstracts.KiePMMLNamed;
import org.kie.pmml.models.regression.api.model.predictors.KiePMMLCategoricalPredictor;
import org.kie.pmml.models.regression.api.model.predictors.KiePMMLNumericPredictor;
import org.kie.pmml.models.regression.api.model.predictors.KiePMMLRegressionTablePredictor;

public class KiePMMLRegressionTable extends KiePMMLIDed {

    private static final long serialVersionUID = 1703573265998162350L;
    private Number intercept;
    private Serializable targetCategory;
    private List<KiePMMLExtension> extensions;
    private Set<KiePMMLNumericPredictor> numericPredictors;
    private List<KiePMMLCategoricalPredictor> categoricalPredictors;
    private Set<KiePMMLPredictorTerm> predictorTerms;
    private Map<String, KiePMMLNumericPredictor> numericPredictorsMap = new HashMap<>();
    private Map<String, List<KiePMMLCategoricalPredictor>> categoricalPredictorMaps = new HashMap<>();

    public static Builder builder() {
        return new Builder();
    }

    public Optional<KiePMMLNumericPredictor> getKiePMMLNumericPredictorByName(String fieldName) {
        return numericPredictorsMap.containsKey(fieldName) ? Optional.of(numericPredictorsMap.get(fieldName)) : Optional.empty();
    }

    public Optional<List<KiePMMLCategoricalPredictor>> getKiePMMLCategoricalPredictorsByName(String fieldName) {
        return categoricalPredictorMaps.containsKey(fieldName) ? Optional.of(categoricalPredictorMaps.get(fieldName)) : Optional.empty();
    }

    public Number getIntercept() {
        return intercept;
    }

    public Object getTargetCategory() {
        return targetCategory;
    }

    public List<KiePMMLExtension> getExtensions() {
        return extensions;
    }

    public Set<KiePMMLNumericPredictor> getNumericPredictors() {
        return numericPredictors;
    }

    public List<KiePMMLCategoricalPredictor> getCategoricalPredictors() {
        return categoricalPredictors;
    }

    public Set<KiePMMLPredictorTerm> getPredictorTerms() {
        return predictorTerms;
    }

    @Override
    public String toString() {
        return "KiePMMLRegressionTable{" +
                "intercept=" + intercept +
                ", targetCategory=" + targetCategory +
                ", extensions=" + extensions +
                ", numericPredictors=" + numericPredictors +
                ", categoricalPredictors=" + categoricalPredictors +
                ", predictorTerms=" + predictorTerms +
                ", numericPredictorsMap=" + numericPredictorsMap +
                ", categoricalPredictorMaps=" + categoricalPredictorMaps +
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
        KiePMMLRegressionTable that = (KiePMMLRegressionTable) o;
        return Objects.equals(intercept, that.intercept) &&
                Objects.equals(targetCategory, that.targetCategory) &&
                Objects.equals(extensions, that.extensions) &&
                Objects.equals(numericPredictors, that.numericPredictors) &&
                Objects.equals(categoricalPredictors, that.categoricalPredictors) &&
                Objects.equals(predictorTerms, that.predictorTerms) &&
                Objects.equals(numericPredictorsMap, that.numericPredictorsMap) &&
                Objects.equals(categoricalPredictorMaps, that.categoricalPredictorMaps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), intercept, targetCategory, extensions, numericPredictors, categoricalPredictors, predictorTerms, numericPredictorsMap, categoricalPredictorMaps);
    }

    private KiePMMLRegressionTable() {
    }

    public static class Builder  extends KiePMMLIDed.Builder<KiePMMLRegressionTable> {

        private Builder() {
            super("RegressionTable-", KiePMMLRegressionTable::new);
        }

        public Builder withIntercept(Number intercept) {
            toBuild.intercept = intercept;
            return this;
        }

        public Builder withTargetCategory(Serializable targetCategory) {
            toBuild.targetCategory = targetCategory;
            return this;
        }

        public Builder withExtensions(List<KiePMMLExtension> extensions) {
            toBuild.extensions = extensions;
            return this;
        }

        public Builder withNumericPredictors(Set<KiePMMLNumericPredictor> numericPredictors) {
            toBuild.numericPredictors = numericPredictors;
            toBuild.numericPredictorsMap.putAll(numericPredictors.stream().collect(Collectors.toMap(
                    KiePMMLRegressionTablePredictor::getName,
                    predictor -> predictor)));
            return this;
        }

        public Builder withCategoricalPredictors(List<KiePMMLCategoricalPredictor> categoricalPredictors) {
            toBuild.categoricalPredictors = categoricalPredictors;
            toBuild.categoricalPredictorMaps = categoricalPredictors.stream()
                    .collect(Collectors.groupingBy(KiePMMLNamed::getName));
            return this;
        }

        public Builder withPredictorTerms(Set<KiePMMLPredictorTerm> predictorTerms) {
            toBuild.predictorTerms = predictorTerms;
            return this;
        }

    }
}
