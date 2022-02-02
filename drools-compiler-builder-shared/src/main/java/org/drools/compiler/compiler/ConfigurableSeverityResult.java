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

import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.builder.conf.KBuilderSeverityOption;
import org.kie.api.io.Resource;


/**
 *
 */
public abstract class ConfigurableSeverityResult extends BaseKnowledgeBuilderResultImpl {
    
    public ConfigurableSeverityResult(Resource resource, KnowledgeBuilderConfiguration config) {
        super(resource);
        severity = config.getOption(KBuilderSeverityOption.class, getOptionKey()).getSeverity();
    }
    
    private ResultSeverity severity;
    /* (non-Javadoc)
     * @see org.kie.compiler.DroolsProblem#getProblemType()
     */
    @Override
    public ResultSeverity getSeverity() {
        return severity;
    }
    
    abstract String getOptionKey();
  
}
