package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConditionalBranch extends ConditionalElement implements Externalizable, NamedConsequenceInvoker {

    private EvalCondition condition;

    private NamedConsequence consequence;

    private ConditionalBranch elseBranch;

    public ConditionalBranch() {
    }

    public ConditionalBranch( EvalCondition condition, NamedConsequence consequence, ConditionalBranch elseBranch ) {
        this.condition = condition;
        this.consequence = consequence;
        this.elseBranch = elseBranch;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        condition = (EvalCondition)in.readObject();
        consequence = (NamedConsequence)in.readObject();
        elseBranch = (ConditionalBranch)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(condition);
        out.writeObject(consequence);
        out.writeObject(elseBranch);
    }

    /**
     * There is not reason to clone this object since it is stateless.
     * So a clone() call will return the instance itself.
     *
     * @see org.drools.rule.ConditionalElement#clone()
     */
    @Override
    public ConditionalBranch clone() {
        return this;
    }

    /**
     * It is not possible to declare any new variables, so always
     * return an Empty Map
     *
     * @see org.drools.rule.RuleConditionElement#getInnerDeclarations()
     */
    public Map<String,Declaration> getInnerDeclarations() {
        return Collections.emptyMap();
    }

    /**
     * It is not possible to nest elements inside a conditional branch, so
     * always return an empty list.
     *
     * @see org.drools.rule.RuleConditionElement#getNestedElements()
     */
    public List<RuleConditionElement> getNestedElements() {
        return Collections.emptyList();
    }

    /**
     * It is not possible to declare and export any variables,
     * so always return an empty map
     *
     * @see org.drools.rule.RuleConditionElement#getOuterDeclarations()
     */
    public Map<String,Declaration> getOuterDeclarations() {
        return Collections.emptyMap();
    }

    /**
     * Not possible to resolve any declaration, so always return null.
     *
     * @see org.drools.rule.RuleConditionElement#resolveDeclaration(java.lang.String)
     */
    public Declaration resolveDeclaration(String identifier) {
        return null;
    }

    public boolean invokesConsequence(String consequenceName) {
        return consequence.invokesConsequence(consequenceName) || ( elseBranch != null && elseBranch.invokesConsequence(consequenceName) );
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 31 * condition.hashCode() + 37 * consequence.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final ConditionalBranch other = (ConditionalBranch) obj;
        return condition.equals( other.condition ) && consequence.equals( other.consequence );
    }

    @Override
    public String toString() {
        return "if ( " + condition + " ) " + consequence;
    }

    public boolean isPatternScopeDelimiter() {
        return false;
    }

    public EvalCondition getEvalCondition() {
        return condition;
    }

    public NamedConsequence getNamedConsequence() {
        return consequence;
    }

    public ConditionalBranch getElseBranch() {
        return elseBranch;
    }
}
