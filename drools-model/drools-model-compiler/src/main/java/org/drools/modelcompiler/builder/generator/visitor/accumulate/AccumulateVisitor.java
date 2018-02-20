package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.PatternDescr;

public interface AccumulateVisitor {

    void visit(AccumulateDescr descr, PatternDescr basePattern);
}
