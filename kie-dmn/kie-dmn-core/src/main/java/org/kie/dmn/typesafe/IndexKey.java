package org.kie.dmn.typesafe;

import org.kie.dmn.api.core.DMNType;

public class IndexKey {

    public final DMNType dmnType;

    public IndexKey(DMNType dmnType) {
        this.dmnType = dmnType;
    }

    public static IndexKey from(DMNType dmnType) {
        return new IndexKey(dmnType);
    }

    public Object getName() {
        return dmnType.getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dmnType == null) ? 0 : dmnType.hashCode());
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
        IndexKey other = (IndexKey) obj;
        if (dmnType == null) {
            if (other.dmnType != null)
                return false;
        } else if (!dmnType.equals(other.dmnType))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "IndexKey [" + getName() + "]";
    }

}
