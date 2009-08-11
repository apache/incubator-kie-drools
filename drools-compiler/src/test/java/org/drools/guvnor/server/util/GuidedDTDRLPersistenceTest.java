package org.drools.guvnor.server.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.ActionInsertFact;
import org.drools.guvnor.client.modeldriven.brl.ActionRetractFact;
import org.drools.guvnor.client.modeldriven.brl.ActionSetField;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.RuleAttribute;
import org.drools.guvnor.client.modeldriven.brl.RuleMetadata;
import org.drools.guvnor.client.modeldriven.brl.RuleModel;
import org.drools.guvnor.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.dt.ActionCol;
import org.drools.guvnor.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.guvnor.client.modeldriven.dt.ActionRetractFactCol;
import org.drools.guvnor.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.guvnor.client.modeldriven.dt.AttributeCol;
import org.drools.guvnor.client.modeldriven.dt.ConditionCol;
import org.drools.guvnor.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.guvnor.client.modeldriven.dt.MetadataCol;

public class GuidedDTDRLPersistenceTest extends TestCase {


	public void test2Rules() throws Exception {
		GuidedDecisionTable dt = new GuidedDecisionTable();
		dt.tableName = "michael";

		AttributeCol attr = new AttributeCol();
		attr.attr = "salience";
        attr.defaultValue = "66";
		dt.attributeCols.add(attr);

		ConditionCol con = new ConditionCol();
		con.boundName = "f1";
		con.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
		con.factField = "age";
		con.factType = "Driver";
		con.header = "Driver f1 age";
		con.operator = "==";
		dt.conditionCols.add(con);

		ConditionCol con2 = new ConditionCol();
		con2.boundName = "f1";
		con2.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
		con2.factField = "name";
		con2.factType = "Driver";
		con2.header = "Driver f1 name";
		con2.operator = "==";
		dt.conditionCols.add(con2);

		ConditionCol con3 = new ConditionCol();
		con3.boundName = "f1";
		con3.constraintValueType = ISingleFieldConstraint.TYPE_RET_VALUE;
		con3.factField = "rating";
		con3.factType = "Driver";
		con3.header = "Driver rating";
		con3.operator = "==";
		dt.conditionCols.add(con3);


		ConditionCol con4 = new ConditionCol();
		con4.boundName = "f2";
		con4.constraintValueType = ISingleFieldConstraint.TYPE_PREDICATE;
		con4.factType = "Driver";
		con4.header = "Driver 2 pimp";
        con4.factField = "(not needed)";
		dt.conditionCols.add(con4);


		ActionInsertFactCol ins = new ActionInsertFactCol();
		ins.boundName = "ins";
		ins.factType = "Cheese";
		ins.factField = "price";
		ins.type = SuggestionCompletionEngine.TYPE_NUMERIC;
		dt.actionCols.add(ins);

		ActionRetractFactCol ret = new ActionRetractFactCol();
		ret.boundName = "f2";
		dt.actionCols.add(ret);

		ActionSetFieldCol set = new ActionSetFieldCol();
		set.boundName = "f1";
		set.factField = "goo1";
		set.type = SuggestionCompletionEngine.TYPE_STRING;
		dt.actionCols.add(set);

		ActionSetFieldCol set2 = new ActionSetFieldCol();
		set2.boundName = "f1";
		set2.factField = "goo2";
        set2.defaultValue = "whee";
		set2.type = SuggestionCompletionEngine.TYPE_STRING;
		dt.actionCols.add(set2);

		dt.data = new String[][] {
				new String[] {"1", "desc", "42", "33", "michael", "age * 0.2", "age > 7", "6.60", "true", "gooVal1", null},
				new String[] {"2", "desc", "", "39", "bob", "age * 0.3", "age > 7", "6.60", "", "gooVal1", ""}
		};



		GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
		String drl = p.marshal(dt);


		assertTrue(drl.indexOf("from row number") > -1);
		assertTrue(drl.indexOf("rating == ( age * 0.2 )") > 0);
		assertTrue(drl.indexOf("f2 : Driver( eval( age > 7 ))") > 0);
		assertTrue(drl.indexOf("rating == ( age * 0.3 )") > drl.indexOf("rating == ( age * 0.2 )"));
        assertTrue(drl.indexOf("f1.setGoo2( \"whee\" )") > 0);   //for default
        assertTrue(drl.indexOf("salience 66") > 0);   //for default
        

	}


