/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.data;

import java.util.Collection;

import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.lang.descr.PackageDescr;
import org.drools.verifier.VerifierComponentMockFactory;
import org.drools.verifier.components.EnumField;
import org.drools.verifier.components.EnumRestriction;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.InlineEvalDescr;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.OperatorDescrType;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.PatternOperatorDescr;
import org.drools.verifier.components.QualifiedIdentifierRestriction;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.ReturnValueFieldDescr;
import org.drools.verifier.components.ReturnValueRestriction;
import org.drools.verifier.components.RuleOperatorDescr;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.components.TextConsequence;
import org.drools.verifier.components.PatternVariable;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.components.VerifierAccessorDescr;
import org.drools.verifier.components.VerifierAccumulateDescr;
import org.drools.verifier.components.VerifierCollectDescr;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.components.RuleEval;
import org.drools.verifier.components.VerifierFieldAccessDescr;
import org.drools.verifier.components.VerifierFromDescr;
import org.drools.verifier.components.VerifierMethodAccessDescr;
import org.drools.verifier.components.PatternEval;
import org.drools.verifier.components.VerifierRule;

public class VerifierDataMapsTest {

    @Test
    public void testSaveVerifierComponentAndGet() {
        VerifierData data = VerifierReportFactory.newVerifierData();

        VerifierRule rule = VerifierComponentMockFactory.createRule1();
        rule.setName( "0" );
        String rulePath = rule.getPath();

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
                                                     rulePath );

