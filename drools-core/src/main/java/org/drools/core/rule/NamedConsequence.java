/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.rule;

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

    private boolean terminal;

    public NamedConsequence() {
    }

    public NamedConsequence( String name, boolean breaking ) {
        this.name = name;
        this.breaking = breaking;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = (String)in.readObject();
        breaking = in.readBoolean();
        terminal = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( name );
        out.writeBoolean( breaking );
        out.writeBoolean( terminal );
    }

    /**
     * There is not reason to clone this object since it is stateless.
     * So a clone() call will return the instance itself.
     *
     * @see org.drools.core.rule.ConditionalElement#clone()
     */
    @Override
    public NamedConsequence clone() {
        return this;
    }

    /**
     * It is not possible to declare any new variables, so always
     * return an Empty Map
     *
     * @see org.drools.core.rule.RuleConditionElement#getInnerDeclarations()
     */
    public Map<String,Declaration> getInnerDeclarations() {
        return Collections.emptyMap();
    }

    /**
     * It is not possible to nest elements inside an entry point, so
     * always return an empty list.
     *
     * @see org.drools.core.rule.RuleConditionElement#getNestedElements()
     */
    public List<RuleConditionElement> getNestedElements() {
        return Collections.emptyList();
    }

    /**
     * It is not possible to declare and export any variables,
     * so always return an empty map
     *
     * @see org.drools.core.rule.RuleConditionElement#getOuterDeclarations()
     */
    public Map<String,Declaration> getOuterDeclarations() {
        return Collections.emptyMap();
    }

    /**
     * Not possible to resolve any declaration, so always return null.
     *
     * @see org.drools.core.rule.RuleConditionElement#resolveDeclaration(java.lang.String)
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

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
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
