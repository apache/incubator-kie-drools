/*
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

public class SelfReferencingConstraint extends BaseKnowledgeBuilderResultImpl {

    public static final String KEY = "selfReferencingConstraint";
    private static final ResultSeverity DEFAULT_SEVERITY = ResultSeverity.WARNING;

    private final ResultSeverity severity;
    private final int[] lines;

    public SelfReferencingConstraint(Resource resource, KnowledgeBuilderConfiguration config, String fieldName, String expression) {
        super(resource, getMessage(fieldName, expression));
        this.severity = resolveSeverity(config);
        this.lines = new int[0];
    }

    private static ResultSeverity resolveSeverity(KnowledgeBuilderConfiguration config) {
        if (config != null && config.getOptionSubKeys(KBuilderSeverityOption.KEY).contains(KEY)) {
            return config.getOption(KBuilderSeverityOption.KEY, KEY).getSeverity();
        }
        return DEFAULT_SEVERITY;
    }

    @Override
    public ResultSeverity getSeverity() {
        return severity;
    }

    @Override
    public int[] getLines() {
        return lines;
    }

    private static String getMessage(String fieldName, String expression) {
        return "Constraint '" + expression + "' compares field '" + fieldName +
                "' to itself within the same pattern, which is always true and likely a bug. " +
                "Consider using a bound variable (e.g. $" + fieldName + ") from another pattern instead.";
    }
}
