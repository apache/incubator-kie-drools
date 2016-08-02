/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.rule;

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
    
    private Pattern         resultPattern;
    private String          queryName;
    private QueryArgument[] arguments;
    private int[]           variableIndexes;
    private boolean         openQuery;
    private boolean         abductive;

    private Declaration[] requiredDeclarations;

    public QueryElement() {
        // for serialisation
    }
    
    public QueryElement(Pattern resultPattern,
                        String queryName,
                        QueryArgument[] arguments,
                        int[] variableIndexes,
                        Declaration[] requiredDeclarations,
                        boolean openQuery,
                        boolean abductive) {
        this.resultPattern = resultPattern;
        this.queryName = queryName;
        this.arguments = arguments;
        this.variableIndexes = variableIndexes;
        this.requiredDeclarations = requiredDeclarations;
        this.openQuery = openQuery;
        this.abductive = abductive;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
       out.writeObject( this.resultPattern );
       out.writeObject( this.queryName );
       out.writeObject( this.arguments );
       out.writeObject( this.variableIndexes );
       out.writeObject( this.requiredDeclarations );
       out.writeBoolean( this.openQuery );
       out.writeBoolean( this.abductive );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.resultPattern = ( Pattern ) in.readObject();
        this.queryName = (String) in.readObject();
        this.arguments = (QueryArgument[]) in.readObject();
        this.variableIndexes = (int[]) in.readObject();
        this.requiredDeclarations = ( Declaration[] ) in.readObject();
        this.openQuery = in.readBoolean();
        this.abductive = in.readBoolean();
    }

    public int[] getVariableIndexes() {
        return variableIndexes;
    }

    public void setVariableIndexes( int[] variableIndexes ) {
        this.variableIndexes = variableIndexes;
    }

    public String getQueryName() {
        return queryName;
    }

    public QueryArgument[] getArguments() {
        return arguments;
    }

    public Map<String,Declaration> getInnerDeclarations() {
        return this.resultPattern.getInnerDeclarations();
    }

    public Map<String,Declaration> getOuterDeclarations() {
        return this.resultPattern.getOuterDeclarations();
    }

    public List<? extends RuleConditionElement> getNestedElements() {
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
    
    public boolean isOpenQuery() {
        return openQuery;
    }

    public boolean isAbductive() {
        return abductive;
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return this.resultPattern.resolveDeclaration( identifier );
    }

    @Override
    public QueryElement clone() {
        return new QueryElement( resultPattern.clone(), queryName, arguments, variableIndexes, requiredDeclarations, openQuery, abductive );
    }

    @Override
    public String toString() {
        return "QueryElement [resultPattern=" + resultPattern + 
                   ", queryName=" + queryName + ", argTemplate=" + Arrays.toString( arguments ) +
                   ", openQuery=" + openQuery +
                   ", abductive=" + abductive +
                   ", requiredDeclarations=" + Arrays.toString( requiredDeclarations ) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode( arguments );
        result = prime * result + (openQuery ? 1231 : 1237);
        result = prime * result + (abductive ? 1231 : 1237);
        result = prime * result + ((queryName == null) ? 0 : queryName.hashCode());
        result = prime * result + Arrays.hashCode( requiredDeclarations );
        result = prime * result + ((resultPattern == null) ? 0 : resultPattern.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        QueryElement other = (QueryElement) obj;
        if ( !Arrays.equals( arguments,
                             other.arguments ) ) return false;
        if ( openQuery != other.openQuery ) return false;
        if ( abductive != other.abductive ) return false;
        if ( queryName == null ) {
            if ( other.queryName != null ) return false;
        } else if ( !queryName.equals( other.queryName ) ) return false;
        if ( !Arrays.equals( requiredDeclarations,
                             other.requiredDeclarations ) ) return false;
        if ( resultPattern == null ) {
            if ( other.resultPattern != null ) return false;
        } else if ( !resultPattern.equals( other.resultPattern ) ) return false;
        return true;
    }
}
