/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.rule.builder.dialect.asm;

import org.drools.compiler.rule.builder.ConsequenceBuilder;
import org.drools.compiler.rule.builder.dialect.DialectUtil;
import org.drools.compiler.rule.builder.dialect.java.JavaAnalysisResult;
import org.drools.compiler.rule.builder.dialect.java.JavaRuleBuilderHelper;
import org.drools.compiler.rule.builder.dialect.java.KnowledgeHelperFixer;
import org.drools.core.rule.Declaration;
import org.drools.compiler.rule.builder.RuleBuildContext;

import java.util.Map;

import static org.drools.compiler.rule.builder.dialect.DialectUtil.fixBlockDescr;

public abstract class AbstractASMConsequenceBuilder implements ConsequenceBuilder {
    public void build(RuleBuildContext context, String consequenceName) {
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        Map<String, Object> vars = consequenceContext(context, consequenceName);
        if (vars == null) {
            return;
        }
        JavaRuleBuilderHelper.generateMethodTemplate("consequenceMethod", context, vars);

        byte[] bytecode = createConsequenceBytecode(context, vars);
        JavaRuleBuilderHelper.registerInvokerBytecode(context, vars, bytecode, context.getRule());

        // popping Rule.getLHS() from the build stack
        context.getBuildStack().pop();
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
        String fixedConsequence = DialectUtil.fixBlockDescr(context, analysis, decls);

        if ( fixedConsequence == null ) {
            // not possible to rewrite the modify blocks
            return null;
        }
        fixedConsequence = KnowledgeHelperFixer.fix(fixedConsequence);

        return JavaRuleBuilderHelper.createConsequenceContext(context, consequenceName, className, fixedConsequence, decls, analysis.getBoundIdentifiers());
    }

    protected abstract byte[] createConsequenceBytecode(RuleBuildContext ruleContext, final Map<String, Object> consequenceContext);
}
