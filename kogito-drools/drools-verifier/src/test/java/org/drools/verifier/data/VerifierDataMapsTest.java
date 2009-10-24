package org.drools.verifier.data;

import java.util.Collection;

import junit.framework.TestCase;

import org.drools.verifier.components.Constraint;
import org.drools.verifier.components.EnumField;
import org.drools.verifier.components.EnumRestriction;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.FieldObjectTypeLink;
import org.drools.verifier.components.InlineEvalDescr;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.OperatorDescr;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.QualifiedIdentifierRestriction;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.ReturnValueFieldDescr;
import org.drools.verifier.components.ReturnValueRestriction;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.components.TextConsequence;
import org.drools.verifier.components.Variable;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.components.VerifierAccessorDescr;
import org.drools.verifier.components.VerifierAccumulateDescr;
import org.drools.verifier.components.VerifierCollectDescr;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.components.VerifierEvalDescr;
import org.drools.verifier.components.VerifierFieldAccessDescr;
import org.drools.verifier.components.VerifierFromDescr;
import org.drools.verifier.components.VerifierMethodAccessDescr;
import org.drools.verifier.components.VerifierPredicateDescr;
import org.drools.verifier.components.VerifierRule;

public class VerifierDataMapsTest extends TestCase {

    public void testSaveVerifierComponentAndGet() {
        VerifierData data = VerifierReportFactory.newVerifierData();

        VerifierRule rule = new VerifierRule();
        rule.setGuid( "0" );

        data.add( rule );

        Collection<VerifierComponent> all = data.getAll();

        assertEquals( 1,
                      all.size() );
        assertEquals( rule,
                      all.toArray()[0] );

        Collection<VerifierRule> rules = data.getAll( VerifierComponentType.RULE );

        assertEquals( 1,
                      rules.size() );
        assertEquals( rule,
                      rules.toArray()[0] );

        VerifierRule rule2 = data.getVerifierObject( VerifierComponentType.RULE,
                                                     "0" );

        assertNotNull( rule2 );
        assertEquals( rule,
                      rule2 );
    }

    public void testSaveVerifierComponentAndGetForAllComponentTypes() {

        saveVerifierComponentAndGet( fillTestValues( new Constraint() ) );
        saveVerifierComponentAndGet( fillTestValues( new FieldObjectTypeLink() ) );
        saveVerifierComponentAndGet( fillTestValues( new InlineEvalDescr() ) );
        saveVerifierComponentAndGet( fillTestValues( new ObjectType() ) );
        saveVerifierComponentAndGet( fillTestValues( new OperatorDescr() ) );
        saveVerifierComponentAndGet( fillTestValues( new SubPattern() ) );
        saveVerifierComponentAndGet( fillTestValues( new ReturnValueFieldDescr() ) );
        saveVerifierComponentAndGet( fillTestValues( new RulePackage() ) );
        saveVerifierComponentAndGet( fillTestValues( new SubRule() ) );
        saveVerifierComponentAndGet( fillTestValues( new TextConsequence() ) );
        saveVerifierComponentAndGet( fillTestValues( new Variable() ) );
        saveVerifierComponentAndGet( fillTestValues( new VerifierAccessorDescr() ) );
        saveVerifierComponentAndGet( fillTestValues( new VerifierAccumulateDescr() ) );
        saveVerifierComponentAndGet( fillTestValues( new VerifierCollectDescr() ) );
        saveVerifierComponentAndGet( fillTestValues( new VerifierEvalDescr() ) );
        saveVerifierComponentAndGet( fillTestValues( new VerifierFieldAccessDescr() ) );
        saveVerifierComponentAndGet( fillTestValues( new VerifierFromDescr() ) );
        saveVerifierComponentAndGet( fillTestValues( new VerifierMethodAccessDescr() ) );
        saveVerifierComponentAndGet( fillTestValues( new VerifierPredicateDescr() ) );
        saveVerifierComponentAndGet( fillTestValues( new VerifierRule() ) );
    }

    public void testSaveVerifierComponentAndGetForAllFields() {
        saveVerifierComponentAndGet( (Field) fillTestValues( new EnumField() ) );
        saveVerifierComponentAndGet( (Field) fillTestValues( new Field() ) );
    }

    public void testSaveVerifierComponentAndGetForAllRestrictions() {
        saveVerifierComponentAndGet( (Restriction) fillTestValues( new LiteralRestriction() ) );
        saveVerifierComponentAndGet( (Restriction) fillTestValues( new EnumRestriction() ) );
        saveVerifierComponentAndGet( (Restriction) fillTestValues( new QualifiedIdentifierRestriction() ) );
        saveVerifierComponentAndGet( (Restriction) fillTestValues( new ReturnValueRestriction() ) );
        saveVerifierComponentAndGet( (Restriction) fillTestValues( new ReturnValueRestriction() ) );
        saveVerifierComponentAndGet( (Restriction) fillTestValues( new VariableRestriction() ) );
    }

