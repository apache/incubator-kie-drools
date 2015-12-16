/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.impl.adapters;

import org.drools.builder.ResultSeverity;
import org.drools.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderError;

import static org.drools.impl.adapters.AdapterUtil.adaptResultSeverity;

public class KnowledgeBuilderErrorAdapter implements org.drools.builder.KnowledgeBuilderError {

    private final KnowledgeBuilderError delegate;

    public KnowledgeBuilderErrorAdapter(KnowledgeBuilderError delegate) {
        this.delegate = delegate;
    }

    public ResultSeverity getSeverity() {
        return adaptResultSeverity(delegate.getSeverity());
    }

    public String getMessage() {
        return delegate.getMessage();
    }

    public int[] getLines() {
        return delegate.getLines();
    }

    public Resource getResource() {
        return new ResourceAdapter(delegate.getResource());
    }

    public String toString() {
        return delegate.toString();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof KnowledgeBuilderErrorAdapter && delegate.equals(((KnowledgeBuilderErrorAdapter)obj).delegate);
    }
}
