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
        Rule rule;

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
