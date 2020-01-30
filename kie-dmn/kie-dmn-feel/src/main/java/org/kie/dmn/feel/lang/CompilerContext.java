/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.lang;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.lang.types.FEELTypeRegistry;
import org.kie.dmn.feel.runtime.FEELFunction;

public interface CompilerContext {

    CompilerContext addInputVariableType( String name, Type type );

    Map<String, Type> getInputVariableTypes();

    CompilerContext addInputVariable( String name, Object value );

    Map<String, Object> getInputVariables();

    Set<FEELEventListener> getListeners();

    CompilerContext addFEELFunctions(Collection<FEELFunction> customFunction);

    Collection<FEELFunction> getFEELFunctions();

    boolean isDoCompile();

    void setDoCompile( boolean doCompile );

    void setFEELTypeRegistry(FEELTypeRegistry typeRegistry);

    FEELTypeRegistry getFEELFeelTypeRegistry();
}
