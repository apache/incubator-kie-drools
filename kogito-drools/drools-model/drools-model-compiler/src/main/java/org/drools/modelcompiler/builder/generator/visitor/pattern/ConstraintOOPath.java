/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.util.Optional;

import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.OOPathExprGenerator;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;

class ConstraintOOPath implements DSLNode {

    private final RuleContext context;
    private final PackageModel packageModel;
    private final PatternDescr pattern;
    private final Class<?> patternType;
    private final PatternConstraintParseResult patternConstraintParseResult;
    private final String expression;
    private final DrlxParseSuccess drlxParseResult;

    public ConstraintOOPath(RuleContext context, PackageModel packageModel, PatternDescr pattern, Class<?> patternType, PatternConstraintParseResult patternConstraintParseResult, String expression, DrlxParseSuccess drlxParseResult) {
        this.context = context;
        this.packageModel = packageModel;
        this.pattern = pattern;
        this.patternType = patternType;
        this.patternConstraintParseResult = patternConstraintParseResult;
        this.expression = expression;
        this.drlxParseResult = drlxParseResult;
    }

    @Override
    public void buildPattern() {
        final String patternIdentifierGenerated;
        // If the  outer pattern does not have a binding we generate it
        if (patternConstraintParseResult.getPatternIdentifier() != null) {
            patternIdentifierGenerated = patternConstraintParseResult.getPatternIdentifier();
        } else {
            patternIdentifierGenerated = context.getExprId(patternType, expression);
            context.addDeclaration(patternIdentifierGenerated, patternType, Optional.of(pattern), Optional.empty());
        }

        new OOPathExprGenerator(context, packageModel).visit(patternType, patternIdentifierGenerated, drlxParseResult);
    }
}
