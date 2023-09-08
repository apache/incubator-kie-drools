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
package org.drools.model.codegen.execmodel.generator.visitor.pattern;

import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseResult;

public class PatternConstraintParseResult {
    private String expression;
    private String patternIdentifier;
    private DrlxParseResult drlxParseResult;

    public PatternConstraintParseResult(String expression, String patternIdentifier, DrlxParseResult drlxParseResult) {
        this.expression = expression;
        this.patternIdentifier = patternIdentifier;
        this.drlxParseResult = drlxParseResult;
    }

    public String getExpression() {
        return expression;
    }

    public String getPatternIdentifier() {
        return patternIdentifier;
    }

    public DrlxParseResult getDrlxParseResult() {
        return drlxParseResult;
    }
}
