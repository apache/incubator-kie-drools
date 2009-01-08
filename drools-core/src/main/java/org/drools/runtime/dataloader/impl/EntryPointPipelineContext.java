/**
 * 
 */
package org.drools.runtime.dataloader.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemoryEntryPoint;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.ResultHandler;
import org.drools.runtime.pipeline.impl.BasePipelineContext;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public class EntryPointPipelineContext extends BasePipelineContext
    implements
    PipelineContext {
    private Map                     handles;
    private WorkingMemoryEntryPoint entryPoint;
    private ResultHandler           resultHandler;

    public EntryPointPipelineContext(WorkingMemoryEntryPoint entryPoint,
                                     ResultHandler resultHandler) {
        super( ((InternalRuleBase) ((InternalWorkingMemoryEntryPoint) entryPoint).getRuleBase()).getRootClassLoader() );
        this.handles = new HashMap<FactHandle, Object>();
        this.entryPoint = entryPoint;
        this.resultHandler = resultHandler;
    }

    public Map getHandles() {
        return handles;
    }

    public WorkingMemoryEntryPoint getEntryPoint() {
        return entryPoint;
    }

    public ResultHandler getResultHandler() {
        return resultHandler;
    }

}