package org.drools.serialization.protobuf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;
import org.kie.api.runtime.Environment;

/**
 * Extension to default <code>MarshallerWriteContext</code> that allows to pass additional
 * information to marshaller strategies, such as process instance id, task it, state
 */
public class ProtobufProcessMarshallerWriteContext extends ProtobufMarshallerWriteContext {
    
    public static final int STATE_ACTIVE = 1;
    public static final int STATE_COMPLETED = 2;

    private String processInstanceId;
    private Long taskId;
    private Long workItemId;
    private int state;
    

    public ProtobufProcessMarshallerWriteContext( OutputStream stream,
                                                  InternalKnowledgeBase kBase,
                                                  InternalWorkingMemory wm,
                                                  Map<Integer, BaseNode> sinks,
                                                  ObjectMarshallingStrategyStore resolverStrategyFactory,
                                                  Environment env) throws IOException {
        super(stream, kBase, wm, sinks, resolverStrategyFactory, env);
    }
    
    public String getProcessInstanceId() {
        return processInstanceId;
    }
    
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    public Long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    public Long getWorkItemId() {
        return workItemId;
    }
    
    public void setWorkItemId(Long workItemId) {
        this.workItemId = workItemId;
    }
    
    public int getState() {
        return state;
    }
    
    public void setState(int state) {
        this.state = state;
    }

}
