package org.jbpm.integrationtests;

public class TestVariable {
	
	private TestVariableData data = new TestVariableData();

	public TestVariable(String name) {
		this.data.name = name;
	}
	
	public String getName() {
		return data.name;
	}

}
