package org.drools.assistant;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.assistant.refactor.drl.VariableBinding;

public class VariableBindingTest extends TestCase {

	private String line;
	private String response;

	@Override
	protected void setUp() throws Exception {
		line = "\tEmployee($company : company, $age : age > 80, salary > 400)";
	}

	public void testFieldWithVariableAssignedTest1() {
		response = VariableBinding.execute(line, 24);
		Assert.assertEquals(true, response.equals(line));
	}

	public void testClassNameWithoutVariableAssigned() {
		response = VariableBinding.execute(line, 4);
		Assert.assertEquals(false, response.equals(line));
	}

	public void testFieldWithVariableAssignedTest2() {
		response = VariableBinding.execute(line, 39);
		Assert.assertEquals(true, response.equals(line));
	}

	public void testAssignVariableInsideTheComparator() {
		response = VariableBinding.execute(line, 50);
		Assert.assertEquals(false, response.equals(line));
	}

	public void testInsideFieldComparator() {
		response = VariableBinding.execute(line, 58);
		Assert.assertEquals(true, response.equals(line));
	}

	public void testComplexLineTestMustAssign() {
		line = "$ma20 : Double() from accumulate( $r2:ClosePrice(close, this != $r1, this after [0ms,20ms] $r1) , average ( $value ))";
		response = VariableBinding.execute(line, 53);
		Assert.assertEquals(false, response.equals(line));
	}

	public void testComplexLineTestDontMustAssign() {
		line = "$ma20 : Double() from accumulate( $r2:ClosePrice($close : close, this != $r1, this after [0ms,20ms] $r1) , average ( $value ))";
		response = VariableBinding.execute(line, 61);
		Assert.assertEquals(true, response.equals(line));
	}

	public void testComplexLineClosePriceMustAssign() {
		line = "$ma20 : Double() from accumulate( ClosePrice($close : close, this != $r1, this after [0ms,20ms] $r1) , average ( $value ))";
		response = VariableBinding.execute(line, 43);
		Assert.assertEquals(false, response.equals(line));
	}

	public void testComplexLineClosePriceDontMustAssign() {
		line = "$ma20 : Double() from accumulate( $cp : ClosePrice($close : close, this != $r1, this after [0ms,20ms] $r1) , average ( $value ))";
		response = VariableBinding.execute(line, 36);
		Assert.assertEquals(true, response.equals(line));
	}

//	public void testThisDontWorks() {
//		line = "$ma20 : Double() from accumulate( $r2:ClosePrice($close : close, this != $r1, this after [0ms,20ms] $r1) , average ( $value ))";
//		response = VariableBinding.execute(line, 121);
//		System.out.println(response);
//		Assert.assertEquals(true, response.equals(line));
//	}

	public void testSampleDRL() {
		line = "\t\tMessage( status == Message.HELLO, myMessage : message )\n";
		response = VariableBinding.execute(line, 3);
		Assert.assertEquals(false, response.equals(line));
	}

	public void testWithoutSpacesOrTab() {
		line = "Message( status == Message.HELLO, myMessage : message )\n";
		response = VariableBinding.execute(line, 0);
		Assert.assertEquals(false, response.equals(line));
	}

	public void testWithoutSpacesOrTabButWithVariableAssigned() {
		line = "m : Message( status == Message.HELLO, myMessage : message )\n";
		response = VariableBinding.execute(line, 1);
		Assert.assertEquals(true, response.equals(line));
	}

	public void testWithoutSpaceOnLeftOfField() {
		line = "m : Message( status == Message.HELLO,message )\n";
		response = VariableBinding.execute(line, 37);
		Assert.assertEquals(false, response.equals(line));
	}

}
