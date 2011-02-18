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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class QueryElement extends ConditionalElement
    implements
    Externalizable {
    
    private Pattern       resultPattern;
    private String        queryName;
    private Object[]      arguments;
    private int[]         declIndexes;
    private int[]         variables;

    private Declaration[] requiredDeclarations;

    public QueryElement(Pattern       resultPattern,
                        String queryName,
                        Object[] arguments,
                        Declaration[] requiredDeclarations,
                        int[] declIndexes,
                        int[] variables) {
        super();
        this.resultPattern = resultPattern;
        this.queryName = queryName;
        this.arguments = arguments;
        this.requiredDeclarations = requiredDeclarations;
        this.declIndexes = declIndexes;
        this.variables = variables;
    }

    public String getQueryName() {
        return queryName;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public int[] getDeclIndexes() {
        return declIndexes;
    }

    public int[] getVariables() {
        return variables;
    }

    public Map getInnerDeclarations() {
        return this.resultPattern.getInnerDeclarations();
    }

    public Map getOuterDeclarations() {
        return this.resultPattern.getOuterDeclarations();
    }

    public List getNestedElements() {
        return Collections.EMPTY_LIST;
    }
    
    public Pattern getResultPattern() {
        return this.resultPattern;
    }

    public boolean isPatternScopeDelimiter() {
        return true;
    }
    
    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return this.resultPattern.resolveDeclaration( identifier );
    }

    public void replaceDeclaration(Declaration declaration,
                                   Declaration resolved) {
        for ( int i = 0; i < this.requiredDeclarations.length; i++ ) {
            if ( this.requiredDeclarations[i].equals( declaration ) ) {
                this.requiredDeclarations[i] = resolved;
            }
        }
    }


    @Override
    public Object clone() {
        return new QueryElement( resultPattern, queryName, arguments, requiredDeclarations, declIndexes, variables );
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
       out.writeObject( this.resultPattern );
       out.writeObject( this.queryName );
       out.writeObject( this.arguments );
       out.writeObject( this.requiredDeclarations );
       out.writeObject( this.declIndexes );
       out.writeObject( this.variables );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.resultPattern = ( Pattern ) in.readObject();
        this.queryName = (String) in.readObject();
        this.arguments = (Object[]) in.readObject();
        this.requiredDeclarations = ( Declaration[] ) in.readObject();
        this.declIndexes = ( int[] ) in.readObject();
        this.variables = ( int[] ) in.readObject();
    }



}
