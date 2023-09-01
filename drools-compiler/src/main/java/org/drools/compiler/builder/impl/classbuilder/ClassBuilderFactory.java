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
package org.drools.compiler.builder.impl.classbuilder;

import org.drools.base.rule.TypeDeclaration;
import org.kie.api.internal.utils.KieService;

import static org.drools.base.base.CoreComponentsBuilder.throwExceptionForMissingMvel;

public interface ClassBuilderFactory extends KieService {

    class Holder {
        private static final ClassBuilderFactory factory = getFactory();

        private static ClassBuilderFactory getFactory() {
            ClassBuilderFactory instance = KieService.load( ClassBuilderFactory.class );
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
