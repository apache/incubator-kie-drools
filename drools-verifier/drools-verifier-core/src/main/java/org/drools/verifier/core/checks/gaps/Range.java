/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.verifier.core.checks.gaps;

import java.util.List;
import java.util.function.Consumer;

import org.drools.verifier.core.cache.inspectors.condition.ConditionInspector;

public abstract class Range<T extends Comparable>
        implements Comparable<Range<T>> {

    protected T lowerBound = minValue();
    protected T upperBound = maxValue();

    public Range(final List<ConditionInspector> conditionInspectors) {
        if (conditionInspectors != null) {
            conditionInspectors.forEach(getConditionParser());
        }
    }

    protected abstract Consumer<ConditionInspector> getConditionParser();

    @Override
    public String toString() {
        return lowerBound + " < x < " + upperBound;
    }

    @Override
    public int compareTo(Range<T> o) {
        return lowerBound.compareTo(o.lowerBound);
    }

    protected abstract T minValue();

    protected abstract T maxValue();
}
