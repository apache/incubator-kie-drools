/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.correlation;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class CompositeCorrelation implements Correlation<Set<? extends Correlation<?>>> {

    private String key;
    private Set<? extends Correlation<?>> correlations;

    public CompositeCorrelation(Set<? extends Correlation<?>> correlations) {
        this.key = buildKey(correlations);
        this.correlations = Collections.unmodifiableSet(correlations);
    }

    private static String buildKey(Set<? extends Correlation<?>> correlations) {
        return correlations.stream().map(Correlation::getKey).collect(Collectors.joining("|"));
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Set<? extends Correlation<?>> getValue() {
        return correlations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeCorrelation)) {
            return false;
        }
        CompositeCorrelation that = (CompositeCorrelation) o;
        return Objects.equals(getValue(), that.getValue()) && Objects.equals(getKey(), that.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue(), getKey());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CompositeCorrelation.class.getSimpleName() + "[", "]")
                .add("correlations=" + correlations)
                .toString();
    }
}
