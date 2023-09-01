package org.drools.model.codegen.execmodel.generator.visitor;

import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.CollectDescr;
import org.drools.drl.ast.descr.PatternDescr;

public class FromCollectVisitor {

    public static final String GENERIC_COLLECT = "genericCollect";

    private final ModelGeneratorVisitor parentVisitor;

    public FromCollectVisitor(ModelGeneratorVisitor parentVisitor) {
        this.parentVisitor = parentVisitor;
    }

    public void transformFromCollectToCollectList(PatternDescr pattern, CollectDescr collectDescr) {
        // The inner pattern of the "from collect" needs to be processed to have the binding
        final PatternDescr collectDescrInputPattern = collectDescr.getInputPattern();
        if (!parentVisitor.initPattern( collectDescrInputPattern )) {
            return;
        }

        String collectTarget = pattern.getObjectType();

        final AccumulateDescr accumulateDescr = new AccumulateDescr();
        accumulateDescr.setInputPattern(collectDescrInputPattern);
        accumulateDescr.addFunction(getCollectFunction(collectTarget), null, false, new String[]{collectDescrInputPattern.getIdentifier()});

        final PatternDescr transformedPatternDescr = new PatternDescr(collectTarget, pattern.getIdentifier());
        for (BaseDescr o : pattern.getConstraint().getDescrs()) {
            transformedPatternDescr.addConstraint(o);
        }
        transformedPatternDescr.setSource(accumulateDescr);
        transformedPatternDescr.accept(parentVisitor);
    }

    private static String getCollectFunction(String collectTarget) {
        switch (collectTarget) {
            case "Collection":
            case "java.util.Collection":
            case "List":
            case "java.util.List":
                return "collectList";
            case "Set":
            case "java.util.Set":
                return "collectSet";
            default: return GENERIC_COLLECT;
        }
    }
}
