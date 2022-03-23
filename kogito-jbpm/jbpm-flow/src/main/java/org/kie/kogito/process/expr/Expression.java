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
package org.kie.kogito.process.expr;

import java.util.Optional;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;

public interface Expression {

    <T> T eval(Object target, Class<T> returnClass, KogitoProcessContext context);

    boolean isValid(Optional<KogitoProcessContext> context);

    void assign(Object target, Object value, KogitoProcessContext context);

    String asString();
}
