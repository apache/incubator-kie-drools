package org.drools.workflow.core.node;

import java.util.HashMap;
import java.util.Map;

import org.drools.definition.process.Connection;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.Node;

public class StateNode extends EventBasedNode implements  Constrainable{

	private static final long serialVersionUID = 1L;
    private Map<String,Constraint>            constraints = new HashMap<String,Constraint>();

   
    public void setConstraints(Map<String, Constraint> constraints) {
        this.constraints = constraints;
    }

    public void addConstraint(String name, Constraint constraint) {
        this.constraints.put(name, constraint);
    }
    public Constraint getConstraint(String name){

        return this.constraints.get(name);
    }
    public Map<String,Constraint> getConstraints(){
        return this.constraints;
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {

    }
    

}
