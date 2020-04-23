package org.kie.dmn.typesafe;

import org.kie.dmn.api.core.DMNType;

public class DMNTypeKey {

    public final String namespace;
    public final String name;

    private DMNTypeKey(String ns, String name) {
        this.namespace = ns;
        this.name = name;
    }

    public static DMNTypeKey from(DMNType dmnType) {
        return new DMNTypeKey(dmnType.getNamespace(), dmnType.getName());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DMNTypeKey other = (DMNTypeKey) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (namespace == null) {
            if (other.namespace != null)
                return false;
        } else if (!namespace.equals(other.namespace))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DMNTypeKey [name=" + name + ", namespace=" + namespace + "]";
    }

}
