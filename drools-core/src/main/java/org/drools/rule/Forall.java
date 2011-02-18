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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The forall conditional element.
 */
public class Forall extends ConditionalElement {

    private static final long serialVersionUID = 510l;

    // forall base pattern
    private Pattern            basePattern;
    // foral remaining patterns
    private List              remainingPatterns;

    public Forall() {
        this( null,
              new ArrayList( 1 ) );
    }

    public Forall(final Pattern basePattern) {
        this( basePattern,
              new ArrayList( 1 ) );
    }

    public Forall(final Pattern basePattern,
                  final List remainingPatterns) {
        this.basePattern = basePattern;
        this.remainingPatterns = remainingPatterns;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        basePattern = (Pattern)in.readObject();
        remainingPatterns = (List)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(basePattern);
        out.writeObject(remainingPatterns);
    }
    /* (non-Javadoc)
     * @see org.drools.rule.ConditionalElement#clone()
     */
    public Object clone() {
        return new Forall( this.basePattern,
                           new ArrayList( this.remainingPatterns ) );
    }

    /**
     * Forall inner declarations are only provided by the base patterns
     * since it negates the remaining patterns
     */
    public Map getInnerDeclarations() {
        final Map inner = new HashMap( this.basePattern.getOuterDeclarations() );
        for ( final Iterator it = this.remainingPatterns.iterator(); it.hasNext(); ) {
            inner.putAll( ((Pattern) it.next()).getOuterDeclarations() );
        }
        return inner;
    }

    /**
     * Forall does not export any declarations
     */
    public Map getOuterDeclarations() {
        return Collections.EMPTY_MAP;
    }

    /**
     * Forall can only resolve declarations from its base pattern
     */
    public Declaration resolveDeclaration(final String identifier) {
        return (Declaration) this.getInnerDeclarations().get( identifier );
    }

    /**
     * @return the basePattern
     */
    public Pattern getBasePattern() {
        return this.basePattern;
    }

    /**
     * @param basePattern the basePattern to set
     */
    public void setBasePattern(final Pattern basePattern) {
        this.basePattern = basePattern;
    }

    /**
     * @return the remainingPatterns
     */
    public List getRemainingPatterns() {
        return this.remainingPatterns;
    }

    /**
     * @param remainingPatterns the remainingPatterns to set
     */
    public void setRemainingPatterns(final List remainingPatterns) {
        this.remainingPatterns = remainingPatterns;
    }

    /**
     * Adds one more pattern to the list of remaining patterns
     * @param pattern
     */
    public void addRemainingPattern(final Pattern pattern) {
        this.remainingPatterns.add( pattern );
    }

    public List getNestedElements() {
        List elements = new ArrayList( 1 + this.remainingPatterns.size() );
        elements.add( this.basePattern );
        elements.addAll( this.remainingPatterns );
        return elements;
    }

    public boolean isPatternScopeDelimiter() {
        return true;
    }
    
    @Override
    public String toString() {
        return "forall( "+basePattern+" "+remainingPatterns+" )";
    }

}
