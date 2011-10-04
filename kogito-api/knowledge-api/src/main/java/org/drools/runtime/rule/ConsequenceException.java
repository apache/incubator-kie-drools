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

package org.drools.runtime.rule;

import java.io.PrintStream;
import java.util.Collection;

import org.drools.definition.rule.Rule;

public class ConsequenceException extends RuntimeException {
    private WorkingMemory workingMemory;
    private Activation    activation;    

    public ConsequenceException( final Throwable rootCause,
                                 final WorkingMemory workingMemory,
                                 final Activation activation ){
        super( rootCause );
        this.workingMemory = workingMemory;
        this.activation = activation;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder( "Exception executing consequence for " );
        Rule rule = null;
        
        if( activation != null && ( rule = activation.getRule() ) != null ){
            String packageName = rule.getPackageName();
            String ruleName = rule.getName();
            sb.append( "rule \"" ).append( ruleName ).append( "\" in " ).append( packageName );
        } else {
            sb.append( "rule, name unknown" );
        }
        sb.append( ": " ).append( super.getMessage() );
        return sb.toString();
    }
    
    public Activation getActivation() {
        return this.activation;
    }
    
    public void printFacts(){
        printFacts( System.err );
    }

    public void  printFacts( PrintStream pStream ){
        Collection< ? extends FactHandle> handles = activation.getFactHandles();
        for( FactHandle handle: handles ) {
            Object object = workingMemory.getObject( handle );
            if( object != null ){
                pStream.println( "   Fact " + object.getClass().getSimpleName() +
                                 ": " + object.toString() );
            }
        }
    }    

    public String toString() {
        return getMessage();
    }    
}
