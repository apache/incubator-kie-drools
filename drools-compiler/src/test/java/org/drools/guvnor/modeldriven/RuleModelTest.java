package org.drools.guvnor.modeldriven;

import java.util.List;

import junit.framework.TestCase;

import org.drools.guvnor.client.modeldriven.brl.ActionRetractFact;
import org.drools.guvnor.client.modeldriven.brl.ActionSetField;
import org.drools.guvnor.client.modeldriven.brl.CompositeFactPattern;
import org.drools.guvnor.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.ConnectiveConstraint;
import org.drools.guvnor.client.modeldriven.brl.DSLSentence;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.IAction;
import org.drools.guvnor.client.modeldriven.brl.IPattern;
import org.drools.guvnor.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.RuleAttribute;
import org.drools.guvnor.client.modeldriven.brl.RuleMetadata;
import org.drools.guvnor.client.modeldriven.brl.RuleModel;
import org.drools.guvnor.client.modeldriven.brl.SingleFieldConstraint;

import com.thoughtworks.xstream.XStream;

public class RuleModelTest extends TestCase {

	public void testAddItemLhs() {
		final RuleModel model = new RuleModel();
		final FactPattern x = new FactPattern();
		model.addLhsItem(x);
		assertEquals(1, model.lhs.length);

		final FactPattern y = new FactPattern();
		model.addLhsItem(y);

		assertEquals(2, model.lhs.length);
		assertEquals(x, model.lhs[0]);
		assertEquals(y, model.lhs[1]);

	}

	public void testAddItemRhs() {
		final RuleModel model = new RuleModel();
		final IAction a0 = new ActionSetField();
		final IAction a1 = new ActionSetField();

		model.addRhsItem(a0);

		assertEquals(1, model.rhs.length);
		model.addRhsItem(a1);

		assertEquals(2, model.rhs.length);

		assertEquals(a0, model.rhs[0]);
		assertEquals(a1, model.rhs[1]);
	}

	public void testAllVariableBindings() {
		final RuleModel model = new RuleModel();
		model.lhs = new IPattern[2];
		final FactPattern x = new FactPattern("Car");
		model.lhs[0] = x;
		x.boundName = "boundFact";

		SingleFieldConstraint sfc = new SingleFieldConstraint("q");
		x.addConstraint(sfc);
		sfc.fieldBinding = "field1";

		SingleFieldConstraint sfc2 = new SingleFieldConstraint("q");
		x.addConstraint(sfc2);
		sfc2.fieldBinding = "field2";

		model.lhs[1] = new CompositeFactPattern();

		List vars = model.getAllVariables();
		assertEquals(3, vars.size());
		assertEquals("boundFact", vars.get(0));
		assertEquals("field1", vars.get(1));
		assertEquals("field2", vars.get(2));

		assertTrue(model.isVariableNameUsed("field2"));

	}

	public void testAttributes() {
		final RuleModel m = new RuleModel();
		final RuleAttribute at = new RuleAttribute("salience", "42");
		m.addAttribute(at);
		assertEquals(1, m.attributes.length);
		assertEquals(at, m.attributes[0]);

		final RuleAttribute at2 = new RuleAttribute("agenda-group", "x");
		m.addAttribute(at2);
		assertEquals(2, m.attributes.length);
		assertEquals(at2, m.attributes[1]);

		m.removeAttribute(0);
		assertEquals(1, m.attributes.length);
		assertEquals(at2, m.attributes[0]);
	}

	public void testBindingList() {
		final RuleModel model = new RuleModel();

		model.lhs = new IPattern[3];
		final FactPattern x = new FactPattern("Car");
		model.lhs[0] = x;
		x.boundName = "x";

		final FactPattern y = new FactPattern("Car");
		model.lhs[1] = y;
		y.boundName = "y";

		final SingleFieldConstraint[] cons = new SingleFieldConstraint[2];
		y.constraintList = new CompositeFieldConstraint();
		y.constraintList.constraints = cons;
		cons[0] = new SingleFieldConstraint("age");
		cons[0].fieldBinding = "qbc";
		cons[0].fieldType = "String";
		cons[0].connectives = new ConnectiveConstraint[1];
		cons[0].connectives[0] = new ConnectiveConstraint("&", "x");
		cons[0].connectives[0].constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
		cons[1] = new SingleFieldConstraint("make");
		cons[1].fieldType = "Long";
		cons[1].connectives = new ConnectiveConstraint[1];
		cons[1].connectives[0] = new ConnectiveConstraint("=", "2");
		cons[1].connectives[0].constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;


		final FactPattern other = new FactPattern("House");
		model.lhs[2] = other;

		final List b = model.getBoundFacts();
		assertEquals(3, b.size());

		assertEquals("x", b.get(0));
		assertEquals("y", b.get(1));
		assertEquals("qbc", b.get(2));

	}

