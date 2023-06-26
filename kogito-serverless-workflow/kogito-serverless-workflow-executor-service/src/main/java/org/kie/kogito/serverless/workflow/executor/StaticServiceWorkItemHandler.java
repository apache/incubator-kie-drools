/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.executor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jbpm.compiler.canonical.ReflectionUtils;
import org.kie.kogito.serverless.workflow.ServiceWorkItemHandler;

import static org.kie.kogito.serverless.workflow.SWFConstants.SERVICE_TASK_TYPE;

public class StaticServiceWorkItemHandler extends ServiceWorkItemHandler {

    @Override
    protected Object invoke(String className, String methodName, Object parameters) {
        try {
            ClassLoader cls = Thread.currentThread().getContextClassLoader();
            Class<?> clazz = cls.loadClass(className);
            Object[] args = parameters instanceof Map ? ((Map<String, Object>) parameters).values().toArray() : new Object[] { parameters };
            return ReflectionUtils.getMethod(cls, clazz, methodName, Stream.of(args).map(Object::getClass).map(Class::getName).collect(Collectors.toList())).invoke(getInstance(clazz),
                    args);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    protected Object getInstance(Class<?> clazz) throws ReflectiveOperationException {
        return clazz.getConstructor().newInstance();
    }

    @Override
    public String getName() {
        return SERVICE_TASK_TYPE;
    }
}