    public void testInterpolate() {
        GuidedDecisionTable dt = new GuidedDecisionTable();
        dt.tableName = "michael";

        AttributeCol attr = new AttributeCol();
        attr.attr = "salience";
        attr.defaultValue = "66";
        dt.attributeCols.add(attr);

        ConditionCol con = new ConditionCol();
        con.boundName = "f1";
        con.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
        con.factField = "age";
        con.factType = "Driver";
        con.header = "Driver f1 age";
        con.operator = "==";
        dt.conditionCols.add(con);

        ConditionCol con2 = new ConditionCol();
        con2.boundName = "f1";
        con2.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
        con2.factField = "name";
        con2.factType = "Driver";
        con2.header = "Driver f1 name";
        con2.operator = "==";
        dt.conditionCols.add(con2);

        ConditionCol con3 = new ConditionCol();
        con3.boundName = "f1";
        con3.constraintValueType = ISingleFieldConstraint.TYPE_RET_VALUE;
        con3.factField = "rating";
        con3.factType = "Driver";
        con3.header = "Driver rating";
        con3.operator = "==";
        dt.conditionCols.add(con3);


        ConditionCol con4 = new ConditionCol();
        con4.boundName = "f2";
        con4.constraintValueType = ISingleFieldConstraint.TYPE_PREDICATE;
        con4.factType = "Driver";
        con4.header = "Driver 2 pimp";
        con4.factField = "this.hasSomething($param)";
        dt.conditionCols.add(con4);


        ActionInsertFactCol ins = new ActionInsertFactCol();
        ins.boundName = "ins";
        ins.factType = "Cheese";
        ins.factField = "price";
        ins.type = SuggestionCompletionEngine.TYPE_NUMERIC;
        dt.actionCols.add(ins);

        ActionRetractFactCol ret = new ActionRetractFactCol();
        ret.boundName = "f2";
        dt.actionCols.add(ret);

        ActionSetFieldCol set = new ActionSetFieldCol();
        set.boundName = "f1";
        set.factField = "goo1";
        set.type = SuggestionCompletionEngine.TYPE_STRING;
        dt.actionCols.add(set);

        ActionSetFieldCol set2 = new ActionSetFieldCol();
        set2.boundName = "f1";
        set2.factField = "goo2";
        set2.defaultValue = "whee";
        set2.type = SuggestionCompletionEngine.TYPE_STRING;
        dt.actionCols.add(set2);

        dt.data = new String[][] {
                new String[] {"1", "desc", "42", "33", "michael", "age * 0.2", "BAM", "6.60", "true", "gooVal1", null},
                new String[] {"2", "desc", "", "39", "bob", "age * 0.3", "BAM", "6.60", "", "gooVal1", ""}
        };



        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal(dt);


        assertTrue(drl.indexOf("from row number") > -1);
        assertTrue(drl.indexOf("rating == ( age * 0.2 )") > 0);
        //assertTrue(drl.indexOf("f2 : Driver( eval( age > 7 ))") > 0);
        assertTrue(drl.indexOf("f2 : Driver( eval( this.hasSomething(BAM) ))") > 0);
        assertTrue(drl.indexOf("rating == ( age * 0.3 )") > drl.indexOf("rating == ( age * 0.2 )"));
        assertTrue(drl.indexOf("f1.setGoo2( \"whee\" )") > 0);   //for default
        assertTrue(drl.indexOf("salience 66") > 0);   //for default
        
    }

	public void testCellVal() {
		GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
		assertFalse(p.validCell(null));
		assertFalse(p.validCell(""));
		assertFalse(p.validCell("  "));

	}

