/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.internal.command;

import java.util.function.Function;

import org.kie.api.runtime.Context;

public interface RegistryContext extends Context {

    <T> RegistryContext register(Class<T> clazz, T instance);

    <T> T lookup(Class<T> clazz);

    default <T> T computeIfAbsent(Class<T> clazz, Function<? super Class<T>, ? extends  T> mappingFunction) {
        T element = lookup(clazz);
        if (element == null) {
            element = mappingFunction.apply(clazz);
            register(clazz, element);
        }
        return element;
    }

    ContextManager getContextManager();

    static RegistryContext create() {
        try {
            return (RegistryContext) Class.forName( "org.drools.commands.impl.ContextImpl" ).getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to instance RegistryContext, please add org.drools:drools-commands to your classpath", e);
        }
    }
}