        assertNotNull( rule2 );
        assertEquals( rule,
                      rule2 );
    }

    @Test
    public void testSaveVerifierComponentAndGetForAllComponentTypes() {

        RulePackage rulePackage = VerifierComponentMockFactory.createPackage1();
        saveVerifierComponentAndGet( rulePackage );

        VerifierRule rule = VerifierComponentMockFactory.createRule1();
        saveVerifierComponentAndGet( rule );

        Pattern pattern = VerifierComponentMockFactory.createPattern1();
        saveVerifierComponentAndGet( pattern );

        saveVerifierComponentAndGet( new InlineEvalDescr( pattern ) );
        saveVerifierComponentAndGet( new ObjectType(new PackageDescr("testPackage1")) );
        saveVerifierComponentAndGet( new RuleOperatorDescr( rule,
                                                            OperatorDescrType.AND ) );
        saveVerifierComponentAndGet( new PatternOperatorDescr( pattern,
                                                               OperatorDescrType.AND ) );
        saveVerifierComponentAndGet( new SubPattern( pattern,
                                                     0 ) );
        saveVerifierComponentAndGet( new ReturnValueFieldDescr( pattern ) );
        saveVerifierComponentAndGet( new SubRule( rule,
                                                  0 ) );
        saveVerifierComponentAndGet( new TextConsequence( rule ) );
        saveVerifierComponentAndGet( new PatternVariable( rule ) );
        saveVerifierComponentAndGet( new VerifierAccessorDescr( rule ) );
        saveVerifierComponentAndGet( new VerifierAccumulateDescr( pattern ) );
        saveVerifierComponentAndGet( new VerifierCollectDescr( pattern ) );
        saveVerifierComponentAndGet( new RuleEval( rule ) );
        saveVerifierComponentAndGet( new VerifierFieldAccessDescr( rule ) );
        saveVerifierComponentAndGet( new VerifierFromDescr( pattern ) );
        saveVerifierComponentAndGet( new VerifierMethodAccessDescr( rule ) );
        saveVerifierComponentAndGet( new PatternEval( pattern ) );
    }

    @Test
    public void testSaveVerifierComponentAndGetForAllFields() {
        saveVerifierComponentAndGet( new EnumField(new PackageDescr("testPackage1")) );
        saveVerifierComponentAndGet( new Field(new PackageDescr("testPackage1")) );
    }

    @Test
    public void testSaveVerifierComponentAndGetForAllRestrictions() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        saveVerifierComponentAndGet( LiteralRestriction.createRestriction( pattern,
                                                                           "" ) );
        saveVerifierComponentAndGet( new EnumRestriction( pattern ) );
        saveVerifierComponentAndGet( new QualifiedIdentifierRestriction( pattern ) );
        saveVerifierComponentAndGet( new ReturnValueRestriction( pattern ) );
        saveVerifierComponentAndGet( new ReturnValueRestriction( pattern ) );
        saveVerifierComponentAndGet( new VariableRestriction( pattern ) );
    }

    @Test
    public void testSavePatternAndGet() {
        VerifierData data = VerifierReportFactory.newVerifierData();

        VerifierRule rule = VerifierComponentMockFactory.createRule1();
        assertNotNull( rule.getName() );
        assertEquals( "testRule1",
                      rule.getName() );

        ObjectType objectType = new ObjectType(new PackageDescr("testPackage1"));
        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        assertNotNull( pattern.getRulePath() );
        assertEquals( rule.getPath(),
                      pattern.getRulePath() );

        assertNotNull( pattern.getName() );
        assertEquals( rule.getName(),
                      pattern.getRuleName() );

        pattern.setObjectTypePath( objectType.getPath() );
        assertNotNull( pattern.getObjectTypePath() );
        assertEquals( objectType.getPath(),
                      pattern.getObjectTypePath() );

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
                                                                objectType.getPath() );

        assertNotNull( objectType2 );
        assertEquals( objectType,
                      objectType2 );

        VerifierComponent rule2 = data.getVerifierObject( rule.getVerifierComponentType(),
                                                          rule.getPath() );

        assertNotNull( rule2 );
        assertEquals( rule,
                      rule2 );
    }

    private void saveVerifierComponentAndGet(Field field) {
        VerifierData data = VerifierReportFactory.newVerifierData();

        ObjectType objectType = new ObjectType(new PackageDescr("testPackage1"));

        field.setObjectTypePath( objectType.getPath() );

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
                                                           field.getPath() );

        assertNotNull( field2 );
        assertEquals( field,
                      field2 );

        Collection<VerifierComponent> objectTypes = data.getAll( objectType.getVerifierComponentType() );

        assertEquals( 1,
                      objectTypes.size() );
        assertEquals( objectType,
                      objectTypes.toArray()[0] );

        VerifierComponent objectType2 = data.getVerifierObject( objectType.getVerifierComponentType(),
                                                                objectType.getPath() );

        assertNotNull( objectType2 );
        assertEquals( objectType,
                      objectType2 );
    }

    private void saveVerifierComponentAndGet(Restriction component) {
        VerifierData data = VerifierReportFactory.newVerifierData();

        ObjectType objectType = new ObjectType(new PackageDescr("testPackage1"));

        Field field = new Field(new PackageDescr("testPackage1"));
        field.setObjectTypePath( objectType.getPath() );

        component.setFieldPath( field.getPath() );

        assertNotNull( component.getFieldPath() );

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
                                                               component.getPath() );

        assertNotNull( component2 );
        assertEquals( component,
                      component2 );

        Collection<VerifierComponent> fields = data.getAll( field.getVerifierComponentType() );

        assertEquals( 1,
                      fields.size() );
        assertEquals( field,
                      fields.toArray()[0] );

        VerifierComponent field2 = data.getVerifierObject( field.getVerifierComponentType(),
                                                           field.getPath() );

        assertNotNull( field2 );
        assertEquals( field,
                      field2 );

        Collection<VerifierComponent> objectTypes = data.getAll( objectType.getVerifierComponentType() );

        assertEquals( 1,
                      objectTypes.size() );
        assertEquals( objectType,
                      objectTypes.toArray()[0] );

        VerifierComponent objectType2 = data.getVerifierObject( objectType.getVerifierComponentType(),
                                                                objectType.getPath() );

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
                                                               component.getPath() );

        assertNotNull( component2 );
        assertEquals( component,
                      component2 );
    }

}
