/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.rule.accessor;

import org.drools.base.base.ValueResolver;
import org.drools.core.reteoo.BaseTuple;
import org.drools.core.rule.Declaration;
import org.kie.api.runtime.rule.FactHandle;

public interface PredicateExpression
    extends
    Invoker {

    Object createContext();

    public boolean evaluate(FactHandle handle,
                            BaseTuple tuple,
                            Declaration[] previousDeclarations,
                            Declaration[] localDeclarations,
                            ValueResolver valueResolver,
                            Object context ) throws Exception;
}
