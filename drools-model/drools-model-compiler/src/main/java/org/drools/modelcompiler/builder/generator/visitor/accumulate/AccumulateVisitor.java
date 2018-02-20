package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;

public abstract class AccumulateVisitor {

    protected final RuleContext context;
    protected final PackageModel packageModel;
    protected final ModelGeneratorVisitor modelGeneratorVisitor;

    public AccumulateVisitor(RuleContext context, ModelGeneratorVisitor modelGeneratorVisitor, PackageModel packageModel) {
        this.context = context;
        this.modelGeneratorVisitor = modelGeneratorVisitor;
        this.packageModel = packageModel;
    }

    public abstract void visit(AccumulateDescr descr, PatternDescr basePattern);
}
