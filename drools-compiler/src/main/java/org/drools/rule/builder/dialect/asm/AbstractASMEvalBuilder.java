package org.drools.rule.builder.dialect.asm;

import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleConditionBuilder;

import java.util.Arrays;
import java.util.Map;

import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.createVariableContext;
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.generateMethodTemplate;
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.registerInvokerBytecode;

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
