/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.domain.common.accessor;

import java.util.function.Function;

public abstract class AbstractMemberAccessor implements MemberAccessor {

    // We cache this so that the same reference is always returned; useful for CS node sharing.
    private final Function getterFuction = this::executeGetter;

    @Override
    public final <Fact_, Result_> Function<Fact_, Result_> getGetterFunction() {
        return getterFuction;
    }

}
