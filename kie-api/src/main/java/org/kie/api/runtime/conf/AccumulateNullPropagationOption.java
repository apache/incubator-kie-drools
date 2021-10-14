/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.api.runtime.conf;

/**
 * An option to define if 'accumulate' propagates its result even when its accumulate function result is 'null'.
 * For example, min(), max(), ave() returns 'null' when no fact matches the Pattern.
 *
 * drools.accumulateNullPropagation = &lt;true|false&gt;
 *
 * DEFAULT = false
 */
public enum AccumulateNullPropagationOption implements SingleValueKieSessionOption {

    YES(true),
    NO(false);

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the accumulate null propagation configuration
     */
    public static final String PROPERTY_NAME = "drools.accumulateNullPropagation";

    private final boolean accumulateNullPropagation;

    /**
     * Private constructor to enforce the use of the factory method
     * @param accumulateNullPropagation
     */
    AccumulateNullPropagationOption(final boolean accumulateNullPropagation) {
        this.accumulateNullPropagation = accumulateNullPropagation;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isAccumulateNullPropagation() {
        return accumulateNullPropagation;
    }

}
