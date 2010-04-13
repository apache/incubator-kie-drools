package org.drools.command.runtime.rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.DisconnectedFactHandle;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

@XmlAccessorType(XmlAccessType.NONE)
public class InsertObjectInEntryPointCommand
        implements
        GenericCommand<FactHandle> {

    private static final long serialVersionUID = 1L;
    @XmlElement
    private Object object;
    @XmlAttribute(name = "out-identifier", required = true)
    private String outIdentifier;
    private boolean returnObject = true;

    public InsertObjectInEntryPointCommand() {
    }

    public InsertObjectInEntryPointCommand(Object object) {
        this.object = object;
    }

    public InsertObjectInEntryPointCommand(Object object, String outIdentifier) {
        super();
        this.object = object;
        this.outIdentifier = outIdentifier;
    }

    public FactHandle execute(Context context) {

        WorkingMemoryEntryPoint ep = ((KnowledgeCommandContext) context).getWorkingMemoryEntryPoint();
        FactHandle factHandle = ep.insert(object);

        DisconnectedFactHandle disconectedHandle = new DisconnectedFactHandle(factHandle.toExternalForm());

        if (outIdentifier != null) {
            if (this.returnObject) {
                ((ExecutionResultImpl) ((KnowledgeCommandContext) context).getExecutionResults()).getResults().put(this.outIdentifier,
                        object);
            }
            ((ExecutionResultImpl) ((KnowledgeCommandContext) context).getExecutionResults()).getFactHandles().put(this.outIdentifier,
                    disconectedHandle.toExternalForm());
        }

        return disconectedHandle;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return this.object;
    }

    public String getOutIdentifier() {
        return this.outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public boolean isReturnObject() {
        return returnObject;
    }

    public void setReturnObject(boolean returnObject) {
        this.returnObject = returnObject;
    }

    public String toString() {
        return "session.insert(" + object + ");";
    }
}
