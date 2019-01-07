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

import java.util.ArrayList;
import java.util.Collection;

import org.kie.api.internal.assembler.ProcessedResource;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderError;

/**
 * A basic implementation of a {@link ResourceProcessor}. This is the preferred way to
 * implement such interface.
 */
public abstract class AbstractResourceProcessor<T extends ProcessedResource> implements ResourceProcessor<T> {

    private final Resource resource;
    private final ArrayList<KnowledgeBuilderError> errors;
    private T processedResource;

    /**
     * @param resource resource that will be processed
     */
    public AbstractResourceProcessor(Resource resource) {
        this.resource = resource;
        this.errors = new ArrayList<>();
    }

    /**
     * Implementations should always call {@link #appendError(KnowledgeBuilderError)}
     * to append errors that occur during processing, and {@link #setProcessedResource(ProcessedResource)}
     * to return the result.
     */
    @Override
    public abstract void process();

    @Override
    public final Resource getResource() {
        return resource;
    }

    /**
     * Utility to append errors to the internal collection during {@link #process()}
     */
    protected final void appendError(KnowledgeBuilderError error) {
        errors.add(error);
    }

    /**
     * Utility to set the result of {@link #process()}.
     */
    protected final void setProcessedResource(T processedResource) {
        this.processedResource = processedResource;
    }

    @Override
    public final T getProcessedResource() {
        if (processedResource == null && errors.isEmpty()) {
            throw new IllegalStateException("Resource has not been processed");
        }
        if (!errors.isEmpty()) {
            throw new IllegalStateException("Errors occurred while processing the resource");
        }
        return processedResource;
    }

    @Override
    public final Collection<? extends KnowledgeBuilderError> getErrors() {
        return errors;
    }
}
