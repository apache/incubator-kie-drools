/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.visitor;

import java.util.List;

import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.OrDescr;
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
