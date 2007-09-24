/*
 * Copyright 2005 JBoss Inc
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.RuntimeDroolsException;
import org.drools.base.ClassObjectType;

/**
 * @author etirelli
 *
 */
public class Collect extends ConditionalElement
    implements
    PatternSource {

    private static final long serialVersionUID = 400L;

    private Pattern           sourcePattern;
    private Pattern           resultPattern;

    public Collect(final Pattern sourcePattern,
                   final Pattern resultPattern) {

        this.sourcePattern = sourcePattern;
        this.resultPattern = resultPattern;
    }

    public Object clone() {
        return new Collect( this.sourcePattern,
                            this.resultPattern );
    }

    public Pattern getResultPattern() {
        return this.resultPattern;
    }

    public Pattern getSourcePattern() {
        return this.sourcePattern;
    }

    public Collection instantiateResultObject() throws RuntimeDroolsException {
        try {
            // Collect can only be used with a Collection implementation, so
            // FactTemplateObject type is not allowed
            return (Collection) ((ClassObjectType) this.resultPattern.getObjectType()).getClassType().newInstance();
        } catch ( final ClassCastException cce ) {
            throw new RuntimeDroolsException( "Collect CE requires a Collection implementation as return type",
                                              cce );
        } catch ( final InstantiationException e ) {
            throw new RuntimeDroolsException( "Collect CE requires a non-argument constructor for the return type",
                                              e );
        } catch ( final IllegalAccessException e ) {
            throw new RuntimeDroolsException( "Collect CE requires an accessible constructor for the return type",
                                              e );
        }
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
}
