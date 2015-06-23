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
     * @see org.kie.rule.ConditionalElement#clone()
     */
    @Override
    public ConditionalBranch clone() {
        return new ConditionalBranch(condition.clone(), consequence.clone(), (elseBranch == null ) ? null : elseBranch.clone());
    }

    /**
     * It is not possible to declare any new variables, so always
     * return an Empty Map
     *
     * @see org.kie.rule.RuleConditionElement#getInnerDeclarations()
     */
    public Map<String,Declaration> getInnerDeclarations() {
        return Collections.emptyMap();
    }

    /**
     * It is not possible to nest elements inside a conditional branch, so
     * always return an empty list.
     *
     * @see org.kie.rule.RuleConditionElement#getNestedElements()
     */
    public List<RuleConditionElement> getNestedElements() {
        return Collections.emptyList();
    }

    /**
     * It is not possible to declare and export any variables,
     * so always return an empty map
     *
     * @see org.kie.rule.RuleConditionElement#getOuterDeclarations()
     */
    public Map<String,Declaration> getOuterDeclarations() {
        return Collections.emptyMap();
    }

    /**
     * Not possible to resolve any declaration, so always return null.
     *
     * @see org.kie.rule.RuleConditionElement#resolveDeclaration(java.lang.String)
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
