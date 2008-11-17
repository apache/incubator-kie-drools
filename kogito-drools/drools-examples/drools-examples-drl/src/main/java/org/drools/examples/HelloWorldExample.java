package org.drools.examples;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.definition.KnowledgePackage;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public class HelloWorldExample {

    public static final void main(final String[] args) throws Exception {
        //read in the source
        final Reader source = new InputStreamReader( HelloWorldExample.class.getResourceAsStream( "HelloWorld.drl" ) );

        final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        //this will parse and compile in one step
        builder.addResource( source,
                             KnowledgeType.DRL );

        // Check the builder for errors
        if ( builder.hasErrors() ) {
            System.out.println( builder.getErrors().toString() );
            throw new RuntimeException( "Unable to compile \"HelloWorld.drl\"." );
        }

        //get the compiled package (which is serializable)
        final Collection<KnowledgePackage> pkgs = builder.getKnowledgePackages();

        //add the package to a rulebase (deploy the rule package).
        final KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( pkgs );

        final StatefulKnowledgeSession session = knowledgeBase.newStatefulKnowledgeSession();
        session.setGlobal( "list",
                           new ArrayList() );

//        session.addEventListener( new DebugAgendaEventListener() );
//        session.addEventListener( new DebugWorkingMemoryEventListener() );
        
//        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( session );
//        logger.setFileName( "log/helloworld" );        

        final Message message = new Message();
        message.setMessage( "Hello World" );
        message.setStatus( Message.HELLO );
        session.insert( message );
        
        session.fireAllRules();
        
//        logger.writeToDisk();
        
        session.dispose();
    }

    public static class Message {
        public static final int HELLO   = 0;
        public static final int GOODBYE = 1;

        private String          message;

        private int             status;

        public Message() {

        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(final String message) {
            this.message = message;
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(final int status) {
            this.status = status;
        }
        
        public static Message doSomething(Message message) {
            return message;
        }
        
        public boolean isSomething(String msg, List list) {
            list.add( this );        
            return this.message.equals( msg );
        }
    }

}
