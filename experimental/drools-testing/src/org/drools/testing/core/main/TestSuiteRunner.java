package org.drools.testing.core.main;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilder;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.DefaultAgendaEventListener;
import org.drools.lang.descr.PackageDescr;
import org.drools.testing.core.beans.Scenario;
import org.drools.testing.core.exception.CouldNotParseDrlException;
import org.drools.testing.core.exception.RuleTestLanguageException;
import org.drools.testing.core.filters.MultipleRuleAgendaFilter;
import org.drools.testing.core.rules.model.Account;
import org.drools.testing.core.rules.model.Person;

public class TestSuiteRunner {

	private String file;
	private RuleBase ruleBase;
	
	public TestSuiteRunner (String file) throws RuleTestLanguageException {
		this.file = file;
		buildRuleBase();
	}
	
	/**
	 * Creates a new working memory from the rule base and fires
	 * the specified rules on the TestSuite Object
	 * @throws RuleTestLanguageException
	 */
	public void runTests (Scenario scenario) throws RuleTestLanguageException {
		WorkingMemory wm = ruleBase.newWorkingMemory();
		Collection ruleNames = new ArrayList();
		org.drools.testing.core.beans.Rule[] rules = scenario.getRule();
		for (int i=0; i<rules.length; i++) 
			ruleNames.add(rules[i].getName());
		
		wm.addEventListener( new DefaultAgendaEventListener() {                            
			   public void afterActivationFired(AfterActivationFiredEvent event) {
			       super.afterActivationFired( event );
			       System.out.println( event );
			   }
			   
			});
		
		Account account = new Account();
		account.setStatus("active");
		account.setBalance(new Integer(0));
		wm.assertObject(account);
		
		Person person = new Person();
		person.setAge(new Integer(25));
		wm.assertObject(person);
		
		wm.fireAllRules(new MultipleRuleAgendaFilter(ruleNames));
		wm.dispose();
	}
	
	private void buildRuleBase () throws RuleTestLanguageException {
		
		try {
			//	read in the source
	        Reader reader = new InputStreamReader( TestSuiteRunner.class.getResourceAsStream( file ) );
	        DrlParser parser = new DrlParser();
	        PackageDescr packageDescr = parser.parse( reader );
	        
	
	        //pre build the package
	        PackageBuilder builder = new PackageBuilder();
	        builder.addPackage( packageDescr );
	        org.drools.rule.Package pkg = builder.getPackage();
	
	        //add the package to a rulebase
	        ruleBase = RuleBaseFactory.newRuleBase();
	        ruleBase.addPackage( pkg );
		}catch (Exception e) {
        	throw new CouldNotParseDrlException("Could not parse drl file: "+file,e);
        }
	}
	
}
