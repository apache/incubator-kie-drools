package org.drools.impact.analysis.model.right;

public class ModifiedMapProperty extends ModifiedProperty {

    // property is map name

    protected final String key;

    public ModifiedMapProperty(String property, String key) {
        this(property, key, null);
    }

    public ModifiedMapProperty(String property, String key, Object value) {
        super(property, value);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "ModifiedMapProperty{" +
               "property='" + property + '\'' +
               ", key='" + key + '\'' +
               ", value=" + value +
               '}';
    }
}
