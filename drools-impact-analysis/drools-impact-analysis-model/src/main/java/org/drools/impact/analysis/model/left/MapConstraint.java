package org.drools.impact.analysis.model.left;

public class MapConstraint extends Constraint {

    private String key;

    public MapConstraint(Constraint constraint) {
        this.property = constraint.getProperty();
        this.value = constraint.getValue();
        this.type = constraint.getType();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "MapConstraint{" +
               "type=" + type +
               ", property='" + property + '\'' +
               ", key='" + key + '\'' +
               ", value=" + value +
               '}';
    }
}
