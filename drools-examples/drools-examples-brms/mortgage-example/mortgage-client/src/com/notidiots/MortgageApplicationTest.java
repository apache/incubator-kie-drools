/**
 * Copyright 2010 JBoss Inc
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
