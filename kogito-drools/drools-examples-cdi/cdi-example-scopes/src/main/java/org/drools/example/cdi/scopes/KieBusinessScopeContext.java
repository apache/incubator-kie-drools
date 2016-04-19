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

package org.drools.example.cdi.scopes;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class KieBusinessScopeContext implements Context, Serializable {

    private Map<String, Object> somewhere = new HashMap<String, Object>();

    public KieBusinessScopeContext() {
        System.out.println(">>> Creating a new context instance");
    }


    // Get the scope type of the context object.
    @Override
    public Class<? extends Annotation> getScope() {
        return KieBusinessScoped.class;
    }

    // Return an existing instance of certain contextual type or create a new instance by calling
    // javax.enterprise.context.spi.Contextual.create(CreationalContext) and return the new instance.

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        Bean bean = (Bean) contextual;
        // you can store the bean somewhere
        if (somewhere.containsKey(bean.getName())) {
            return (T) somewhere.get(bean.getName());
        } else {
            T t = (T) bean.create(creationalContext);
            somewhere.put(bean.getName(), t);
            return t;
        }
    }

    // Return an existing instance of a certain contextual type or a null value.
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Contextual<T> contextual) {
        Bean bean = (Bean) contextual;
        // you can store the bean somewhere
        if (somewhere.containsKey(bean.getName())) {
            return (T) somewhere.get(bean.getName());
        } else {
            return null;
        }
    }

    // Determines if the context object is active.
    @Override
    public boolean isActive() {
        return true;
    }

}
