/*
 * Copyright (c) 2021. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model;

import org.drools.model.functions.Function1;

public interface BetaIndex<A, B, V> extends BetaIndexN<A, V> {

    Function1<B, ?> getRightOperandExtractor();

    default int getArity() {
        return 1;
    }
}
