package org.drools.rule.builder.dialect.asm;

import org.drools.compiler.*;
import org.drools.lang.descr.*;
import org.drools.reteoo.*;
import org.drools.rule.*;
import org.drools.rule.builder.*;

import java.util.*;

import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.*;

public abstract class AbstractASMEvalBuilder implements RuleConditionBuilder {
    public RuleConditionElement build(RuleBuildContext context, BaseDescr descr) {
         return build(context, descr, null);
     }

     public RuleConditionElement build(RuleBuildContext context, BaseDescr descr, Pattern prefixPattern) {
         // it must be an EvalDescr
         final EvalDescr evalDescr = (EvalDescr) descr;

         final String className = "eval" + context.getNextId();

         evalDescr.setClassMethodName( className );

         Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations(context.getRule());

         AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                           evalDescr,
                                                                           evalDescr.getContent(),
                                                                           new BoundIdentifiers( context.getDeclarationResolver().getDeclarationClasses( decls ),
                                                                                                 context.getPackageBuilder().getGlobals() ) );
         final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();

         final Declaration[] declarations = decls.values().toArray( new Declaration[decls.size()]);
         Arrays.sort( declarations, RuleTerminalNode.SortDeclarations.instance  );

         final EvalCondition eval = new EvalCondition( declarations );

         final Map vars = createVariableContext(className,
                                               (String)evalDescr.getContent(),
                                               context,
                                               declarations,
                                               null,
                                               usedIdentifiers.getGlobals());

         generateMethodTemplate("evalMethod", context, vars);

         byte[] bytecode = createEvalBytecode(context, vars);
         registerInvokerBytecode(context, vars, bytecode, eval);
         return eval;
     }

    protected abstract byte[] createEvalBytecode(RuleBuildContext context, Map vars);
}
