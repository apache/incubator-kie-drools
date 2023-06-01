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

package org.drools.base.rule.accessor;

import org.drools.base.base.ValueResolver;
import org.drools.base.base.ValueType;
import org.drools.base.reteoo.BaseTuple;

public interface TupleValueExtractor extends Cloneable {

    ValueType getValueType();

    default Object getValue( BaseTuple tuple) {
        return getValue( null, tuple );
    }

    Object getValue(ValueResolver valueResolver, BaseTuple tuple);

    TupleValueExtractor clone();
}
