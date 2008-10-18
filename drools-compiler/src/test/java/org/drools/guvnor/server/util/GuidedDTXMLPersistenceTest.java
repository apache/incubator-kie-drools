package org.drools.guvnor.server.util;

import junit.framework.TestCase;

import org.drools.guvnor.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.guvnor.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.guvnor.client.modeldriven.dt.AttributeCol;
import org.drools.guvnor.client.modeldriven.dt.ConditionCol;
import org.drools.guvnor.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.guvnor.client.modeldriven.dt.MetadataCol;

public class GuidedDTXMLPersistenceTest extends TestCase {

	public void testRoundTrip() {
		GuidedDecisionTable dt = new GuidedDecisionTable();
		dt.actionCols.add(new ActionInsertFactCol());
		dt.actionCols.add(new ActionSetFieldCol());
		dt.metadataCols.add(new MetadataCol());
		dt.attributeCols.add(new AttributeCol());
		dt.conditionCols.add(new ConditionCol());
		dt.data = new String[][] {
				new String[] {"hola"}
		};
		dt.tableName = "blah";
		dt.descriptionWidth = 42;

		String xml = GuidedDTXMLPersistence.getInstance().marshal(dt);
		assertNotNull(xml);
		assertEquals(-1, xml.indexOf("ActionSetField"));
		assertEquals(-1, xml.indexOf("ConditionCol"));
		assertEquals(-1, xml.indexOf("GuidedDecisionTable"));

		GuidedDecisionTable dt_ = GuidedDTXMLPersistence.getInstance().unmarshal(xml);
		assertNotNull(dt_);
		assertEquals(42, dt_.descriptionWidth);
		assertEquals("blah", dt_.tableName);
		assertEquals(1, dt_.metadataCols.size());
		assertEquals(1, dt_.attributeCols.size());
		assertEquals(2, dt_.actionCols.size());
		assertEquals(1, dt_.conditionCols.size());

	}

}
