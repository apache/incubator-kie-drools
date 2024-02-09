/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.base.rule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ValueResolver;

public class Collect extends ConditionalElement
    implements
    PatternSource {

    private static final long         serialVersionUID = 510l;

    private Pattern                   sourcePattern;
    private Pattern                   resultPattern;

    private Class<Collection<Object>> cls;

    public Collect() {
    }

    public Collect(final Pattern sourcePattern, final Pattern resultPattern) {
        this.sourcePattern = sourcePattern;
        this.resultPattern = resultPattern;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        sourcePattern = (Pattern) in.readObject();
        resultPattern = (Pattern) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( sourcePattern );
        out.writeObject( resultPattern );
    }

    public Collect clone() {
        PatternSource source = this.resultPattern.getSource();
        if ( source == this ) {
            this.resultPattern.setSource( null );
        }
        Pattern clonedResultPattern = this.resultPattern.clone();

        Collect collect = new Collect( this.sourcePattern.clone(), clonedResultPattern );
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
    public Collection<Object> instantiateResultObject(ValueResolver valueResolver) throws RuntimeException {
        try {
            // Collect can only be used with a Collection implementation, so
            // FactTemplateObject type is not allowed
            if ( this.cls == null ) {
                ClassObjectType objType = ((ClassObjectType) this.resultPattern.getObjectType());
                String className = determineResultClassName( objType );
                this.cls = (Class<Collection<Object>>) Class.forName( className, true, valueResolver.getRuleBase().getRootClassLoader() );
            }
            return this.cls.newInstance();
        } catch ( final ClassCastException cce ) {
            throw new RuntimeException( "Collect CE requires a Collection implementation as return type", cce );
        } catch ( final InstantiationException e ) {
            throw new RuntimeException( "Collect CE requires a non-argument constructor for the return type", e );
        } catch ( final IllegalAccessException e ) {
            throw new RuntimeException( "Collect CE requires an accessible constructor for the return type", e );
        } catch ( final ClassNotFoundException e ) {
            throw new RuntimeException("Collect CE could not resolve return result class '" + this.resultPattern.getObjectType().getClassName() + "'", e );
        }
    }

    /**
     * If the user uses an interface as a result type, use a default
     * concrete class.
     * 
     * List -> ArrayList
     * Collection -> ArrayList
     * Set -> HashSet
     */
    private String determineResultClassName(ClassObjectType objType) {
        String className = objType.getClassName();
        if ( List.class.getName().equals( className ) ) {
            return ArrayList.class.getName();
        }
        if ( Set.class.getName().equals( className ) ) {
            return HashSet.class.getName();
        }
        if ( Collection.class.getName().equals( className ) ) {
            return ArrayList.class.getName();
        }
        return className;
    }

    public Map<String, Declaration> getInnerDeclarations() {
        return this.sourcePattern.getInnerDeclarations();
    }

    public Map<String, Declaration> getOuterDeclarations() {
        return Collections.EMPTY_MAP;
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return this.sourcePattern.getInnerDeclarations().get( identifier );
    }

    public List<Pattern> getNestedElements() {
        return Collections.singletonList( this.sourcePattern );
    }

    public boolean isPatternScopeDelimiter() {
        return true;
    }

    @Override
    public boolean requiresLeftActivation() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Collect collect = (Collect) o;
        return Objects.equals(sourcePattern, collect.sourcePattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourcePattern);
    }
}
