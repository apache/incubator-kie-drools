/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.builder.impl;

import org.drools.base.base.ObjectType;
import org.drools.base.rule.TypeDeclaration;

/**
 * Public interface to a {@link TypeDeclarationBuilder}
 *
 * Deals with updating a {@link org.kie.api.KieBase}, if there exist a live one.
 *
 */
public interface TypeDeclarationManager {
    TypeDeclaration getAndRegisterTypeDeclaration(Class<?> cls, String packageName);

    TypeDeclaration getTypeDeclaration(Class<?> cls);

    TypeDeclaration getTypeDeclaration(ObjectType objectType);
}
