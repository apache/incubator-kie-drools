package org.drools.verifier.visitor;

import java.util.List;

import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.solver.Solvers;

abstract class ConditionalElementDescrVisitor {

    protected final VerifierData data;

    protected VerifierRule       rule;

    protected final Solvers      solvers;

    protected OrderNumber        orderNumber = new OrderNumber();

    public ConditionalElementDescrVisitor(VerifierData data,
                                          Solvers solvers) {
        this.data = data;
        this.solvers = solvers;
    }

    protected void visit(List descrs) throws UnknownDescriptionException {
        for ( Object object : descrs ) {
            visit( object );
        }
    }

    protected abstract void visit(Object descr) throws UnknownDescriptionException;

    protected abstract VerifierComponent getParent();

    protected abstract void visitAndDescr(AndDescr descr) throws UnknownDescriptionException;

    protected abstract void visitOrDescr(OrDescr descr) throws UnknownDescriptionException;

}
class OrderNumber {
    private int orderNumber = -1;

    public int next() {
        return orderNumber++;
    }
}
