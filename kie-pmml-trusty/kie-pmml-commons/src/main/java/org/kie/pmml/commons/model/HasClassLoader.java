/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.commons.model;

import java.util.Map;

/**
 * Interface used to define if a given <code>Object</code> may provide a <b>context</b> <code>ClassLoader</code>
 */
public interface HasClassLoader {

    ClassLoader getClassLoader();

    /**
     * Compile the given sources and add them to given <code>Classloader</code> of the current instance.
     * Returns the <code>Class</code> with the given <b>fullClassName</b>
     * @param sourcesMap
     * @param fullClassName
     * @return
     */
    Class<?> compileAndLoadClass(Map<String, String> sourcesMap, String fullClassName);

}
