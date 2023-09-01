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
package org.kie.efesto.compilationmanager.api.model;

import java.util.List;

import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

/**
 * A  <code>CompilationOutput</code> from one engine that will
 * be an <code>EfestoResource</code> for another one.
 * This will be translated to a <code>GeneratedRedirectResource</code>,
 * that has a specif json-format and semantic.
 */
public abstract class EfestoRedirectOutput<T> extends AbstractEfestoCallableCompilationOutput implements EfestoResource<T> {

    private final String targetEngine;

    /**
     * This is the <b>payload</b> to forward to the target compilation-engine
     */
    private final T content;

    protected EfestoRedirectOutput(ModelLocalUriId modelLocalUriId, String targetEngine, T content) {
        super(modelLocalUriId, (List<String>) null);
        if (targetEngine == null || targetEngine.isEmpty()) {
            throw new KieEfestoCommonException("Missing required target");
        }
        this.targetEngine = targetEngine;
        this.content = content;
    }

    public String getTargetEngine() {
        return targetEngine;
    }

    @Override
    public T getContent() {
        return content;
    }

}
