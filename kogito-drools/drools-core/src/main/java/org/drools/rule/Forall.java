/*
 * Copyright 2006 JBoss Inc
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The forall conditional element.
 * 
 * @author etirelli
 */
public class Forall extends ConditionalElement {

    private static final long serialVersionUID = -5993305516857875971L;

    // forall base column
    private Column            baseColumn;
    // foral remaining columns
    private List              remainingColumns;

    public Forall() {
        this( null,
              new ArrayList( 1 ) );
    }

    public Forall(final Column baseColumn) {
        this( baseColumn,
              new ArrayList( 1 ) );
    }

    public Forall(final Column baseColumn,
                  final List remainingColumns) {
        this.baseColumn = baseColumn;
        this.remainingColumns = remainingColumns;
    }

    /* (non-Javadoc)
     * @see org.drools.rule.ConditionalElement#clone()
     */
    public Object clone() {
        return new Forall( this.baseColumn,
                           new ArrayList( this.remainingColumns ) );
    }

    /**
     * Forall inner declarations are only provided by the base columns
     * since it negates the remaining columns
     */
    public Map getInnerDeclarations() {
        final Map inner = new HashMap( this.baseColumn.getOuterDeclarations() );
        for ( final Iterator it = this.remainingColumns.iterator(); it.hasNext(); ) {
            inner.putAll( ((Column) it.next()).getOuterDeclarations() );
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
     * Forall can only resolve declarations from its base column
     */
    public Declaration resolveDeclaration(final String identifier) {
        return (Declaration) this.getInnerDeclarations().get( identifier );
    }

    /**
     * @return the baseColumn
     */
    public Column getBaseColumn() {
        return this.baseColumn;
    }

    /**
     * @param baseColumn the baseColumn to set
     */
    public void setBaseColumn(final Column baseColumn) {
        this.baseColumn = baseColumn;
    }

    /**
     * @return the remainingColumns
     */
    public List getRemainingColumns() {
        return this.remainingColumns;
    }

    /**
     * @param remainingColumns the remainingColumns to set
     */
    public void setRemainingColumns(final List remainingColumns) {
        this.remainingColumns = remainingColumns;
    }

    /**
     * Adds one more column to the list of remaining columns
     * @param column
     */
    public void addRemainingColumn(final Column column) {
        this.remainingColumns.add( column );
    }

}
