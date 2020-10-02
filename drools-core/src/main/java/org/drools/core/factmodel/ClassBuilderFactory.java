/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.factmodel;

import org.drools.core.rule.TypeDeclaration;
import org.kie.api.internal.utils.ServiceRegistry;

import static org.drools.core.base.CoreComponentsBuilder.throwExceptionForMissingMvel;

public interface ClassBuilderFactory {

    boolean DUMP_GENERATED_CLASSES = false;

    class Holder {
        private static final ClassBuilderFactory factory = getFactory();

        private static ClassBuilderFactory getFactory() {
            ClassBuilderFactory instance = ServiceRegistry.getService( ClassBuilderFactory.class );
            return instance != null ? instance : throwExceptionForMissingMvel();
        }
    }

    static ClassBuilderFactory get() {
        return Holder.factory;
    }

    ClassBuilder getBeanClassBuilder();

    EnumClassBuilder getEnumClassBuilder();

    ClassBuilder getPropertyWrapperBuilder();

    void setPropertyWrapperBuilder(ClassBuilder pcb);

    ClassBuilder getClassBuilder(TypeDeclaration type);
}
