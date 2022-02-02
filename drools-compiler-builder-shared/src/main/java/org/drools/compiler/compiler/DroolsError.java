/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.compiler;

import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.ResultSeverity;
import org.kie.api.io.Resource;

public abstract class DroolsError extends BaseKnowledgeBuilderResultImpl implements KnowledgeBuilderError {

    public DroolsError() {
        this(null);
    }

    public DroolsError(Resource resource) {
        super(resource);
    }

    public ResultSeverity getSeverity() {
        return ResultSeverity.ERROR;
    }

    /**
     * Default implementation (overriden where possible and meaningful) returning just an empty string
     * @return
     */
    public String getNamespace() {
        return "";
    }
}
