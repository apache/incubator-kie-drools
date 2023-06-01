/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.rule;

import org.drools.core.rule.constraint.Constraint;
import org.drools.core.rule.accessor.FieldValue;
import org.drools.core.rule.accessor.ReadAccessor;
import org.drools.core.rule.accessor.TupleValueExtractor;
import org.drools.core.util.FieldIndex;
import org.drools.core.util.index.ConstraintTypeOperator;
import org.kie.api.KieBaseConfiguration;

public interface IndexableConstraint extends Constraint {

    boolean isUnification();

    boolean isIndexable(short nodeType, KieBaseConfiguration config);

    ConstraintTypeOperator getConstraintType();

    FieldValue getField();

    FieldIndex getFieldIndex();

    ReadAccessor getFieldExtractor();

    default void unsetUnification() { }

    TupleValueExtractor getIndexExtractor();
}
