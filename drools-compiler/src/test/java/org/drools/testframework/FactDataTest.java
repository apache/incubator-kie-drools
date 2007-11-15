package org.drools.testframework;

import java.util.ArrayList;

import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;

import junit.framework.TestCase;

public class FactDataTest extends TestCase {
	public void testAdd() {
		FactData fd = new FactData("x", "y", new ArrayList(), false );
		assertEquals(0, fd.fieldData.size());
		fd.fieldData.add(new FieldData("x", "y"));
		assertEquals(1, fd.fieldData.size());
		fd.fieldData.add(new FieldData("q", "x"));
		assertEquals(2, fd.fieldData.size());
	}
}
