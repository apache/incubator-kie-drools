package org.drools.brms.modeldriven;

import java.util.List;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.brxml.ActionRetractFact;
import org.drools.brms.client.modeldriven.brxml.ActionSetField;
import org.drools.brms.client.modeldriven.brxml.Constraint;
import org.drools.brms.client.modeldriven.brxml.FactPattern;
import org.drools.brms.client.modeldriven.brxml.IAction;
import org.drools.brms.client.modeldriven.brxml.IPattern;
import org.drools.brms.client.modeldriven.brxml.RuleAttribute;
import org.drools.brms.client.modeldriven.brxml.RuleModel;

import com.thoughtworks.xstream.XStream;

public class RuleModelTest extends TestCase {

    public void testBoundFactFinder() {
        RuleModel model = new RuleModel();
        
        assertNull(model.getBoundFact( "x" ));
        model.lhs = new IPattern[3];
        
        FactPattern x = new FactPattern("Car");
        model.lhs[0] = x;
        x.boundName = "x";
        
        assertNotNull(model.getBoundFact( "x" ));
        assertEquals(x, model.getBoundFact( "x" ));
        
        FactPattern y = new FactPattern("Car");
        model.lhs[1] = y;
        y.boundName = "y";
        
        FactPattern other = new FactPattern("House");
        model.lhs[2] = other;
        
        assertEquals(y, model.getBoundFact( "y" ));
        assertEquals(x, model.getBoundFact( "x" ));
        
        
        model.rhs = new IAction[1];
        ActionSetField set = new ActionSetField();
        set.variable = "x";
        model.rhs[0] = set;
        
        assertTrue(model.isBoundFactUsed( "x" ));
        assertFalse(model.isBoundFactUsed( "y" ));
        
        assertEquals(3, model.lhs.length);
        assertFalse(model.removeLhsItem( 0 ));
        assertEquals(3, model.lhs.length);
        
        ActionRetractFact fact = new ActionRetractFact("q");
        model.rhs[0] = fact;
        assertTrue(model.isBoundFactUsed( "q" ));
        assertFalse(model.isBoundFactUsed( "x" ));
        
        XStream xt = new XStream();
        xt.alias( "rule", RuleModel.class );
        xt.alias( "fact", FactPattern.class );
        xt.alias( "retract", ActionRetractFact.class );
        
        
        String brl = xt.toXML( model );
        
        System.out.println(brl);
    }
    
    public void testScopedVariables() {
        
        //setup the data...
        
        RuleModel model = new RuleModel();
        model.lhs = new IPattern[3];
        FactPattern x = new FactPattern("Car");
        model.lhs[0] = x;
        x.boundName = "x";
        
        FactPattern y = new FactPattern("Car");
        model.lhs[1] = y;
        y.boundName = "y";
        Constraint[] cons = new Constraint[2];
        y.constraints = cons;
        cons[0] = new Constraint("age");
        cons[1] = new Constraint("make");
        cons[0].fieldBinding = "qbc";
        
        FactPattern other = new FactPattern("House");
        model.lhs[2] = other;
        other.boundName = "q";
        Constraint[] cons2 = new Constraint[1];
        cons2[0] = new Constraint();
        other.constraints = cons2;
        
        
        //check the results for correct scope
        List vars = model.getBoundVariablesInScope(cons[0]);
        assertEquals(1, vars.size());
        assertEquals("x", vars.get( 0 ));
        
        vars = model.getBoundVariablesInScope(cons[1]);
        assertEquals(2, vars.size());
        assertEquals("x", vars.get( 0 ));
        assertEquals("qbc", vars.get( 1 ));   
        
        vars = model.getBoundVariablesInScope(cons[0]);
        assertEquals(1, vars.size());
        assertEquals("x", vars.get( 0 ));
        
        
        vars = model.getBoundVariablesInScope(cons2[0]);
        assertEquals(3, vars.size());
        assertEquals("x", vars.get( 0 ));        
        assertEquals("qbc", vars.get( 1 ));
        assertEquals("y", vars.get( 2 ));
    }
    
    public void testBindingList() {
        RuleModel model = new RuleModel();
        
        model.lhs = new IPattern[3];
        FactPattern x = new FactPattern("Car");
        model.lhs[0] = x;
        x.boundName = "x";
        
        FactPattern y = new FactPattern("Car");
        model.lhs[1] = y;
        y.boundName = "y";
        
        FactPattern other = new FactPattern("House");
        model.lhs[2] = other;
        
        
        List b = model.getBoundFacts();
        assertEquals(2, b.size());
        
        assertEquals("x", b.get( 0 ));
        assertEquals("y", b.get( 1 ));
        
    }
    
    public void testRemoveItemLhs() {
        RuleModel model = new RuleModel();
        
        model.lhs = new IPattern[3];
        FactPattern x = new FactPattern("Car");
        model.lhs[0] = x;
        x.boundName = "x";
        
        FactPattern y = new FactPattern("Car");
        model.lhs[1] = y;
        y.boundName = "y";
        
        FactPattern other = new FactPattern("House");
        model.lhs[2] = other;
        
        assertEquals(3, model.lhs.length);
        assertEquals(x, model.lhs[0]);
        
        model.removeLhsItem( 0 );
        
        assertEquals(2, model.lhs.length);
        assertEquals(y, model.lhs[0]);        
    }
    
    public void testRemoveItemRhs() {
        RuleModel model = new RuleModel();
        
        model.rhs = new IAction[3];
        ActionRetractFact r0 = new ActionRetractFact("x");
        ActionRetractFact r1 = new ActionRetractFact("y");
        ActionRetractFact r2 = new ActionRetractFact("z");
        
        model.rhs[0] = r0;
        model.rhs[1] = r1;
        model.rhs[2] = r2;
        
        model.removeRhsItem(1);
        
        assertEquals(2, model.rhs.length);
        assertEquals(r0, model.rhs[0]);
        assertEquals(r2, model.rhs[1]);        
    }
    
    public void testAddItemLhs() {
        RuleModel model = new RuleModel();
        FactPattern x = new FactPattern();
        model.addLhsItem( x );
        assertEquals(1, model.lhs.length);
        
        FactPattern y = new FactPattern();
        model.addLhsItem( y );
        
        assertEquals(2, model.lhs.length);
        assertEquals(x, model.lhs[0]);
        assertEquals(y, model.lhs[1]);
        
    }
    
    public void testAddItemRhs() {
        RuleModel model = new RuleModel();
        IAction a0 = new ActionSetField();
        IAction a1 = new ActionSetField();
        
        model.addRhsItem( a0 );
        
        assertEquals(1, model.rhs.length);
        model.addRhsItem( a1 );
        
        assertEquals(2, model.rhs.length);
        
        assertEquals(a0, model.rhs[0]);
        assertEquals(a1, model.rhs[1]);
    }
    
    public void testAttributes() {
        RuleModel m = new RuleModel();
        RuleAttribute at = new RuleAttribute("salience", "42");
        m.addAttribute(at);
        assertEquals(1, m.attributes.length);
        assertEquals(at, m.attributes[0]);
        
        RuleAttribute at2 = new RuleAttribute("agenda-group", "x");
        m.addAttribute( at2 );
        assertEquals(2, m.attributes.length);
        assertEquals(at2, m.attributes[1]);
        
        m.removeAttribute( 0 );
        assertEquals(1, m.attributes.length);
        assertEquals(at2, m.attributes[0]);
    }
    
    
}
