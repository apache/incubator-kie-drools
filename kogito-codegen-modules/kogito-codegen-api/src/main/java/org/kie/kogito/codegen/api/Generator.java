/*
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
package org.kie.kogito.codegen.api;

import java.util.Collection;
import java.util.Optional;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;

/**
 * A code generator for a part of the platform, e.g. rules, processes, etc.
 */
public interface Generator {

    GeneratedFileType MODEL_TYPE = GeneratedFileType.of("MODEL", GeneratedFileType.Category.SOURCE, true, true);

    /**
     * kogito.codegen.(engine_name) -> (boolean) enable/disable engine code generation (default true)
     */
    String CONFIG_PREFIX = "kogito.codegen.";

    /**
     * Returns the "section" of the Application class corresponding to rules.
     * e.g the processes() method with processes().createMyProcess() etc.
     *
     */
    Optional<ApplicationSection> section();

    /**
     * Returns the collection of all the files that have been generated/compiled
     *
     */
    Collection<GeneratedFile> generate();

    Optional<ConfigGenerator> configGenerator();

    KogitoBuildContext context();

    String name();

    boolean isEmpty();

    /**
     * Override this method to specify an order of execution
     * 
     * @return
     */
    default int priority() {
        return Integer.MAX_VALUE;
    }

    default boolean isEnabled() {
        return !isEmpty() && context().getApplicationProperty(CONFIG_PREFIX + name())
                .map("true"::equalsIgnoreCase)
                .orElse(true);
    }
}
