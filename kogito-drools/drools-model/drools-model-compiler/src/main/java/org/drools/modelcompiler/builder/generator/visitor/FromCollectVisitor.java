package org.drools.modelcompiler.builder.generator.visitor;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.CollectDescr;
import org.drools.compiler.lang.descr.PatternDescr;

public class FromCollectVisitor {

    private final ModelGeneratorVisitor parentVisitor;

    public FromCollectVisitor(ModelGeneratorVisitor parentVisitor) {
        this.parentVisitor = parentVisitor;
    }

    public void trasformFromCollectToCollectList(PatternDescr pattern, CollectDescr collectDescr) {
        // The inner pattern of the "from collect" needs to be processed to have the binding
        final PatternDescr collectDescrInputPattern = collectDescr.getInputPattern();
        parentVisitor.initPattern( collectDescrInputPattern );

        final AccumulateDescr accumulateDescr = new AccumulateDescr();
        accumulateDescr.setInputPattern(collectDescrInputPattern);
        accumulateDescr.addFunction("collectList", null, false, new String[]{collectDescrInputPattern.getIdentifier()});

        final PatternDescr transformedPatternDescr = new PatternDescr(pattern.getObjectType(), pattern.getIdentifier());
        for (BaseDescr o : pattern.getConstraint().getDescrs()) {
            transformedPatternDescr.addConstraint(o);
        }
        transformedPatternDescr.setSource(accumulateDescr);
        transformedPatternDescr.accept(parentVisitor);
    }
}
