/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.efesto.runtimemanager.api.model;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

/**
 * A generic <i>input</i> to be consumed
 */
public interface EfestoInput<T> {

    /**
     * The unique, full identifier of a given model' resource
     * @return
     */
    ModelLocalUriId getModelLocalUriId();

    T getInputData();

    default EfestoClassKey getEfestoClassKeyIdentifier() {
        List<Type> generics = getInputData() != null ? Collections.singletonList(getInputData().getClass()) : Collections.emptyList();
        return new EfestoClassKey(this.getClass(), generics.toArray(new Type[0]));
    }
}
