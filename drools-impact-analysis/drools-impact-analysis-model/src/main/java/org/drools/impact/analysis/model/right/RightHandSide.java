package org.drools.impact.analysis.model.right;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class RightHandSide {

    private final List<ConsequenceAction> actions = new ArrayList<>();

    public void addAction(ConsequenceAction action) {
        actions.add(action);
    }

    public List<ConsequenceAction> getActions() {
        return actions;
    }

    @Override
    public String toString() {
        return "RightHandSide{" +
                "actions=" + actions.stream().map( Object::toString ).collect( joining("\n", ",\n", "") ) +
                '}';
    }
}
