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

import java.util.Optional;

import org.drools.drl.ast.descr.PatternDescr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.generator.OOPathExprGenerator;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.visitor.DSLNode;

class ConstraintOOPath implements DSLNode {

    private final RuleContext context;
    private final PackageModel packageModel;
    private final PatternDescr pattern;
    private final Class<?> patternType;
    private final PatternConstraintParseResult patternConstraintParseResult;
    private final String expression;
    private final DrlxParseSuccess drlxParseResult;

    public ConstraintOOPath(RuleContext context, PackageModel packageModel, PatternDescr pattern, Class<?> patternType, PatternConstraintParseResult patternConstraintParseResult, DrlxParseSuccess drlxParseResult) {
        this.context = context;
        this.packageModel = packageModel;
        this.pattern = pattern;
        this.patternType = patternType;
        this.patternConstraintParseResult = patternConstraintParseResult;
        this.expression = patternConstraintParseResult.expression();
        this.drlxParseResult = drlxParseResult;
    }

    @Override
    public void buildPattern() {
        final String patternIdentifierGenerated;
        // If the  outer pattern does not have a binding we generate it
        if (patternConstraintParseResult.patternIdentifier() != null) {
            patternIdentifierGenerated = patternConstraintParseResult.patternIdentifier();
        } else {
            patternIdentifierGenerated = context.getExprId(patternType, expression);
            context.addDeclaration(patternIdentifierGenerated, patternType, Optional.of(pattern), Optional.empty());
        }

        new OOPathExprGenerator(context, packageModel).visit(patternType, patternIdentifierGenerated, drlxParseResult);
    }
}
