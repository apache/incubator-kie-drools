package org.kie.dmn.validation.dtanalysis.model;

import org.kie.dmn.feel.util.Generated;

public class MisleadingRule {

    public final int misleadingRule;
    public final int misleadRule;

    public MisleadingRule(int misleadingRule, int misleadRule) {
        super();
        this.misleadingRule = misleadingRule;
        this.misleadRule = misleadRule;
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + misleadRule;
        result = prime * result + misleadingRule;
        return result;
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MisleadingRule other = (MisleadingRule) obj;
        if (misleadRule != other.misleadRule)
            return false;
        if (misleadingRule != other.misleadingRule)
            return false;
        return true;
    }

}
