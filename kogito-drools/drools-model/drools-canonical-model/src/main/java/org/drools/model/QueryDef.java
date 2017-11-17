/*
 * Copyright 2005 JBoss Inc
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

package org.drools.model;

import java.util.Arrays;

import org.drools.model.view.ViewItemBuilder;

public interface QueryDef {

    Class[] QUERIES_BY_ARITY = new Class[] {
            QueryDef.class, Query1Def.class, Query2Def.class, Query3Def.class, Query4Def.class
    };

    static Class getQueryClassByArity(int arity) {
        return QUERIES_BY_ARITY[arity];
    }

    String getPackage();
    String getName();

    Variable<?>[] getArguments();

    default <T> Variable<T> getArg(String argName, Class<T> argType) {
        return Arrays.stream(getArguments())
                .filter(a -> a.getName().equals(argName))
                .map(a -> (Variable<T>)a)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unknown argument: " + argName));
    }

    Query build( ViewItemBuilder... viewItemBuilders );
}
