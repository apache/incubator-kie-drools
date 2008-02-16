package org.drools.examples.troubleticket;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.StatelessSession;
import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.drools.examples.decisiontable.Driver;
import org.drools.examples.decisiontable.Policy;

/**
 * This shows off a decision table.
 */
public class TroubleTicketWithDT {

    public static final void main(String[] args) throws Exception {    	
    	TroubleTicketWithDT launcher = new TroubleTicketWithDT();
    	launcher.executeExample();
    }
    
    public void executeExample() throws Exception {
    	
    	//first we compile the decision table into a whole lot of rules.
    	SpreadsheetCompiler compiler = new SpreadsheetCompiler();
    	String drl = compiler.compile(getSpreadsheetStream(), InputType.XLS);

    	//UNCOMMENT ME TO SEE THE DRL THAT IS GENERATED
    	//System.out.println(drl);

    	RuleBase ruleBase = buildRuleBase(drl);
    	
        // typical decision tables are used statelessly
		StatefulSession session = ruleBase.newStatefulSession();
		
        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( session );
        logger.setFileName( "log/trouble_ticket" );

        final Customer a = new Customer( "A",
                                         "Drools",
                                         "Gold" );
        final Customer b = new Customer( "B",
                                         "Drools",
                                         "Platinum" );
        final Customer c = new Customer( "C",
                                         "Drools",
                                         "Silver" );
        final Customer d = new Customer( "D",
                                         "Drools",
                                         "Silver" );

        final Ticket t1 = new Ticket( a );
        final Ticket t2 = new Ticket( b );
        final Ticket t3 = new Ticket( c );
        final Ticket t4 = new Ticket( d );

        session.insert( a );
        session.insert( b );
        session.insert( c );
        session.insert( d );

        session.insert( t1 );
        session.insert( t2 );
        final FactHandle ft3 = session.insert( t3 );
        session.insert( t4 );

        session.fireAllRules();

        t3.setStatus( "Done" );

        session.update( ft3,
                        t3 );

        try {
            System.err.println( "[[ Sleeping 5 seconds ]]" );
            Thread.sleep( 5000 );
        } catch ( final InterruptedException e ) {
            e.printStackTrace();
        }

        System.err.println( "[[ awake ]]" );

        session.dispose();

        logger.writeToDisk();
    	
    }


    /** Build the rule base from the generated DRL */
	private RuleBase buildRuleBase(String drl) throws DroolsParserException, IOException, Exception {
		//now we build the rule package and rulebase, as if they are normal rules
		PackageBuilder builder = new PackageBuilder();
		builder.addPackageFromDrl( new StringReader(drl) );
		
		//add the package to a rulebase (deploy the rule package).
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage( builder.getPackage() );
		return ruleBase;
	}
    

    private InputStream getSpreadsheetStream() {
    	return this.getClass().getResourceAsStream("TroubleTicket.xls");
	}



    
}
