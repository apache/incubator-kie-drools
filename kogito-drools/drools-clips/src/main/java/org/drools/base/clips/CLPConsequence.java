package org.drools.base.clips;

import org.drools.WorkingMemory;
import org.drools.clips.BlockExecutionEngine;
import org.drools.clips.ExecutionContext;
import org.drools.clips.ExecutionContextImpl;
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
