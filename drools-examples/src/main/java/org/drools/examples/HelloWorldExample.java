package org.drools.examples;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public class HelloWorldExample {

    public static final void main(String[] args) {
        try {

            //load up the rulebase
            RuleBase ruleBase = readRule();
            WorkingMemory workingMemory = ruleBase.newWorkingMemory();

            //go !
            Message message = new Message();
            message.setMessage( "Hello World" );
            message.setStatus( Message.HELLO );
            workingMemory.assertObject( message );
            workingMemory.fireAllRules();

        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

    /**
     * Please note that this is the "low level" rule assembly API.
     */
    private static RuleBase readRule() throws Exception {
        //read in the source
        Reader source = new InputStreamReader( HelloWorldExample.class.getResourceAsStream( "/HelloWorld.drl" ) );

        //optionally read in the DSL (if you are using it).
        //Reader dsl = new InputStreamReader( DroolsTest.class.getResourceAsStream( "/mylang.dsl" ) );

        //Use package builder to build up a rule package.
        //An alternative lower level class called "DrlParser" can also be used...

        PackageBuilder builder = new PackageBuilder();

        //this wil parse and compile in one step
        //NOTE: There are 2 methods here, the one argument one is for normal DRL.
        builder.addPackageFromDrl( source );

        //Use the following instead of above if you are using a DSL:
        //builder.addPackageFromDrl( source, dsl );

        //get the compiled package (which is serializable)
        Package pkg = builder.getPackage();

        //add the package to a rulebase (deploy the rule package).
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        return ruleBase;
    }

    public static class Message {
        public static final int HELLO   = 0;
        public static final int GOODBYE = 1;

        private String          message;

        private int             status;

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

}
