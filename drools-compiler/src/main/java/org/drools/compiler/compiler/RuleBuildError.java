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

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.base.definitions.rule.impl.RuleImpl;

public class RuleBuildError extends DescrBuildError {
    private final RuleImpl rule;

    public RuleBuildError(final RuleImpl rule,
                          final BaseDescr descr,
                          final Object object,
                          final String message) {
        super( descr,
               descr,
               object,
               message );
        this.rule = rule;
    }

    public RuleImpl getRule() {
        return this.rule;
    }
}
