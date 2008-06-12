package org.drools.workflow.core.node;

import org.drools.process.core.Work;
import org.drools.process.core.impl.WorkImpl;

public class HumanTaskNode extends WorkItemNode {

    private static final long serialVersionUID = 4L;

    private String swimlane;
    
    public HumanTaskNode() {
        Work work = new WorkImpl();
        work.setName("Human Task");
        setWork(work);
    }

    public String getSwimlane() {
        return swimlane;
    }

    public void setSwimlane(String swimlane) {
        this.swimlane = swimlane;
    }

}
