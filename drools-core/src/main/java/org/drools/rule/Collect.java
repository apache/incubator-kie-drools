/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.rule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.RuntimeDroolsException;
import org.drools.base.ClassObjectType;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;

public class Collect extends ConditionalElement
    implements
    PatternSource {

    private static final long         serialVersionUID = 510l;

    private Pattern                   sourcePattern;
    private Pattern                   resultPattern;

    private Class<Collection<Object>> cls;

    public Collect() {
    }

    public Collect(final Pattern sourcePattern,
                   final Pattern resultPattern) {

        this.sourcePattern = sourcePattern;
        this.resultPattern = resultPattern;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        sourcePattern = (Pattern) in.readObject();
        resultPattern = (Pattern) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( sourcePattern );
        out.writeObject( resultPattern );
    }

    public Object clone() {
        PatternSource source = this.resultPattern.getSource();
        if ( source == this ) {
            this.resultPattern.setSource( null );
        }
        Pattern clonedResultPattern = (Pattern) this.resultPattern.clone();

        Collect collect = new Collect( (Pattern) this.sourcePattern.clone(),
                                       clonedResultPattern );
        collect.getResultPattern().setSource( collect );
        
        if ( source == this ) {
            this.resultPattern.setSource( this );
        }
        return collect;
    }

    public Pattern getResultPattern() {
        return this.resultPattern;
    }

    public Pattern getSourcePattern() {
        return this.sourcePattern;
    }

    @SuppressWarnings("unchecked")
    public Collection<Object> instantiateResultObject(InternalWorkingMemory wm) throws RuntimeDroolsException {
        try {
            // Collect can only be used with a Collection implementation, so
            // FactTemplateObject type is not allowed
            if ( this.cls == null ) {
                ClassObjectType objType = ((ClassObjectType) this.resultPattern.getObjectType());
                String className = determineResultClassName( objType );
                this.cls = (Class<Collection<Object>>) Class.forName( className,
                                                                      true,
                                                                      ((InternalRuleBase) wm.getRuleBase()).getRootClassLoader() );
            }
            return this.cls.newInstance();
        } catch ( final ClassCastException cce ) {
            throw new RuntimeDroolsException( "Collect CE requires a Collection implementation as return type",
                                              cce );
        } catch ( final InstantiationException e ) {
            throw new RuntimeDroolsException( "Collect CE requires a non-argument constructor for the return type",
                                              e );
        } catch ( final IllegalAccessException e ) {
            throw new RuntimeDroolsException( "Collect CE requires an accessible constructor for the return type",
                                              e );
        } catch ( final ClassNotFoundException e ) {
            throw new RuntimeDroolsException( "Collect CE could not resolve return result class '" + ((ClassObjectType) this.resultPattern.getObjectType()).getClassName() + "'",
                                              e );
        }
    }

    /**
     * If the user uses an interface as a result type, use a default
     * concrete class.
     * 
     * List -> ArrayList
     * Collection -> ArrayList
     * Set -> HashSet
     * 
     * @param objType
     * @return
     */
    private String determineResultClassName(ClassObjectType objType) {
        String className = objType.getClassName();
        if ( List.class.getName().equals( className ) ) {
            className = ArrayList.class.getName();
        } else if ( Set.class.getName().equals( className ) ) {
            className = HashSet.class.getName();
        } else if ( Collection.class.getName().equals( className ) ) {
            className = ArrayList.class.getName();
        }
        return className;
    }

    public Map getInnerDeclarations() {
        return this.sourcePattern.getInnerDeclarations();
    }

    public Map getOuterDeclarations() {
        return Collections.EMPTY_MAP;
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return (Declaration) this.sourcePattern.getInnerDeclarations().get( identifier );
    }

    public List getNestedElements() {
        return Collections.singletonList( this.sourcePattern );
    }

    public boolean isPatternScopeDelimiter() {
        return true;
    }
}
