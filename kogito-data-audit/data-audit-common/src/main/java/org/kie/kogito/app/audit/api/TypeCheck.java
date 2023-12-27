/*
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
package org.kie.kogito.app.audit.api;

import java.util.function.BiConsumer;

class TypeCheck<T> {

    private Class<T> clazz;

    public TypeCheck(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static <T> TypeCheck<T> typeCheckOf(Class<T> clazz) {
        return new TypeCheck<T>(clazz);
    }

    public void ifType(DataAuditContext context, Object event, BiConsumer<DataAuditContext, T> executor) {
        if (event != null && clazz.isInstance(event)) {
            executor.accept(context, clazz.cast(event));
        }
    }
}