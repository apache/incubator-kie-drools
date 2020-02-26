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
package org.kie.pmml.models.regression.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.KiePMMLBase;
import org.kie.pmml.models.regression.model.predictors.KiePMMLCategoricalPredictor;
import org.kie.pmml.models.regression.model.predictors.KiePMMLNumericPredictor;
import org.kie.pmml.models.regression.model.predictors.KiePMMLPredictorTerm;
import org.kie.pmml.models.regression.model.predictors.KiePMMLRegressionTablePredictor;

public class KiePMMLRegressionTable extends KiePMMLBase {

    private Number intercept;
    private Object targetCategory = null;
    private Set<KiePMMLNumericPredictor> numericPredictors = null;
    private Set<KiePMMLCategoricalPredictor> categoricalPredictors = null;
    private Set<KiePMMLPredictorTerm> predictorTerms = null;
    private Map<String, KiePMMLNumericPredictor> numericPredictorsMap = new HashMap<>();
    private Map<String, List<KiePMMLCategoricalPredictor>> categoricalPredictorMaps = new HashMap<>();

    private KiePMMLRegressionTable(String name, List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions, Number intercept) {
        return new Builder(name, extensions, intercept);
    }

    public Optional<KiePMMLNumericPredictor> getKiePMMLNumericPredictorByName(String fieldName) {
        return numericPredictorsMap.containsKey(fieldName) ? Optional.of(numericPredictorsMap.get(fieldName)) : Optional.empty();
    }

    public Optional<KiePMMLCategoricalPredictor> getKiePMMLCategoricalPredictorByNameAndValue(String fieldName, Object value) {
        if (!categoricalPredictorMaps.containsKey(fieldName)) {
            return Optional.empty();
        }
        return categoricalPredictorMaps.get(fieldName).stream().filter(categoricalPredictor -> Objects.equals(fieldName, categoricalPredictor.getName()) && Objects.equals(value, categoricalPredictor.getValue())).findFirst();
    }

    public Number getIntercept() {
        return intercept;
    }

    public Optional<Object> getTargetCategory() {
        return Optional.ofNullable(targetCategory);
    }

    /**
     * @return <code>Optional</code> of <b>unmodifiable</b> <code>Set&lt;KiePMMLNumericPredictor&gt;</code>
     */
    public Optional<Set<KiePMMLNumericPredictor>> getNumericPredictors() {
        return numericPredictors != null ? Optional.of(Collections.unmodifiableSet(numericPredictors)) : Optional.empty();
    }

    /**
     * @return <code>Optional</code> of <b>unmodifiable</b> <code>Set&lt;KiePMMLCategoricalPredictor&gt;</code>
     */
    public Optional<Set<KiePMMLCategoricalPredictor>> getCategoricalPredictors() {
        return categoricalPredictors != null ? Optional.of(Collections.unmodifiableSet(categoricalPredictors)) : Optional.empty();
    }

    /**
     * @return <code>Optional</code> of <b>unmodifiable</b> <code>Set&lt;KiePMMLCategoricalPredictor&gt;</code>
     */
    public Optional<Set<KiePMMLPredictorTerm>> getPredictorTerms() {
        return predictorTerms != null ? Optional.of(Collections.unmodifiableSet(predictorTerms)) : Optional.empty();
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

    public static class Builder extends KiePMMLBase.Builder<KiePMMLRegressionTable> {

        private Builder(String name, List<KiePMMLExtension> extensions, Number intercept) {
            super("RegressionTable-", () -> new KiePMMLRegressionTable(name, extensions));
            toBuild.intercept = intercept;
        }

        public Builder withTargetCategory(Object targetCategory) {
            toBuild.targetCategory = targetCategory;
            return this;
        }

        public Builder withNumericPredictors(Set<KiePMMLNumericPredictor> numericPredictors) {
            toBuild.numericPredictors = numericPredictors;
            toBuild.numericPredictorsMap.putAll(numericPredictors.stream().collect(Collectors.toMap(
                    KiePMMLRegressionTablePredictor::getName,
                    predictor -> predictor)));
            return this;
        }

        public Builder withCategoricalPredictors(Set<KiePMMLCategoricalPredictor> categoricalPredictors) {
            toBuild.categoricalPredictors = categoricalPredictors;
            toBuild.categoricalPredictorMaps = categoricalPredictors.stream()
                    .collect(Collectors.groupingBy(KiePMMLBase::getName));
            return this;
        }

        public Builder withPredictorTerms(Set<KiePMMLPredictorTerm> predictorTerms) {
            toBuild.predictorTerms = predictorTerms;
            return this;
        }
    }
}
