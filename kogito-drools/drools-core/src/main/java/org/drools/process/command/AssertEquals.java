package org.drools.process.command;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.command.Command;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.AgendaFilter;
import org.mvel2.MVEL;

public class AssertEquals
    implements
    GenericCommand<Void> {

    private String  message;
    private Object  expectedObject;
    private String  expectedIdentifier;

    private Command command;
    private String  mvelString;

    public AssertEquals(String message,
                        Object expectedObject,
                        Command command,
                        String mvelString) {
        this.message = message;
        this.expectedObject = expectedObject;
        this.command = command;
        this.mvelString = mvelString;
    }

    public AssertEquals(String message,
                        String expectedIdentifier,
                        Command command,
                        String mvelString) {
        this.message = message;
        this.expectedIdentifier = expectedIdentifier;
        this.command = command;
        this.mvelString = mvelString;
    }

    public Void execute(Context context) {
        Object actualObject = ((GenericCommand) command).execute( context );

        if ( this.mvelString != null ) {
            actualObject = MVEL.eval( this.mvelString,
                                      actualObject );
        }

        if ( this.expectedIdentifier != null ) {
            this.expectedObject = context.get( this.expectedIdentifier );
        }

        Map vars = new HashMap();
        vars.put( "expected",
                  expectedObject );
        vars.put( "actual",
                  actualObject );

        Assert.assertTrue( this.message,
                           (Boolean) MVEL.eval( "expected == actual",
                                                vars ) );

        return null;
    }

    public String toString() {
        return "assert";
    }

}
