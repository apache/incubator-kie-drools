package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NamedConsequence extends ConditionalElement implements NamedConsequenceInvoker, Externalizable {

    private String name;

    private boolean breaking;

    public NamedConsequence() {
    }

    public NamedConsequence( String name, boolean breaking ) {
        this.name = name;
        this.breaking = breaking;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = (String)in.readObject();
        breaking = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( name );
        out.writeBoolean( breaking );
    }

    /**
     * There is not reason to clone this object since it is stateless.
     * So a clone() call will return the instance itself.
     *
     * @see org.drools.rule.ConditionalElement#clone()
     */
    @Override
    public NamedConsequence clone() {
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
     * It is not possible to nest elements inside an entry point, so
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

    public String getConsequenceName() {
        return this.name;
    }

    public boolean invokesConsequence(String consequenceName) {
        return name.equals(consequenceName);
    }

    public boolean isBreaking() {
        return breaking;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return ( breaking ? 1 : 0 ) + ( name == null ? 0 : name.hashCode() );
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final NamedConsequence other = (NamedConsequence) obj;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        return breaking == other.breaking;
    }

    @Override
    public String toString() {
        return (isBreaking() ? " break" : "do") + "[" + name + "]";
    }

    public boolean isPatternScopeDelimiter() {
        return false;
    }
}
