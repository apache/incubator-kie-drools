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

package org.drools.core.rule;

import org.kie.api.runtime.rule.Variable;

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
    private Object[]      argTemplate;
    private int[]         declIndexes;
    private int[]         variableIndexes;
    private boolean       openQuery;
    private boolean       abductive;

    private Declaration[] requiredDeclarations;

    public QueryElement() {
        // for serialisation
    }
    
    public QueryElement(Pattern       resultPattern,
                        String queryName,
                        Object[] argTemplate,
                        Declaration[] requiredDeclarations,
                        int[] declIndexes,
                        int[] variableIndexes, 
                        boolean openQuery,
                        boolean abductive) {
        this.resultPattern = resultPattern;
        this.queryName = queryName;
        this.argTemplate = argTemplate;
        this.requiredDeclarations = requiredDeclarations;
        this.declIndexes = declIndexes;
        this.variableIndexes = variableIndexes;
        this.openQuery = openQuery;
        this.abductive = abductive;
    }
    
    
    public void writeExternal(ObjectOutput out) throws IOException {
       out.writeObject( this.resultPattern );
       out.writeObject( this.queryName );
       out.writeObject( this.argTemplate );
       out.writeObject( this.requiredDeclarations );
       out.writeObject( this.declIndexes );
       out.writeObject( this.variableIndexes );
       out.writeBoolean( this.openQuery );
        out.writeBoolean( this.abductive );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.resultPattern = ( Pattern ) in.readObject();
        this.queryName = (String) in.readObject();
        this.argTemplate = (Object[]) in.readObject();
        for ( int i = 0; i < argTemplate.length; i++ ) {
            if ( argTemplate[i] instanceof Variable ) {
                argTemplate[i] = Variable.v; // we need to reset this as we do == checks later in DroolsQuery
            }
        }
        this.requiredDeclarations = ( Declaration[] ) in.readObject();
        this.declIndexes = ( int[] ) in.readObject();
        this.variableIndexes = ( int[] ) in.readObject();
        this.openQuery = in.readBoolean();
        this.abductive = in.readBoolean();
    }
    

    public String getQueryName() {
        return queryName;
    }

    public Object[] getArgTemplate() {
        return argTemplate;
    }

    public int[] getDeclIndexes() {
        return declIndexes;
    }
    
    public void setVariableIndexes(int[] varIndexes) {
        variableIndexes = varIndexes;
    }

    public int[] getVariableIndexes() {
        return variableIndexes;
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
        return new QueryElement( resultPattern.clone(), queryName, argTemplate, requiredDeclarations, declIndexes, variableIndexes, openQuery, abductive );
    }

    @Override
    public String toString() {
        return "QueryElement [resultPattern=" + resultPattern + 
                   ", queryName=" + queryName + ", argTemplate=" + Arrays.toString( argTemplate ) + 
                   ", declIndexes=" + Arrays.toString( declIndexes ) + ", variableIndexes="+ Arrays.toString( variableIndexes ) + 
                   ", openQuery=" + openQuery + 
                   ", abductive=" + abductive +
                   ", requiredDeclarations=" + Arrays.toString( requiredDeclarations ) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode( argTemplate );
        result = prime * result + Arrays.hashCode( declIndexes );
        result = prime * result + (openQuery ? 1231 : 1237);
        result = prime * result + (abductive ? 1231 : 1237);
        result = prime * result + ((queryName == null) ? 0 : queryName.hashCode());
        result = prime * result + Arrays.hashCode( requiredDeclarations );
        result = prime * result + ((resultPattern == null) ? 0 : resultPattern.hashCode());
        result = prime * result + Arrays.hashCode( variableIndexes );
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        QueryElement other = (QueryElement) obj;
        if ( !Arrays.equals( argTemplate,
                             other.argTemplate ) ) return false;
        if ( !Arrays.equals( declIndexes,
                             other.declIndexes ) ) return false;
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
        if ( !Arrays.equals( variableIndexes,
                             other.variableIndexes ) ) return false;
        return true;
    }

}
