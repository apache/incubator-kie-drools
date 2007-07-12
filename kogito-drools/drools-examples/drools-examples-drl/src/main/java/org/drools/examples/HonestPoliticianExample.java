package org.drools.examples;

import java.io.InputStreamReader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.compiler.PackageBuilder;

public class HonestPoliticianExample {

    /**
     * @param args
     */
    public static void main(final String[] args) throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( HonestPoliticianExample.class.getResourceAsStream( "HonestPolitician.drl" ) ) );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        final StatefulSession session = ruleBase.newStatefulSession();

        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( session );
        logger.setFileName( "log/honest-politician" );

        final Politician blair  = new Politician("blair", true);
        final Politician bush  = new Politician("bush", true);
        final Politician chirac  = new Politician("chirac", true);
        final Politician schroder   = new Politician("schroder", true);
        
        session.insert( blair );
        session.insert( bush );
        session.insert( chirac );
        session.insert( schroder );

        session.fireAllRules();
        
        logger.writeToDisk();
        
        session.dispose();
    }
    
    public static class Politician {
    	private String name;
    	
    	private boolean honest;
    	
    	public Politician() {
    		
    	}
    	
		public Politician(String name, boolean honest) {
			super();
			this.name = name;
			this.honest = honest;
		}
		
		public boolean isHonest() {
			return honest;
		}
		
		public void setHonest(boolean honest) {
			this.honest = honest;
		}

		public String getName() {
			return name;
		}    			    
    }

    public static class Hope {
    	
    	public Hope() {
    		
    	}
    	
    }
    
}
