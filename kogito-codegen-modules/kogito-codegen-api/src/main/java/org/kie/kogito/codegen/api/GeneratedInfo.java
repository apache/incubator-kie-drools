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
package org.kie.kogito.codegen.api;

import java.util.Collection;
import java.util.Collections;

import org.drools.codegen.common.GeneratedFile;

/**
 * A wrapper that allows a generator to return a core information structure, like process, plus an additional
 * set of files to be generated
 * 
 * @param <T> the type of the object returned by info
 */
public class GeneratedInfo<T> {
    private T info;
    private Collection<GeneratedFile> files;

    public GeneratedInfo(T info) {
        this(info, Collections.emptyList());
    }

    public GeneratedInfo(T process, Collection<GeneratedFile> files) {
        this.info = process;
        this.files = files;
    }

    public T info() {
        return info;
    }

    public Collection<GeneratedFile> files() {
        return files;
    }
}
