/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.stream.bi;

import java.util.function.Function;

public final class BiJoiner<A, B, Property_> {

    public static <A, Property_> BiJoiner<A, A, Property_> equals(Function<A, Property_> mapping) {
        return equals(mapping, mapping);
    }

    public static <A, B, Property_> BiJoiner<A, B, Property_> equals(
            Function<A, Property_> leftMapping, Function <B, Property_> rightMapping) {
        return new BiJoiner<>(leftMapping, rightMapping);
    }

    // TODO add not equals, less then, less or equal then, greater than, ...

    // ************************************************************************
    // Node creation methods
    // ************************************************************************

    private final Function<A, Property_> leftMapping;
    private final Function<B, Property_> rightMapping;

    public BiJoiner(Function<A, Property_> leftMapping, Function<B, Property_> rightMapping) {
        this.leftMapping = leftMapping;
        this.rightMapping = rightMapping;
    }

    public Function<A, Property_> getLeftMapping() {
        return leftMapping;
    }

    public Function<B, Property_> getRightMapping() {
        return rightMapping;
    }

}
