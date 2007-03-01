package org.drools.base.mvel;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.Consequence;
import org.drools.spi.FieldValue;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PredicateExpression;
import org.drools.spi.ReturnValueExpression;
import org.drools.spi.Tuple;
import org.mvel.MVEL;

public class MVELConsequence
    implements
    Consequence {
    private static final long       serialVersionUID = 320L;

    private final Serializable      expr;
    private final DroolsMVELFactory factory;

    public MVELConsequence(final Serializable expr,
                           final DroolsMVELFactory factory) {
        this.expr = expr;
        this.factory = factory;
    }

    public void evaluate(KnowledgeHelper knowledgeHelper,
                         WorkingMemory workingMemory) throws Exception {
        this.factory.setContext( knowledgeHelper.getTuple(), null, workingMemory );
        MVEL.executeExpression( this.expr,
                                factory );
    }    
}
