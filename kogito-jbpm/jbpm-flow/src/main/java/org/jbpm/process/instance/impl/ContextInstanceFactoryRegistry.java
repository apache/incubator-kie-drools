/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.process.instance.impl;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.core.Context;
import org.jbpm.process.core.context.exception.CompensationScope;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.context.swimlane.SwimlaneContext;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.exception.CompensationScopeInstance;
import org.jbpm.process.instance.context.exception.DefaultExceptionScopeInstance;
import org.jbpm.process.instance.context.swimlane.SwimlaneContextInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.factory.ReuseContextInstanceFactory;

public class ContextInstanceFactoryRegistry {
    
    public static final ContextInstanceFactoryRegistry INSTANCE = 
        new ContextInstanceFactoryRegistry();

    private Map<Class<? extends Context>, ContextInstanceFactory> registry;

    public ContextInstanceFactoryRegistry() {
        this.registry = new HashMap<Class<? extends Context>, ContextInstanceFactory>();

        // hard wired contexts:
        register(VariableScope.class, new ReuseContextInstanceFactory(
                 VariableScopeInstance.class));
        register(ExceptionScope.class, new ReuseContextInstanceFactory(
                 DefaultExceptionScopeInstance.class));
        register(CompensationScope.class, new ReuseContextInstanceFactory(
                 CompensationScopeInstance.class));
        register(SwimlaneContext.class, new ReuseContextInstanceFactory(
                 SwimlaneContextInstance.class));
    }

    public void register(Class<? extends Context> cls,
            ContextInstanceFactory factory) {
        this.registry.put(cls, factory);
    }

    public ContextInstanceFactory getContextInstanceFactory(Context context) {
        return this.registry.get(context.getClass());
    }
}
