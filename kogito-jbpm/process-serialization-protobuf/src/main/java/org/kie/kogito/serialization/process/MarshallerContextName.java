/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serialization.process;

import org.kie.kogito.process.Process;

public final class MarshallerContextName<T> {

    public static final MarshallerContextName<ObjectMarshallerStrategy[]> OBJECT_MARSHALLING_STRATEGIES = new MarshallerContextName<>("OBJECT_MARSHALLING_STRATEGIES");
    public static final MarshallerContextName<String> MARSHALLER_FORMAT = new MarshallerContextName<>("FORMAT");
    public static final MarshallerContextName<Process<?>> MARSHALLER_PROCESS = new MarshallerContextName<>("PROCESS");
    public static final MarshallerContextName<Boolean> MARSHALLER_INSTANCE_READ_ONLY = new MarshallerContextName<>("READ_ONLY");

    private String name;

    private MarshallerContextName(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    @SuppressWarnings("unchecked")
    public T cast(Object value) {
        return (T) value;
    }
}
