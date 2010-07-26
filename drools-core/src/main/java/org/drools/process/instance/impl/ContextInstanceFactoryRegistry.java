/**
 * Copyright 2010 JBoss Inc
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

package org.drools.process.instance.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.process.core.Context;
import org.drools.process.core.context.exception.ExceptionScope;
import org.drools.process.core.context.swimlane.SwimlaneContext;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.context.exception.DefaultExceptionScopeInstance;
import org.drools.process.instance.context.swimlane.SwimlaneContextInstance;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.process.instance.impl.factory.ReuseContextInstanceFactory;

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
