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

package org.kie.api.runtime.rule;

import java.io.PrintStream;
import java.util.Collection;

import org.kie.api.definition.rule.Rule;

public class ConsequenceException extends RuntimeException {
    private RuleRuntime workingMemory;
    private Match    match;

    public ConsequenceException( final Throwable rootCause,
                                 final RuleRuntime workingMemory,
                                 final Match match ){
        super( rootCause );
        this.workingMemory = workingMemory;
        this.match = match;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder( "Exception executing consequence for " );
        Rule rule = null;

        if( match != null && ( rule = match.getRule() ) != null ){
            String packageName = rule.getPackageName();
            String ruleName = rule.getName();
            sb.append( "rule \"" ).append( ruleName ).append( "\" in " ).append( packageName );
        } else {
            sb.append( "rule, name unknown" );
        }
        sb.append( ": " ).append( super.getMessage() );
        return sb.toString();
    }

    public Match getMatch() {
        return this.match;
    }

    public Rule getRule() {
        return this.match.getRule();
    }

    public void printFacts(){
        printFacts( System.err );
    }

    public void  printFacts( PrintStream pStream ){
        Collection< ? extends FactHandle> handles = match.getFactHandles();
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
