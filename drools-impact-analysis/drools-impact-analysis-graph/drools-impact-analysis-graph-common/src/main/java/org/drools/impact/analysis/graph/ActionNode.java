package org.drools.impact.analysis.graph;

import org.drools.impact.analysis.model.Rule;
import org.drools.impact.analysis.model.right.ConsequenceAction;
import org.drools.impact.analysis.model.right.ModifyAction;

public class ActionNode extends BaseNode {

    private String id;

    private ConsequenceAction action;

    public ActionNode(Rule rule, ConsequenceAction action, int index) {
        super(rule);
        this.action = action;
        this.id = getFqdn() + ":A" + index;
    }

    public ConsequenceAction getAction() {
        return action;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        //        if (action instanceof ModifyAction) {
        //            return "[A: " + action.getType() + ", " + action.getActionClass().getSimpleName() + ", "+ ((ModifyAction)action).getModifiedProperties() + "]";
        //        } else {
        //            return "[A: " + action.getType() + ", " + action.getActionClass().getSimpleName() + "]";
        //        }

        return "[A: " + action.getType() + ", " + action.getActionClass().getSimpleName() + "]";
    }

    @Override
    public String toString() {
        return "ActionNode [id=" + id + ", action=" + action + "]";
    }

}
