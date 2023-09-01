package org.drools.impact.analysis.model.right;

public class ConsequenceAction {

    public enum Type {
        INSERT,
        DELETE,
        MODIFY;
    }

    private final Type type;
    protected final Class<?> actionClass;

    public ConsequenceAction( Type type, Class<?> actionClass ) {
        this.type = type;
        this.actionClass = actionClass;
    }

    public Type getType() {
        return type;
    }

    public Class<?> getActionClass() {
        return actionClass;
    }

    @Override
    public String toString() {
        return "ConsequenceAction{" +
                "type=" + type +
                ", actionClass=" + actionClass.getCanonicalName() +
                '}';
    }
}
