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
package org.drools.mvel.asm;

import java.util.Map;

import org.drools.compiler.compiler.MissingDependencyError;
import org.drools.compiler.rule.builder.ConsequenceBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.base.common.MissingDependencyException;
import org.drools.base.rule.Declaration;
import org.drools.mvel.java.JavaAnalysisResult;
import org.drools.mvel.java.JavaRuleBuilderHelper;

public abstract class AbstractASMConsequenceBuilder implements ConsequenceBuilder {
    public void build(RuleBuildContext context, String consequenceName) {
        // pushing consequence LHS into the stack for variable resolution
        context.getDeclarationResolver().pushOnBuildStack( context.getRule().getLhs() );

        Map<String, Object> vars = consequenceContext(context, consequenceName);
        if (vars == null) {
            return;
        }
        JavaRuleBuilderHelper.generateMethodTemplate("consequenceMethod", context, vars);

        byte[] bytecode = createConsequenceBytecode(context, vars);
        JavaRuleBuilderHelper.registerInvokerBytecode(context, vars, bytecode, context.getRule());

        // popping Rule.getLHS() from the build stack
        context.getDeclarationResolver().popBuildStack();
    }

    private Map<String, Object> consequenceContext(RuleBuildContext context, String consequenceName) {
        String className = consequenceName + "Consequence";
        Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations(context.getRule(), consequenceName);
        JavaAnalysisResult analysis = JavaRuleBuilderHelper.createJavaAnalysisResult(context, consequenceName, decls);

        if ( analysis == null ) {
            // not possible to get the analysis results
            return null;
        }

        // this will fix modify, retract, insert, update, entrypoints and channels
        try {
            String fixedConsequence = KnowledgeHelperFixer.fix(AsmUtil.fixBlockDescr(context, analysis, decls));
            return JavaRuleBuilderHelper.createConsequenceContext(context, consequenceName, className, fixedConsequence, decls, analysis.getBoundIdentifiers());
        } catch (MissingDependencyException e) {
            context.addError(new MissingDependencyError(context.getRuleDescr().getResource(), e));
        }
        return null;
    }

    protected abstract byte[] createConsequenceBytecode(RuleBuildContext ruleContext, final Map<String, Object> consequenceContext);
}
