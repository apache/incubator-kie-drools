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
package org.kie.api.prototype;

import java.util.function.Function;

import org.kie.api.internal.utils.KieService;

public interface PrototypeBuilder {
    String NO_PROTOTYPE = "You're trying to use the Prototypes without having imported it. Please add the module org.drools:drools-model-prototype to your classpath.";

    class Holder {
        private static final PrototypeBuilder.Creator INSTANCE = KieService.load(PrototypeBuilder.Creator.class);
    }

    static PrototypeBuilder prototype(String name) {
        if (Holder.INSTANCE == null) {
            throw new RuntimeException(NO_PROTOTYPE);
        }
        return Holder.INSTANCE.newPrototype(name);
    }

    interface Creator extends KieService {
        PrototypeBuilder newPrototype(String name);
    }

    PrototypeBuilder withField(String name);
    PrototypeBuilder withField(String name, Function<PrototypeFactInstance, Object> extractor);
    PrototypeBuilder withField(String name, Class<?> type);
    PrototypeBuilder withField(String name, Class<?> type, Function<PrototypeFactInstance, Object> extractor);

    PrototypeFact asFact();
    PrototypeEvent asEvent();
}