	public void testName() {
		GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
		assertEquals("Row 42 XXX", p.getName("XXX", "42"));
		assertEquals("Row 42 YYY", p.getName("YYY", "42"));
	}

	public void testAttribs() {
		GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
		String[] row = new String[] {"1", "desc", "a", ""};

		List<AttributeCol> attributeCols = new ArrayList<AttributeCol>();
		RuleModel rm = new RuleModel();
		RuleAttribute[] orig = rm.attributes;
		p.doAttribs(0,attributeCols, row, rm);

		assertSame(orig, rm.attributes);

		AttributeCol col1 = new AttributeCol();
		col1.attr = "salience";
		AttributeCol col2 = new AttributeCol();
		col2.attr = "agenda-group";
		attributeCols.add(col1);
		attributeCols.add(col2);

		p.doAttribs(0, attributeCols, row, rm);

		assertEquals(1, rm.attributes.length);
		assertEquals("salience", rm.attributes[0].attributeName);
		assertEquals("a", rm.attributes[0].value);

		row = new String[] {"1", "desc", "a", "b"};
		p.doAttribs(0, attributeCols, row, rm);
		assertEquals(2, rm.attributes.length);
		assertEquals("salience", rm.attributes[0].attributeName);
		assertEquals("a", rm.attributes[0].value);
		assertEquals("agenda-group", rm.attributes[1].attributeName);
		assertEquals("b", rm.attributes[1].value);

	}

	public void testMetaData() {
		GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
		String[] row = new String[] {"1", "desc", "bar", ""};

		List<MetadataCol> metadataCols = new ArrayList<MetadataCol>();
		RuleModel rm = new RuleModel();
		RuleMetadata[] orig = rm.metadataList;
//		RuleAttribute[] orig = rm.attributes;
		p.doMetadata(metadataCols, row, rm);
//		p.doAttribs(0,metadataCols, row, rm);

		assertSame(orig, rm.metadataList);

		MetadataCol col1 = new MetadataCol();
		col1.attr = "foo";
		MetadataCol col2 = new MetadataCol();
		col2.attr = "foo2";
		metadataCols.add(col1);
		metadataCols.add(col2);

		p.doMetadata(metadataCols, row, rm);
//		p.doAttribs(0, metadataCols, row, rm);

		assertEquals(1, rm.metadataList.length);
		assertEquals("foo", rm.metadataList[0].attributeName);
		assertEquals("bar", rm.metadataList[0].value);

		row = new String[] {"1", "desc", "bar1", "bar2"};
		p.doMetadata(metadataCols, row, rm);
		assertEquals(2, rm.metadataList.length);
		assertEquals("foo", rm.metadataList[0].attributeName);
		assertEquals("bar1", rm.metadataList[0].value);
		assertEquals("foo2", rm.metadataList[1].attributeName);
		assertEquals("bar2", rm.metadataList[1].value);

	}

