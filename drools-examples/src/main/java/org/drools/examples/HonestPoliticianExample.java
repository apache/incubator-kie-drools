package org.drools.examples;

import java.io.InputStreamReader;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
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

        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( workingMemory );
        logger.setFileName( "log/honest-politician" );

        final Politician blair  = new Politician("blair", true);
        final Politician bush  = new Politician("bush", true);
        final Politician chirac  = new Politician("chirac", true);
        final Politician schroder   = new Politician("schroder", true);
        
        workingMemory.assertObject( blair );
        workingMemory.assertObject( bush );
        workingMemory.assertObject( chirac );
        workingMemory.assertObject( schroder );

        workingMemory.fireAllRules();
        
        logger.writeToDisk();
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
