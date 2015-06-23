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

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.lang.descr.ReturnValueRestrictionDescr;
import org.drools.compiler.rule.builder.ReturnValueBuilder;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.ReturnValueRestriction;
import org.drools.compiler.rule.builder.RuleBuildContext;

import java.util.Map;

import static org.drools.compiler.rule.builder.dialect.java.JavaRuleBuilderHelper.createVariableContext;
import static org.drools.compiler.rule.builder.dialect.java.JavaRuleBuilderHelper.generateMethodTemplate;
import static org.drools.compiler.rule.builder.dialect.java.JavaRuleBuilderHelper.registerInvokerBytecode;

public abstract class AbstractASMReturnValueBuilder implements ReturnValueBuilder {

    public void build(final RuleBuildContext context,
                      final BoundIdentifiers usedIdentifiers,
                      final Declaration[] previousDeclarations,
                      final Declaration[] localDeclarations,
                      final ReturnValueRestriction returnValueRestriction,
                      final ReturnValueRestrictionDescr returnValueRestrictionDescr,
                      final AnalysisResult analysis) {
        final String className = "returnValue" + context.getNextId();
        returnValueRestrictionDescr.setClassMethodName( className );

        final Map vars = createVariableContext(className,
                                               (String) returnValueRestrictionDescr.getContent(),
                                               context,
                                               previousDeclarations,
                                               localDeclarations,
                                               usedIdentifiers.getGlobals());

        generateMethodTemplate("returnValueMethod", context, vars);

        byte[] bytecode = createReturnValueBytecode(context, vars, false);
        registerInvokerBytecode(context, vars, bytecode, returnValueRestriction);
    }

    protected abstract byte[] createReturnValueBytecode(RuleBuildContext context, Map vars, boolean readLocalsFromTuple);
}
