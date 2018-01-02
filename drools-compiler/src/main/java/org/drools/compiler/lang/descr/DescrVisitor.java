package org.drools.compiler.lang.descr;

public interface DescrVisitor {

    void visit(BaseDescr descr);

    void visit(AccumulateDescr descr);

    void visit(AndDescr descr);

    void visit(NotDescr descr);

    void visit(ExistsDescr descr);

    void visit(ForallDescr descr);

    void visit(OrDescr descr);

    void visit(EvalDescr descr);

    void visit(FromDescr descr);

    void visit(NamedConsequenceDescr descr);

    void visit(ConditionalBranchDescr descr);

    void visit(PatternDescr descr);

}
