package org.drools.base.mvel;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.mvel.MVEL;

public class MVELConsequence
    implements
    Consequence,
    Serializable  {
    private static final long       serialVersionUID = 320L;

    private final Serializable      expr;
    private final DroolsMVELFactory factory;

    public MVELConsequence(final Serializable expr,
                           final DroolsMVELFactory factory) {
        this.expr = expr;
        this.factory = factory;
    }

    public void evaluate(final KnowledgeHelper knowledgeHelper,
                         final WorkingMemory workingMemory) throws Exception {
        this.factory.setContext( knowledgeHelper.getTuple(),
                                 knowledgeHelper,
                                 null,
                                 workingMemory );
        MVEL.executeExpression( this.expr,
                                null,
                                this.factory );
    }
}
