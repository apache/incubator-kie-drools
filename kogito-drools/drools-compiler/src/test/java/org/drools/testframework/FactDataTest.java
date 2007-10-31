package org.drools.testframework;

import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;

import junit.framework.TestCase;

public class FactDataTest extends TestCase {
	public void testAdd() {
		FactData fd = new FactData("x", "y", new FieldData[0], false );
		assertEquals(0, fd.fieldData.length);
		fd.addFieldData(new FieldData("x", "y", false));
		assertEquals(1, fd.fieldData.length);
		fd.addFieldData(new FieldData("q", "x", false));
		assertEquals(2, fd.fieldData.length);
	}
}