    public void testSavePatternAndGet() {
        VerifierData data = VerifierReportFactory.newVerifierData();

        VerifierRule rule = (VerifierRule) fillTestValues( new VerifierRule() );
        rule.setRuleName( "test" );
        assertNotNull( rule.getRuleName() );
        assertEquals( "test",
                      rule.getRuleName() );

        ObjectType objectType = (ObjectType) fillTestValues( new ObjectType() );
        Pattern pattern = (Pattern) fillTestValues( new Pattern() );

        pattern.setRuleGuid( rule.getGuid() );
        assertNotNull( pattern.getRuleGuid() );
        assertEquals( rule.getGuid(),
                      pattern.getRuleGuid() );

        pattern.setRuleName( rule.getRuleName() );
        assertNotNull( pattern.getRuleName() );
        assertEquals( rule.getRuleName(),
                      pattern.getRuleName() );

        pattern.setObjectTypeGuid( objectType.getGuid() );
        assertNotNull( pattern.getObjectTypeGuid() );
        assertEquals( objectType.getGuid(),
                      pattern.getObjectTypeGuid() );

        data.add( rule );
        data.add( objectType );
        data.add( pattern );

        Collection<VerifierComponent> all = data.getAll();

        assertEquals( 3,
                      all.size() );
        assertTrue( all.contains( pattern ) );
        assertTrue( all.contains( objectType ) );
        assertTrue( all.contains( rule ) );

        Collection<VerifierComponent> components = data.getAll( pattern.getVerifierComponentType() );

        assertEquals( 1,
                      components.size() );
        assertEquals( pattern,
                      components.toArray()[0] );

        VerifierComponent objectType2 = data.getVerifierObject( objectType.getVerifierComponentType(),
                                                                objectType.getGuid() );

        assertNotNull( objectType2 );
        assertEquals( objectType,
                      objectType2 );

        VerifierComponent rule2 = data.getVerifierObject( rule.getVerifierComponentType(),
                                                          rule.getGuid() );

        assertNotNull( rule2 );
        assertEquals( rule,
                      rule2 );
    }

    private VerifierComponent fillTestValues(VerifierComponent component) {
        component.setGuid( "0" );

        assertNotNull( component.getGuid() );
        assertNotNull( component.getVerifierComponentType() );

        return component;
    }

    private void saveVerifierComponentAndGet(Field field) {
        VerifierData data = VerifierReportFactory.newVerifierData();

        ObjectType objectType = (ObjectType) fillTestValues( new ObjectType() );

        field.setObjectTypeGuid( objectType.getGuid() );

        data.add( objectType );
        data.add( field );

        Collection<VerifierComponent> all = data.getAll();

        assertEquals( 2,
                      all.size() );
        assertTrue( all.contains( objectType ) );
        assertTrue( all.contains( field ) );

        Collection<VerifierComponent> fields = data.getAll( field.getVerifierComponentType() );

        assertEquals( 1,
                      fields.size() );
        assertEquals( field,
                      fields.toArray()[0] );

        VerifierComponent field2 = data.getVerifierObject( field.getVerifierComponentType(),
                                                           field.getGuid() );

        assertNotNull( field2 );
        assertEquals( field,
                      field2 );

        Collection<VerifierComponent> objectTypes = data.getAll( objectType.getVerifierComponentType() );

        assertEquals( 1,
                      objectTypes.size() );
        assertEquals( objectType,
                      objectTypes.toArray()[0] );

        VerifierComponent objectType2 = data.getVerifierObject( objectType.getVerifierComponentType(),
                                                                objectType.getGuid() );

        assertNotNull( objectType2 );
        assertEquals( objectType,
                      objectType2 );
    }

    private void saveVerifierComponentAndGet(Restriction component) {
        VerifierData data = VerifierReportFactory.newVerifierData();

        ObjectType objectType = (ObjectType) fillTestValues( new ObjectType() );

        Field field = (Field) fillTestValues( new Field() );
        field.setObjectTypeGuid( objectType.getGuid() );

        component.setFieldGuid( field.getGuid() );

        assertNotNull( component.getFieldGuid() );

        data.add( objectType );
        data.add( field );
        data.add( component );

        Collection<VerifierComponent> all = data.getAll();

        assertEquals( 3,
                      all.size() );
        assertTrue( all.contains( objectType ) );
        assertTrue( all.contains( field ) );
        assertTrue( all.contains( component ) );

        Collection<VerifierComponent> components = data.getAll( component.getVerifierComponentType() );

        assertEquals( 1,
                      components.size() );
        assertEquals( component,
                      components.toArray()[0] );

        VerifierComponent component2 = data.getVerifierObject( component.getVerifierComponentType(),
                                                               component.getGuid() );

        assertNotNull( component2 );
        assertEquals( component,
                      component2 );

        Collection<VerifierComponent> fields = data.getAll( field.getVerifierComponentType() );

        assertEquals( 1,
                      fields.size() );
        assertEquals( field,
                      fields.toArray()[0] );

        VerifierComponent field2 = data.getVerifierObject( field.getVerifierComponentType(),
                                                           field.getGuid() );

        assertNotNull( field2 );
        assertEquals( field,
                      field2 );

        Collection<VerifierComponent> objectTypes = data.getAll( objectType.getVerifierComponentType() );

        assertEquals( 1,
                      objectTypes.size() );
        assertEquals( objectType,
                      objectTypes.toArray()[0] );

        VerifierComponent objectType2 = data.getVerifierObject( objectType.getVerifierComponentType(),
                                                                objectType.getGuid() );

        assertNotNull( objectType2 );
        assertEquals( objectType,
                      objectType2 );
    }

    private void saveVerifierComponentAndGet(VerifierComponent component) {
        VerifierData data = VerifierReportFactory.newVerifierData();

        data.add( component );

        Collection<VerifierComponent> all = data.getAll();

        assertEquals( 1,
                      all.size() );
        assertEquals( component,
                      all.toArray()[0] );

        Collection<VerifierComponent> components = data.getAll( component.getVerifierComponentType() );

        assertEquals( 1,
                      components.size() );
        assertEquals( component,
                      components.toArray()[0] );

        VerifierComponent component2 = data.getVerifierObject( component.getVerifierComponentType(),
                                                               component.getGuid() );

        assertNotNull( component2 );
        assertEquals( component,
                      component2 );
    }

}
