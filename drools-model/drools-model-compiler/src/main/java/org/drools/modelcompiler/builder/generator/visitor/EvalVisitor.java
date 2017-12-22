package org.drools.modelcompiler.builder.generator.visitor;

import org.drools.compiler.lang.descr.EvalDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.ModelGenerator;
import org.drools.modelcompiler.builder.generator.RuleContext;

public class EvalVisitor {

    private final RuleContext context;
    private final PackageModel packageModel;

    public EvalVisitor(RuleContext context, PackageModel packageModel) {
        this.context = context;
        this.packageModel = packageModel;
    }

    public void visit(EvalDescr descr) {
        String expression = descr.getContent().toString();
        String bindingId = DrlxParseUtil.findBindingId(expression, context.getAvailableBindings())
                .orElseThrow(() -> new UnsupportedOperationException("unable to parse eval expression: " + expression));
        Class<?> patternType = context.getDeclarationById(bindingId)
                .map(DeclarationSpec::getDeclarationClass)
                .orElseThrow(RuntimeException::new);
        DrlxParseResult drlxParseResult = ModelGenerator.drlxParse(context, packageModel, patternType, bindingId, expression);
        ModelGenerator.processExpression(context, drlxParseResult);
    }


}