	public void testLHS() {
		GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
		String[] row = new String[] {"1", "desc", "a", "mike", "33 + 1", "age > 6", "stilton"};

		List<ConditionCol> cols = new ArrayList<ConditionCol>();
		ConditionCol col = new ConditionCol();
		col.boundName = "p1";
		col.factType = "Person";
		col.factField = "name";
		col.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
		col.operator = "==";
		cols.add(col);

		ConditionCol col2 = new ConditionCol();
		col2.boundName = "p1";
		col2.factType = "Person";
		col2.factField = "age";
		col2.constraintValueType = ISingleFieldConstraint.TYPE_RET_VALUE;
		col2.operator = "<";
		cols.add(col2);

		ConditionCol col3 = new ConditionCol();
		col3.boundName = "p1";
		col3.factType = "Person";
		col3.constraintValueType = ISingleFieldConstraint.TYPE_PREDICATE;
		cols.add(col3);

		ConditionCol col4 = new ConditionCol();
		col4.boundName = "c";
		col4.factType = "Cheese";
		col4.factField = "type";
		col4.operator = "==";
		col4.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
		cols.add(col4);

		RuleModel rm = new RuleModel();

		p.doConditions(1, cols, row, rm);
		assertEquals(2, rm.lhs.length);

		assertEquals("Person", ((FactPattern)rm.lhs[0]).factType);
		assertEquals("p1", ((FactPattern)rm.lhs[0]).boundName);

		assertEquals("Cheese", ((FactPattern)rm.lhs[1]).factType);
		assertEquals("c", ((FactPattern)rm.lhs[1]).boundName);

		//examine the first pattern
		FactPattern person = (FactPattern) rm.lhs[0];
		assertEquals(3, person.constraintList.constraints.length);
		SingleFieldConstraint cons = (SingleFieldConstraint) person.constraintList.constraints[0];
		assertEquals(ISingleFieldConstraint.TYPE_LITERAL, cons.constraintValueType);
		assertEquals("name", cons.fieldName);
		assertEquals("==", cons.operator);
		assertEquals("mike", cons.value);

		cons = (SingleFieldConstraint) person.constraintList.constraints[1];
		assertEquals(ISingleFieldConstraint.TYPE_RET_VALUE, cons.constraintValueType);
		assertEquals("age", cons.fieldName);
		assertEquals("<", cons.operator);
		assertEquals("33 + 1", cons.value);

		cons = (SingleFieldConstraint) person.constraintList.constraints[2];
		assertEquals(ISingleFieldConstraint.TYPE_PREDICATE, cons.constraintValueType);
		assertEquals("age > 6", cons.value);


		//examine the second pattern
		FactPattern cheese = (FactPattern) rm.lhs[1];
		assertEquals(1, cheese.constraintList.constraints.length);
		cons = (SingleFieldConstraint) cheese.constraintList.constraints[0];
		assertEquals("type", cons.fieldName);
		assertEquals("==", cons.operator);
		assertEquals("stilton", cons.value);
		assertEquals(ISingleFieldConstraint.TYPE_LITERAL, cons.constraintValueType);
	}

	public void testRHS() {
		GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
		String[] row = new String[] {"1", "desc", "a", "a condition", "actionsetfield1", "actionsetfield2", "retract", "actioninsertfact1", "actioninsertfact2"};

		List<ActionCol> cols = new ArrayList<ActionCol>();
		ActionSetFieldCol asf1 = new ActionSetFieldCol();
		asf1.boundName = "a";
		asf1.factField = "field1";

		asf1.type = SuggestionCompletionEngine.TYPE_STRING;
		cols.add(asf1);

		ActionSetFieldCol asf2 = new ActionSetFieldCol();
		asf2.boundName = "a";
		asf2.factField = "field2";
		asf2.update = true;
		asf2.type = SuggestionCompletionEngine.TYPE_NUMERIC;
		cols.add(asf2);

		ActionRetractFactCol ret = new ActionRetractFactCol();
		ret.boundName = "ret";
		cols.add(ret);

		ActionInsertFactCol ins1 = new ActionInsertFactCol();
		ins1.boundName = "ins";
		ins1.factType = "Cheese";
		ins1.factField = "price";
		ins1.type = SuggestionCompletionEngine.TYPE_NUMERIC;
		cols.add(ins1);

		ActionInsertFactCol ins2 = new ActionInsertFactCol();
		ins2.boundName = "ins";
		ins2.factType = "Cheese";
		ins2.factField = "type";
		ins2.type = SuggestionCompletionEngine.TYPE_NUMERIC;
		cols.add(ins2);


		RuleModel rm = new RuleModel();
		p.doActions(2, cols, row, rm);
		assertEquals(3, rm.rhs.length);

		//examine the set field action that is produced
		ActionSetField a1 = (ActionSetField) rm.rhs[0];
		assertEquals("a", a1.variable);
		assertEquals(2, a1.fieldValues.length);

		assertEquals("field1", a1.fieldValues[0].field);
		assertEquals("actionsetfield1", a1.fieldValues[0].value);
		assertEquals(SuggestionCompletionEngine.TYPE_STRING, a1.fieldValues[0].type);

		assertEquals("field2", a1.fieldValues[1].field);
		assertEquals("actionsetfield2", a1.fieldValues[1].value);
		assertEquals(SuggestionCompletionEngine.TYPE_NUMERIC, a1.fieldValues[1].type);


		//examine the retract
		ActionRetractFact a2 = (ActionRetractFact) rm.rhs[1];
		assertEquals("ret", a2.variableName);

		//examine the insert
		ActionInsertFact a3 = (ActionInsertFact) rm.rhs[2];
		assertEquals("Cheese", a3.factType);
		assertEquals(2, a3.fieldValues.length);

		assertEquals("price", a3.fieldValues[0].field);
		assertEquals("actioninsertfact1", a3.fieldValues[0].value);
		assertEquals(SuggestionCompletionEngine.TYPE_NUMERIC, a3.fieldValues[0].type);

		assertEquals("type", a3.fieldValues[1].field);
		assertEquals("actioninsertfact2", a3.fieldValues[1].value);
		assertEquals(SuggestionCompletionEngine.TYPE_NUMERIC, a3.fieldValues[1].type);


	}

