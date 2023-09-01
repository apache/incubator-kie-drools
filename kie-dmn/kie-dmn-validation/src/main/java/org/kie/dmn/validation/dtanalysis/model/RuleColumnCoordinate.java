package org.kie.dmn.validation.dtanalysis.model;

import org.kie.dmn.feel.util.Generated;

public class RuleColumnCoordinate {
    public final int rule;
    public final int column;
    public final String feelText;

    public RuleColumnCoordinate(int rule, int column, String feelText) {
        super();
        this.rule = rule;
        this.column = column;
        this.feelText = feelText;
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + column;
        result = prime * result + ((feelText == null) ? 0 : feelText.hashCode());
        result = prime * result + rule;
        return result;
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RuleColumnCoordinate other = (RuleColumnCoordinate) obj;
        if (column != other.column) {
            return false;
        }
        if (feelText == null) {
            if (other.feelText != null) {
                return false;
            }
        } else if (!feelText.equals(other.feelText)) {
            return false;
        }
        if (rule != other.rule) {
            return false;
        }
        return true;
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RuleColumnCoordinate [rule=");
        builder.append(rule);
        builder.append(", column=");
        builder.append(column);
        builder.append(", feelText=");
        builder.append(feelText);
        builder.append("]");
        return builder.toString();
    }


}
