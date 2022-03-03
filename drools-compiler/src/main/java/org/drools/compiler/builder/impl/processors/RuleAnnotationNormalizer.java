package org.drools.compiler.builder.impl.processors;

import org.drools.drl.ast.descr.AnnotatedBaseDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ConditionalElementDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.PatternDestinationDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;

public class RuleAnnotationNormalizer implements CompilationPhase {
    private final AnnotationNormalizer annotationNormalizer;
    private final PackageDescr packageDescr;

    public RuleAnnotationNormalizer(AnnotationNormalizer annotationNormalizer, PackageDescr packageDescr) {
        this.annotationNormalizer = annotationNormalizer;
        this.packageDescr = packageDescr;
    }

    public void process() {
        for (RuleDescr ruleDescr : packageDescr.getRules()) {
            annotationNormalizer.normalize(ruleDescr);
            traverseAnnotations(ruleDescr.getLhs());
        }
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return annotationNormalizer.getResults();
    }

    private void traverseAnnotations(BaseDescr descr) {
        if (descr instanceof AnnotatedBaseDescr) {
            annotationNormalizer.normalize((AnnotatedBaseDescr) descr);
        }
        if (descr instanceof ConditionalElementDescr) {
            for (BaseDescr baseDescr : ((ConditionalElementDescr) descr).getDescrs()) {
                traverseAnnotations(baseDescr);
            }
        }
        if (descr instanceof PatternDescr && ((PatternDescr) descr).getSource() != null) {
            traverseAnnotations(((PatternDescr) descr).getSource());
        }
        if (descr instanceof PatternDestinationDescr) {
            traverseAnnotations(((PatternDestinationDescr) descr).getInputPattern());
        }
    }
}
