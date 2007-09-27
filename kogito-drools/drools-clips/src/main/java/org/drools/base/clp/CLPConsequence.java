package org.drools.base.clp;

import org.drools.WorkingMemory;
import org.drools.clp.BlockExecutionEngine;
import org.drools.clp.ExecutionContext;
import org.drools.clp.ExecutionContextImpl;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;

public class CLPConsequence
    implements
    Consequence {

    private final BlockExecutionEngine engine;
    private final int                        varSize;

    public CLPConsequence(BlockExecutionEngine engine,
                          int varSize) {
        this.engine = engine;
        this.varSize = varSize;
    }

    public void evaluate(KnowledgeHelper knowledgeHelper,
                         WorkingMemory workingMemory) throws Exception {
        ExecutionContext context = new ExecutionContextImpl( (InternalWorkingMemory) workingMemory,
                                                             (ReteTuple) knowledgeHelper.getTuple(),
                                                             varSize );
        engine.execute( context );
    }

}
