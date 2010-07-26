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

package org.drools.assistant;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.assistant.option.AssistantOption;
import org.drools.assistant.option.ReplaceAssistantOption;
import org.drools.assistant.processor.AbstractRuleAssistantProcessor;
import org.drools.assistant.processor.DRLRefactorProcessor;

public class DRLAssistantTest extends TestCase {

	private AbstractRuleAssistantProcessor ruleAssistant;
	private String rule;

	@Override
	protected void setUp() throws Exception {
		ruleAssistant = new DRLRefactorProcessor();
		rule = 	"package org.drools.assistant.test;\n\n" +
		"import org.drools.assistant.test.model.Company;\n" +
		"IMPORT org.drools.assistant.test.model.Employee;\n\n" +
		"import function org.drools.assistant.model.Class1.anotherFunction \n" +
		"import		function org.drools.assistant.model.Class1.mathFunction \n" +
		"global     org.drools.assistant.test.model.Class2    results \n"+
		"GLOBAL org.drools.assistant.test.model.Class3 current\n"+ 
		"expander help-expander.dsl\n" +
		"query \"all clients\"\n" +
		"	result : Clients()\n" +
		"end\n" +
		"query \"new query\"\n" +
		"	objects : Clients()\n" +
		"end\n" +
		"function String hello(String name) {\n"+
		"    return \"Hello \"+name+\"!\";\n"+
		"}\n" +
		"function String helloWithAge(String name, Integer age) {\n"+
		"    return \"Hello2 \"+name+\"! \" + age;\n"+
		"}\n" +
		"rule   \"My Test Rule\"\n" +
		"when\n"+ 
		"	$employee : Employee($company : company, $company1 : oldcompany, $age : age > 80, salary > 400)\n" +
		"	$result : Company(company==$company, retireAge <= $age)\n" + 
		"then\n"+ 
		"	System.out.println(\"can retire\")\n" +
		"end\n"+
		"rule   \"My Second Rule\"\n" +
		"when\n"+ 
		"	Driver(licence = 1234, $name : name)\n" +
		"	$car : Car(company : $company, ownerLicense == licence, year == 2009)\n" + 
		"then\n"+ 
		"	System.out.println(\"licence 1234 has a new car\")\n" +
		"end\n";
	}

	public void testAssignSalaryFieldToVariable() throws Exception {
		List<AssistantOption> options = ruleAssistant.getRuleAssistant(rule, 780);
		assertEquals(options.size(), 1);
		ReplaceAssistantOption assistantOption = (ReplaceAssistantOption) options.get(0);
		Assert.assertEquals("\t$employee : Employee($company : company, $company1 : oldcompany, $age : age > 80, salary $ : > 400)", assistantOption.getContent());
	}

	public void testDontAssignFieldInsideRHS() throws Exception {
		List<AssistantOption> options = ruleAssistant.getRuleAssistant(rule, 840);
		assertEquals(options.size(), 0);
	}

	public void testAssignLicenseFromSecondRule() throws Exception {
		List<AssistantOption> options = ruleAssistant.getRuleAssistant(rule, 930);
		assertEquals(options.size(), 1);
		ReplaceAssistantOption assistantOption = (ReplaceAssistantOption) options.get(0);
		Assert.assertEquals("\tDriver($licence : licence = 1234, $name : name)", assistantOption.getContent());
	}

}
