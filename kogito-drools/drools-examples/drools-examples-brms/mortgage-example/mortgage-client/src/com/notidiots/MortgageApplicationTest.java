package com.notidiots;

import org.drools.RuleBase;
import org.drools.agent.RuleAgent;
import org.drools.definition.type.FactType;

public class MortgageApplicationTest {

    
	/**
	 * @param args
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
	    
		RuleAgent agent = RuleAgent.newRuleAgent("/mortgageapproval.properties");
		RuleBase rb = agent.getRuleBase();

		FactType appType = rb.getFactType("mortgages.LoanApplication");
		FactType incomeType = rb.getFactType("mortgages.IncomeSource");

		Object application = appType.newInstance();
		Object income = incomeType.newInstance();

		appType.set(application, "amount", 25000);
		appType.set(application, "deposit", 1500);
		appType.set(application, "lengthYears", 20);

		incomeType.set(income, "type", "Job");
		incomeType.set(income, "amount", 65000);


		rb.newStatelessSession().execute(new Object[] {application, income});

		System.out.println(application);
	}

}
