package org.kie.dmn.validation.dtanalysis.model;

import java.util.Objects;

import org.kie.dmn.feel.lang.ast.ASTNode;

/**
 * When an output entry is NOT a constant/literal, but an expression (even a FQN symbol),
 * and we can only hold the node identity.
 */
public class DDTAOutputEntryExpression implements Comparable<DDTAOutputEntryExpression> {

    private final ASTNode baseNode;

    public DDTAOutputEntryExpression(ASTNode n) {
        this.baseNode = n;
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseNode.getText());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DDTAOutputEntryExpression other = (DDTAOutputEntryExpression) obj;
        return Objects.equals(baseNode.getText(), other.baseNode.getText());
    }

    @Override
    public int compareTo(DDTAOutputEntryExpression o) {
        return this.baseNode.getText().compareTo(o.baseNode.getText());
    }

}
