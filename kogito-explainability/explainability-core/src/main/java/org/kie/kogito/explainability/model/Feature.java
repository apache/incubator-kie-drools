/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.explainability.model;

import java.util.Objects;

import org.kie.kogito.explainability.model.domain.EmptyFeatureDomain;
import org.kie.kogito.explainability.model.domain.FeatureDomain;

/**
 * A feature represents fixed portions of an input, having a name, a {@link Type} and an associated {@link Value}.
 */
public class Feature {

    private final String name;
    private final Type type;
    private final Value value;

    private final FeatureDomain domain;
    private final boolean constrained;
    private final FeatureDistribution distribution;

    public Feature(String name, Type type, Value value) {
        this(name, type, value, true, EmptyFeatureDomain.create());

    }

    public Feature(String name, Type type, Value value, boolean constrained, FeatureDomain domain) {
        this(name, type, value, constrained, domain, null);
    }

    public Feature(String name, Type type, Value value, boolean constrained, FeatureDomain domain,
            FeatureDistribution distribution) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.domain = domain;
        this.constrained = constrained;
        this.distribution = distribution;
    }

    /**
     * The name of the feature
     *
     * @return this feature name
     */
    public String getName() {
        return this.name;
    }

    /**
     * The type of the feature
     *
     * @return this feature type
     */
    public Type getType() {
        return type;
    }

    /**
     * The value of the feature
     *
     * @return the feature value
     */
    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Feature{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", value=" + value +
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
        Feature feature = (Feature) o;
        return Objects.equals(name, feature.name) &&
                type == feature.type &&
                Objects.equals(value, feature.value);
    }

    public FeatureDomain getDomain() {
        return domain;
    }

    public boolean isConstrained() {
        return constrained;
    }

    public FeatureDistribution getDistribution() {
        return distribution;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, value);
    }
}
