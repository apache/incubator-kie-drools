/*
 * Copyright 2011 JBoss Inc
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
package org.drools.compiler;

import org.drools.builder.KnowledgeBuilderResult;
import org.drools.builder.ResultSeverity;
import org.drools.io.Resource;

/**
 * A base abstract class for all Knowledge Builder results
 *
 */
public abstract class BaseKnowledgeBuilderResultImpl implements KnowledgeBuilderResult {

    private final Resource resource;

    protected BaseKnowledgeBuilderResultImpl(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
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

}
