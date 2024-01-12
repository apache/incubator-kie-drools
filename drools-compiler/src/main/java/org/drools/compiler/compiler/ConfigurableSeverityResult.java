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

package org.drools.compiler.compiler;

import org.drools.drl.parser.BaseKnowledgeBuilderResultImpl;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.builder.conf.KBuilderSeverityOption;


/**
 *
 */
public abstract class ConfigurableSeverityResult extends BaseKnowledgeBuilderResultImpl {
    
    public ConfigurableSeverityResult(Resource resource, KnowledgeBuilderConfiguration config, String message) {
        super(resource, message);
        severity = config.getOption(KBuilderSeverityOption.KEY, getOptionKey()).getSeverity();
    }
    
    private ResultSeverity severity;
    /* (non-Javadoc)
     * @see org.kie.compiler.DroolsProblem#getProblemType()
     */
    @Override
    public ResultSeverity getSeverity() {
        return severity;
    }
    
    protected abstract String getOptionKey();
  
}
