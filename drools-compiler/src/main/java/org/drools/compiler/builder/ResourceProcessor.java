/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.builder;

import java.util.Collection;

import org.kie.api.internal.assembler.ProcessedResource;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderError;

/**
 * For a given resource, it implements a mechanism to transform
 * such resource in a ProcessedResource.
 *
 * This is the interface of some "compiler" of sort, that translate
 * a Resource into an "executable" representation of some kind,
 * suitable for execution by some existing runtime.
 *
 * In most cases, implementors are expected to just extend {@link AbstractResourceProcessor}
 *
 * @param <C> The type of the processed resource.
 */
public interface ResourceProcessor<C extends ProcessedResource> {

    /**
     * Perform the processing of the given resource
     */
    void process();

    /**
     * @return the resource that is processed by this object
     */
    Resource getResource();

    /**
     * @return the processed resource or null if there were errors
     * @throws IllegalStateException if process() was never called
     */
    C getProcessedResource();

    /**
     * Returns any error that have been generated while processing the resource.
     * @return empty if no errors occurred and the resource have been successfully processed
     */
    Collection<? extends KnowledgeBuilderError> getErrors();
}
