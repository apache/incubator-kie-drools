package org.drools.brms.server.converter;

import java.util.List;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.brxml.CompositeFactPattern;
import org.drools.brms.client.modeldriven.brxml.Constraint;
import org.drools.brms.client.modeldriven.brxml.FactPattern;
import org.drools.brms.client.modeldriven.brxml.RuleAttribute;
import org.drools.brms.client.modeldriven.brxml.RuleModel;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.LiteralConstraint;

public class BRXMLToDescrConverterTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testToDescr() {
        BRXMLToDescrConverter converter = new BRXMLToDescrConverter();
        
        RuleModel model = new RuleModel();
        
        // adding rule's attributes
        RuleAttribute salience = new RuleAttribute( "salience", "10" );
        RuleAttribute noloop = new RuleAttribute( "no-loop", null );
        model.addAttribute( salience ); 
        model.addAttribute( noloop ); 
        
        // adding simple pattern
        FactPattern pat1 = new FactPattern("Cheese");
        pat1.boundName = "cheese";
        Constraint constr = new Constraint( "type" );
        constr.operator = "==";
        constr.value = "stilton";
        constr.constraintValueType = Constraint.TYPE_LITERAL;
        pat1.addConstraint( constr );
        model.addLhsItem( pat1 );
        
        CompositeFactPattern comp1 = new CompositeFactPattern();
        comp1.type = CompositeFactPattern.OR;
        
        FactPattern pat2 = new FactPattern("Person");
        FactPattern pat3 = new FactPattern("People");
        comp1.addFactPattern( pat2 );
        comp1.addFactPattern( pat3 );
        
        model.addLhsItem( comp1 );
        
        // converting into descr
        RuleDescr rule = converter.toDescr( model, "" );
        // checking Attributes
        List attributes = rule.getAttributes(); 
        assertEquals( 2, attributes.size() );
        AttributeDescr salienceDescr = (AttributeDescr) attributes.get( 0 );
        assertEquals( salience.attributeName, salienceDescr.getName() );
        assertEquals( salience.value, salienceDescr.getValue() );
        AttributeDescr noloopDescr = (AttributeDescr) attributes.get( 1 );
        assertEquals( noloop.attributeName, noloopDescr.getName() );
        assertEquals( "true", noloopDescr.getValue() );
        
        // checking LHS patterns
        ColumnDescr col1 = (ColumnDescr) rule.getLhs().getDescrs().get( 0 );
        assertEquals( pat1.boundName, col1.getIdentifier() );
        assertEquals( pat1.factType, col1.getObjectType() );
        FieldConstraintDescr field = (FieldConstraintDescr) col1.getDescrs().get( 0 );
        assertEquals( pat1.constraints[0].fieldName, field.getFieldName());
        LiteralRestrictionDescr lit = (LiteralRestrictionDescr) field.getRestrictions().get( 0 );
        assertEquals( pat1.constraints[0].operator, lit.getEvaluator());
        assertEquals( pat1.constraints[0].value, lit.getText());
        
        OrDescr or1 = (OrDescr) rule.getLhs().getDescrs().get( 1 );
        ColumnDescr col2 = (ColumnDescr) or1.getDescrs().get( 0 );
        assertEquals( pat2.factType, col2.getObjectType() );
        
        ColumnDescr col3 = (ColumnDescr) or1.getDescrs().get( 1 );
        assertEquals( pat3.factType, col3.getObjectType() );
        
    }

}
