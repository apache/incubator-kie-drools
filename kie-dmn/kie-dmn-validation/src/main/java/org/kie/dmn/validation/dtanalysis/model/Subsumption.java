package org.kie.dmn.validation.dtanalysis.model;

import org.kie.dmn.feel.util.Generated;

public class Subsumption {

    public final int rule;
    public final int includedRule;

    public Subsumption(int rule, int includedRule) {
        super();
        this.rule = rule;
        this.includedRule = includedRule;
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + includedRule;
        result = prime * result + rule;
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
        Subsumption other = (Subsumption) obj;
        if (includedRule != other.includedRule)
            return false;
        if (rule != other.rule)
            return false;
        return true;
    }

}
