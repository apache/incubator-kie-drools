/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.compiler;

import java.util.Arrays;

import org.drools.core.kie.impl.MessageImpl;
import org.kie.api.io.Resource;
import org.kie.internal.builder.InternalMessage;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;

/**
 * A base abstract class for all Knowledge Builder results
 *
 */
public abstract class BaseKnowledgeBuilderResultImpl implements KnowledgeBuilderResult {

    private Resource resource;

    protected BaseKnowledgeBuilderResultImpl(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public abstract ResultSeverity getSeverity();

    public boolean isError() {
        return getSeverity().equals(ResultSeverity.ERROR);
    }

    /**
     * Classes that extend this must provide a printable message,
     * which summarises the error.
     */
    public abstract String getMessage();

    /**
     * Returns the lines of the error in the source file
     * @return
     */
    public abstract int[] getLines();

    public String toString() {
        return getClass().getSimpleName() + ": " + getMessage();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }

        KnowledgeBuilderResult that = (KnowledgeBuilderResult) o;

        if (resource != null ? !resource.equals(that.getResource()) : that.getResource() != null) {
            return false;
        }

        return getMessage().equals(that.getMessage()) && Arrays.equals(getLines(), that.getLines());
    }

    @Override
    public int hashCode() {
        int hash = (29 * getMessage().hashCode()) + (31 * Arrays.hashCode(getLines()));
        return resource != null ? hash + (37 * resource.hashCode()) : hash;
    }

    @Override
    public InternalMessage asMessage(long id) {
        return new MessageImpl(id, this);
    }
}