	public void testBoundFactFinder() {
		final RuleModel model = new RuleModel();

		assertNull(model.getBoundFact("x"));
		model.lhs = new IPattern[3];

		final FactPattern x = new FactPattern("Car");
		model.lhs[0] = x;
		x.boundName = "x";

		assertNotNull(model.getBoundFact("x"));
		assertEquals(x, model.getBoundFact("x"));

		final FactPattern y = new FactPattern("Car");
		model.lhs[1] = y;
		y.boundName = "y";

		final FactPattern other = new FactPattern("House");
		model.lhs[2] = other;

		assertEquals(y, model.getBoundFact("y"));
		assertEquals(x, model.getBoundFact("x"));

		model.rhs = new IAction[1];
		final ActionSetField set = new ActionSetField();
		set.variable = "x";
		model.rhs[0] = set;

		assertTrue(model.isBoundFactUsed("x"));
		assertFalse(model.isBoundFactUsed("y"));

		assertEquals(3, model.lhs.length);
		assertFalse(model.removeLhsItem(0));
		assertEquals(3, model.lhs.length);

		final ActionRetractFact fact = new ActionRetractFact("q");
		model.rhs[0] = fact;
		assertTrue(model.isBoundFactUsed("q"));
		assertFalse(model.isBoundFactUsed("x"));

		final XStream xt = new XStream();
		xt.alias("rule", RuleModel.class);
		xt.alias("fact", FactPattern.class);
		xt.alias("retract", ActionRetractFact.class);

		final String brl = xt.toXML(model);

		System.out.println(brl);
	}

	public void testGetVariableNameForRHS() {
		RuleModel m = new RuleModel();
		m.name = "blah";

		FactPattern pat = new FactPattern();
		pat.boundName = "pat";
		pat.factType = "Person";

		m.addLhsItem(pat);

		List l = m.getAllVariables();
		assertEquals(1, l.size());
		assertEquals("pat", l.get(0));

	}

	public void testIsDSLEnhanced() throws Exception {
		RuleModel m = new RuleModel();

		assertFalse(m.hasDSLSentences());

		m.addLhsItem(new FactPattern());
		assertFalse(m.hasDSLSentences());

		m.addRhsItem(new ActionSetField("q"));

		assertFalse(m.hasDSLSentences());

		m.addLhsItem(new DSLSentence());
		assertTrue(m.hasDSLSentences());

		m.addRhsItem(new DSLSentence());
		assertTrue(m.hasDSLSentences());

		m = new RuleModel();

		m.addLhsItem(new DSLSentence());
		assertTrue(m.hasDSLSentences());

		m = new RuleModel();
		m.addRhsItem(new DSLSentence());
		assertTrue(m.hasDSLSentences());

	}

	public void testMetaData() {
		final RuleModel m = new RuleModel();

		final RuleMetadata rm = new RuleMetadata("foo", "bar");

		// test add
		m.addMetadata(rm);
		assertEquals(1, m.metadataList.length);
		assertEquals(rm, m.metadataList[0]);

		// should be able to find it
		RuleMetadata gm = m.getMetaData("foo");
		assertNotNull(gm);

		// test add and remove
		final RuleMetadata rm2 = new RuleMetadata("foo2", "bar2");
		m.addMetadata(rm2);
		assertEquals(2, m.metadataList.length);
		assertEquals(rm2, m.metadataList[1]);
		assertEquals("@foo(bar)", rm.toString());

		m.removeMetadata(0);
		assertEquals(1, m.metadataList.length);
		assertEquals(rm2, m.metadataList[0]);
		assertEquals("@foo2(bar2)", (m.metadataList[0]).toString());

		// should be able to find it now that it was removed
		gm = m.getMetaData("foo");
		assertNull(gm);

		// test add via update method
		m.updateMetadata(rm);
		gm = m.getMetaData("foo");
		assertNotNull(gm);

		// test update of existing element
		rm.value = "bar2";
		m.updateMetadata(rm);
		gm = m.getMetaData("foo");
		assertNotNull(gm);
		assertEquals("bar2", gm.value);

	}

	public void testRemoveItemLhs() {
		final RuleModel model = new RuleModel();

		model.lhs = new IPattern[3];
		final FactPattern x = new FactPattern("Car");
		model.lhs[0] = x;
		x.boundName = "x";

		final FactPattern y = new FactPattern("Car");
		model.lhs[1] = y;
		y.boundName = "y";

		final FactPattern other = new FactPattern("House");
		model.lhs[2] = other;

		assertEquals(3, model.lhs.length);
		assertEquals(x, model.lhs[0]);

		model.removeLhsItem(0);

		assertEquals(2, model.lhs.length);
		assertEquals(y, model.lhs[0]);
	}

	public void testRemoveItemRhs() {
		final RuleModel model = new RuleModel();

		model.rhs = new IAction[3];
		final ActionRetractFact r0 = new ActionRetractFact("x");
		final ActionRetractFact r1 = new ActionRetractFact("y");
		final ActionRetractFact r2 = new ActionRetractFact("z");

		model.rhs[0] = r0;
		model.rhs[1] = r1;
		model.rhs[2] = r2;

		model.removeRhsItem(1);

		assertEquals(2, model.rhs.length);
		assertEquals(r0, model.rhs[0]);
		assertEquals(r2, model.rhs[1]);
	}

