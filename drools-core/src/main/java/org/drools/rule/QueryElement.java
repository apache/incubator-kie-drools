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

import org.drools.reteoo.LeftTupleSource;
import org.drools.runtime.rule.Variable;

public class QueryElement extends ConditionalElement
    implements
    Externalizable {
    
    private Pattern       resultPattern;
    private String        queryName;
    private Object[]      argTemplate;
    private int[]         declIndexes;
    private int[]         variableIndexes;
    private boolean       openQuery;

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
                        boolean openQuery) {
        this.resultPattern = resultPattern;
        this.queryName = queryName;
        this.argTemplate = argTemplate;
        this.requiredDeclarations = requiredDeclarations;
        this.declIndexes = declIndexes;
        this.variableIndexes = variableIndexes;
        this.openQuery = openQuery;
    }     
    
    
    public void writeExternal(ObjectOutput out) throws IOException {
       out.writeObject( this.resultPattern );
       out.writeObject( this.queryName );
       out.writeObject( this.argTemplate );
       out.writeObject( this.requiredDeclarations );
       out.writeObject( this.declIndexes );
       out.writeObject( this.variableIndexes );
       out.writeBoolean( this.openQuery );
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
    
    public boolean isOpenQuery() {
        return openQuery;
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return this.resultPattern.resolveDeclaration( identifier );
    }

    @Override
    public Object clone() {
        return new QueryElement( resultPattern, queryName, argTemplate, requiredDeclarations, declIndexes, variableIndexes, openQuery );
    }

    @Override
    public String toString() {
        return "QueryElement [resultPattern=" + resultPattern + 
                   ", queryName=" + queryName + ", argTemplate=" + Arrays.toString( argTemplate ) + 
                   ", declIndexes=" + Arrays.toString( declIndexes ) + ", variableIndexes="+ Arrays.toString( variableIndexes ) + 
                   ", openQuery=" + openQuery + 
                   ", requiredDeclarations=" + Arrays.toString( requiredDeclarations ) + "]";
    }




}
