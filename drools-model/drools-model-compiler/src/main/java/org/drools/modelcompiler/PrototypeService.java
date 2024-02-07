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
package org.drools.modelcompiler;

import java.util.Map;
import java.util.function.Function;

import org.drools.base.base.ObjectType;
import org.drools.base.common.MissingDependencyException;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.model.Variable;
import org.kie.api.internal.utils.KieService;

public interface PrototypeService extends KieService {
    String NO_PROTOTYPE = "You're trying to use the Prototypes without having imported it. Please add the module org.drools:drools-model-prototype to your classpath.";

    ObjectType getPrototypeObjectType(Map<String, ObjectType> objectTypeCache, Map<String, InternalKnowledgePackage> packages, Function<String, InternalKnowledgePackage> packageCreator, Variable patternVariable );

    class Holder {
        private static final PrototypeService INSTANCE = KieService.load(PrototypeService.class);
    }

    static PrototypeService get() {
        return present() ? PrototypeService.Holder.INSTANCE : throwExceptionForMissingPrototype();
    }

    static boolean present() {
        return PrototypeService.Holder.INSTANCE != null;
    }

    static <T> T throwExceptionForMissingPrototype() {
        throw new MissingDependencyException(NO_PROTOTYPE);
    }
}
