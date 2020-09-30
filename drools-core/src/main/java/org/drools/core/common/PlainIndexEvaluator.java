/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.common;

import org.drools.core.rule.IndexEvaluator;
import org.drools.core.spi.InternalReadAccessor;

public class PlainIndexEvaluator implements IndexEvaluator {

    public static final IndexEvaluator INSTANCE = new PlainIndexEvaluator();

    private PlainIndexEvaluator() { }

    public boolean evaluate(InternalWorkingMemory workingMemory,
                            final InternalReadAccessor extractor1,
                            final Object object1,
                            final InternalReadAccessor extractor2,
                            final Object object2) {
        return evaluate(workingMemory, extractor1.getValue( workingMemory, object1 ), extractor2, object2);
    }

    public boolean evaluate(InternalWorkingMemory workingMemory,
                            final Object value1,
                            final InternalReadAccessor extractor2,
                            final Object object2) {
        final Object value2 = extractor2.getValue( workingMemory, object2 );
        if (value1 == null) {
            return value2 == null;
        }
        if (value1 instanceof String) {
            return value2 != null && value1.equals(value2.toString());
        }
        if (value2 instanceof String) {
            return value1 != null && value2.equals(value1.toString());
        }
        return value1.equals( value2 );
    }
}
