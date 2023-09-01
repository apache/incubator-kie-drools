/**
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
package org.kie.efesto.compilationmanager.core.mocks;

import java.util.List;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.model.AbstractEfestoCallableCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;

/**
 * A generic <i>Resource</i> to be processed by specific engine
 */
public abstract class AbstractMockOutput<T> extends AbstractEfestoCallableCompilationOutput implements EfestoResource<T> {

    /**
     * This is the <b>payload</b> to forward to the target compilation-engine
     */
    private final T content;

    protected AbstractMockOutput(ModelLocalUriId modelLocalUriId, T content) {
        super(modelLocalUriId, (List<String>) null);
        this.content = content;
    }

    @Override
    public T getContent() {
        return content;
    }

}