	public void testNoConstraints() {
		GuidedDecisionTable dt = new GuidedDecisionTable();
		ConditionCol c = new ConditionCol();
		c.boundName = "x";
		c.factType = "Context";
		c.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
		dt.conditionCols.add(c);
		ActionSetFieldCol asf = new ActionSetFieldCol();
		asf.boundName = "x";
		asf.factField = "age";
		asf.type = "String";

		dt.actionCols.add(asf);

		String[][] data = new String[][] {
			new String[] {"1", "desc", "y", "old"}
		};
		dt.data = data;

		String drl = GuidedDTDRLPersistence.getInstance().marshal(dt);

		//System.err.println(drl);

		assertTrue(drl.indexOf("Context( )") > -1);
		assertTrue(drl.indexOf("x.setAge") > drl.indexOf("Context( )"));
		assertFalse(drl.indexOf("update( x );") > -1);

		dt.data = new String[][] {
				new String[] {"1", "desc", "", "old"}
			};
		drl = GuidedDTDRLPersistence.getInstance().marshal(dt);
		assertEquals(-1, drl.indexOf("Context( )"));


	}

	public void testUpdateModify() {
		GuidedDecisionTable dt = new GuidedDecisionTable();
		ConditionCol c = new ConditionCol();
		c.boundName = "x";
		c.factType = "Context";
		c.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
		dt.conditionCols.add(c);
		ActionSetFieldCol asf = new ActionSetFieldCol();
		asf.boundName = "x";
		asf.factField = "age";
		asf.type = "String";
		asf.update = true;

		dt.actionCols.add(asf);

		String[][] data = new String[][] {
			new String[] {"1", "desc", "y", "old"}
		};
		dt.data = data;

		String drl = GuidedDTDRLPersistence.getInstance().marshal(dt);

		System.err.println(drl);

		assertTrue(drl.indexOf("Context( )") > -1);
		assertTrue(drl.indexOf("x.setAge") > drl.indexOf("Context( )"));


		dt.data = new String[][] {
				new String[] {"1", "desc", "", "old"}
			};
		drl = GuidedDTDRLPersistence.getInstance().marshal(dt);
		assertEquals(-1, drl.indexOf("Context( )"));

		assertTrue(drl.indexOf("update( x );") > -1);

	}

	public void testNoOperator() {
		GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
		String[] row = new String[] {"1", "desc", "a", "> 42", "33 + 1", "age > 6", "stilton"};

		List<ConditionCol> cols = new ArrayList<ConditionCol>();

		ConditionCol col2 = new ConditionCol();
		col2.boundName = "p1";
		col2.factType = "Person";
		col2.factField = "age";
		col2.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
		col2.operator = "";
		cols.add(col2);


		RuleModel rm = new RuleModel();

		p.doConditions(1, cols, row, rm);


		String drl = BRDRLPersistence.getInstance().marshal(rm);
		assertTrue(drl.indexOf("age > \"42\"") > 0);

	}



}