	public void testScopedVariables() {

		// setup the data...

		final RuleModel model = new RuleModel();
		model.lhs = new IPattern[3];
		final FactPattern x = new FactPattern("Car");
		model.lhs[0] = x;
		x.boundName = "x";

		final FactPattern y = new FactPattern("Car");
		model.lhs[1] = y;
		y.boundName = "y";
		final SingleFieldConstraint[] cons = new SingleFieldConstraint[2];
		y.constraintList = new CompositeFieldConstraint();
		y.constraintList.constraints = cons;
		cons[0] = new SingleFieldConstraint("age");
		cons[1] = new SingleFieldConstraint("make");
		cons[0].fieldBinding = "qbc";
		cons[0].connectives = new ConnectiveConstraint[1];
		cons[0].connectives[0] = new ConnectiveConstraint("&", "x");
		cons[0].connectives[0].constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;

		final FactPattern other = new FactPattern("House");
		model.lhs[2] = other;
		other.boundName = "q";
		final SingleFieldConstraint[] cons2 = new SingleFieldConstraint[1];
		cons2[0] = new SingleFieldConstraint();
		other.constraintList = new CompositeFieldConstraint();
		other.constraintList.constraints = cons2;

		// check the results for correct scope
		List vars = model.getBoundVariablesInScope(cons[0]);
		assertEquals(1, vars.size());
		assertEquals("x", vars.get(0));

		vars = model.getBoundVariablesInScope(cons[0].connectives[0]);
		assertEquals(1, vars.size());
		assertEquals("x", vars.get(0));

		vars = model.getBoundVariablesInScope(cons[1]);
		assertEquals(2, vars.size());
		assertEquals("x", vars.get(0));
		assertEquals("qbc", vars.get(1));

		vars = model.getBoundVariablesInScope(cons[0]);
		assertEquals(1, vars.size());
		assertEquals("x", vars.get(0));

		vars = model.getBoundVariablesInScope(cons2[0]);
		assertEquals(3, vars.size());
		assertEquals("x", vars.get(0));
		assertEquals("qbc", vars.get(1));
		assertEquals("y", vars.get(2));
	}

	public void testScopedVariablesWithCompositeFact() {
		RuleModel m = new RuleModel();
		FactPattern p = new FactPattern();
		CompositeFieldConstraint cf = new CompositeFieldConstraint();
		cf.addConstraint(new SingleFieldConstraint("x"));
		p.addConstraint(cf);
		SingleFieldConstraint sf = new SingleFieldConstraint("q");
		sf.fieldBinding = "abc";

		p.addConstraint(sf);
		SingleFieldConstraint sf2 = new SingleFieldConstraint("q");
		sf2.fieldBinding = "qed";
		cf.addConstraint(sf2);
		m.addLhsItem(p);

		List vars = m.getAllVariables();
		assertEquals(1, vars.size());
		assertEquals("abc", vars.get(0));
	}

	public void testGetFieldConstraint() {
		final RuleModel model = new RuleModel();
		model.lhs = new IPattern[3];
		final FactPattern x = new FactPattern("Boat");
		model.lhs[0] = x;
		x.boundName = "x";

		final FactPattern y = new FactPattern("Car");
		model.lhs[1] = y;
		y.boundName = "y";
		final SingleFieldConstraint[] cons = new SingleFieldConstraint[2];
		y.constraintList = new CompositeFieldConstraint();
		y.constraintList.constraints = cons;
		cons[0] = new SingleFieldConstraint("age");
		cons[0].fieldBinding = "qbc";
		cons[0].fieldType = "String";
		cons[0].connectives = new ConnectiveConstraint[1];
		cons[0].connectives[0] = new ConnectiveConstraint("&", "x");
		cons[0].connectives[0].constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
		cons[1] = new SingleFieldConstraint("make");
		cons[1].fieldType = "Long";
		cons[1].connectives = new ConnectiveConstraint[1];
		cons[1].connectives[0] = new ConnectiveConstraint("=", "2");
		cons[1].connectives[0].constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;

		final FactPattern other = new FactPattern("House");
		model.lhs[2] = other;
		other.boundName = "q";
		final SingleFieldConstraint[] cons2 = new SingleFieldConstraint[1];
		cons2[0] = new SingleFieldConstraint();
		other.constraintList = new CompositeFieldConstraint();
		other.constraintList.constraints = cons2;
		String varTypeString = model.getFieldConstraint("qbc");
		assertEquals("String", varTypeString);
		String varTypeLong = model.getFieldConstraint("make");
		assertEquals(null, varTypeLong);
		FactPattern varTypeBoat = model.getBoundFact("x");
		assertEquals("Boat", varTypeBoat.factType);
		FactPattern varTypeCar = model.getBoundFact("y");
		assertEquals("Car", varTypeCar.factType);
	}
}
