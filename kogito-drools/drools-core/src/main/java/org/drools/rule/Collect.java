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
import java.util.Map;

import org.drools.RuntimeDroolsException;
import org.drools.base.ClassObjectType;

/**
 * @author etirelli
 *
 */
public class Collect extends ConditionalElement {

    private static final long serialVersionUID = 6064290134136990287L;

    private Column            sourceColumn;
    private Column            resultColumn;

    public Collect(final Column sourceColumn,
                   final Column resultColumn) {

        this.sourceColumn = sourceColumn;
        this.resultColumn = resultColumn;
    }

    public Object clone() {
        return new Collect( this.sourceColumn,
                            this.resultColumn );
    }

    public Column getResultColumn() {
        return this.resultColumn;
    }

    public Column getSourceColumn() {
        return this.sourceColumn;
    }

    public Collection instantiateResultObject() throws RuntimeDroolsException {
        try {
            // Collect can only be used with a Collection implementation, so
            // FactTemplateObject type is not allowed
            return (Collection) ((ClassObjectType) this.resultColumn.getObjectType()).getClassType().newInstance();
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
        return this.sourceColumn.getInnerDeclarations();
    }

    public Map getOuterDeclarations() {
        return this.resultColumn.getOuterDeclarations();
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return (Declaration) this.sourceColumn.getInnerDeclarations().get( identifier );
    }
}
