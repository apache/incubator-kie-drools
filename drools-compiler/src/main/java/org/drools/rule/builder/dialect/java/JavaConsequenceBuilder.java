/*
 * Copyright 2006 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.rule.builder.dialect.java;

import java.util.Map;

import org.drools.rule.Declaration;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.RuleBuildContext;

import static org.drools.rule.builder.dialect.DialectUtil.fixBlockDescr;
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.*;

public class JavaConsequenceBuilder
    implements
    ConsequenceBuilder {

    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.ConsequenceBuilder#buildConsequence(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.lang.descr.RuleDescr)
     */
    public void build(final RuleBuildContext context, String consequenceName) {

        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        final String className = consequenceName + "Consequence";

        Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations( context.getRule() );

        JavaAnalysisResult analysis = createJavaAnalysisResult(context, consequenceName, decls);

        if ( analysis == null ) {
            // not possible to get the analysis results
            return;
        }
        
        // @TODO <!--(mdp) commented this out until MVEL supports generics.
//        // Set the inputs for each container, this is needed for modifes when the target context is the result of an expression
//        setContainerBlockInputs(context, 
//                                descrs,
//                                analysis.getBlockDescrs(), 
//                                consequenceStr,
//                                bindings,
//                                new HashMap(),
//                                0 );
        // @TODO (mdp) commented this out until MVEL supports generics.-->

        // this will fix modify, retract, insert, update, entrypoints and channels
        String fixedConsequence = fixBlockDescr(context, analysis, decls);

        if ( fixedConsequence == null ) {
            // not possible to rewrite the modify blocks
            return;
        }
        fixedConsequence = KnowledgeHelperFixer.fix( fixedConsequence );
        

        Map<String, Object> map = createConsequenceContext(context, consequenceName, className, fixedConsequence, decls, analysis.getBoundIdentifiers());

        generateTemplates("consequenceMethod",
                "consequenceInvoker",
                context,
                className,
                map,
                context.getRule(),
                context.getRuleDescr());
        // popping Rule.getLHS() from the build stack
        context.getBuildStack().pop();
    }
}
