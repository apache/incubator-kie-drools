/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Information about the search space domain for the model's features.
 */

public class FeatureDomain {
    public static final FeatureDomain EMPTY = new FeatureDomain();
    private final Double start;
    private final Double end;
    private final Set<String> categories;

    private FeatureDomain(Double start, Double end, Set<String> categories) {
        this.start = start;
        this.end = end;
        this.categories = categories;
    }

    /**
     * Create a {@link FeatureDomain} for a continuous feature
     * 
     * @param start The start point of the search space
     * @param end The end point of the search space
     * @return A {@link FeatureDomain}
     */
    public static FeatureDomain numerical(double start, double end) {
        return new FeatureDomain(start, end, null);
    }

    /**
     * Create a {@link FeatureDomain} for a continuous feature
     * 
     * @param start The start point of the search space
     * @param end The end point of the search space
     * @return A {@link FeatureDomain}
     */
    public static FeatureDomain numerical(int start, int end) {
        return new FeatureDomain((double) start, (double) end, null);
    }

    /**
     * Create a {@link FeatureDomain} for a categorical feature
     * 
     * @param categories A set with all the allowed category values
     * @return A {@link FeatureDomain}
     */
    public static FeatureDomain categorical(Set<String> categories) {
        return new FeatureDomain(null, null, categories);
    }

    /**
     * Create a {@link FeatureDomain} for a categorical feature
     * 
     * @param categories A list with all the allowed category values
     * @return A {@link FeatureDomain}
     */
    public static FeatureDomain categorical(List<String> categories) {
        return new FeatureDomain(null, null, new HashSet<>(categories));
    }

    /**
     * Create a {@link FeatureDomain} for a categorical feature
     * 
     * @param categories All the allowed category values
     * @return A {@link FeatureDomain}
     */
    public static FeatureDomain categorical(String... categories) {
        return new FeatureDomain(null, null, new HashSet<>(Arrays.asList(categories)));
    }

    private FeatureDomain() {
        this.start = null;
        this.end = null;
        this.categories = null;
    }

    public Set<String> getCategories() {
        return categories;
    }

    /**
     * Return whether this is an empty domain
     * 
     * @return True if empty
     */
    public boolean isEmpty() {
        return (start == null && end == null && categories == null);
    }

    /**
     * Get start value for this boundary
     *
     * @return the start value
     */
    public Double getStart() {
        return start;
    }

    /**
     * Get the end value for this boundary
     *
     * @return the end value
     */
    public Double getEnd() {
        return end;
    }
}