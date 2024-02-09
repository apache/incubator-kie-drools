/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.verifier.data;

import java.util.Collection;

import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.verifier.VerifierComponentMockFactory;
import org.drools.verifier.components.EnumField;
import org.drools.verifier.components.EnumRestriction;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.InlineEvalDescr;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.OperatorDescrType;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.PatternEval;
import org.drools.verifier.components.PatternOperatorDescr;
import org.drools.verifier.components.PatternVariable;
import org.drools.verifier.components.QualifiedIdentifierRestriction;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.ReturnValueFieldDescr;
import org.drools.verifier.components.ReturnValueRestriction;
import org.drools.verifier.components.RuleEval;
import org.drools.verifier.components.RuleOperatorDescr;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.components.TextConsequence;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.components.VerifierAccessorDescr;
import org.drools.verifier.components.VerifierAccumulateDescr;
import org.drools.verifier.components.VerifierCollectDescr;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.components.VerifierFieldAccessDescr;
import org.drools.verifier.components.VerifierFromDescr;
import org.drools.verifier.components.VerifierMethodAccessDescr;
import org.drools.verifier.components.VerifierRule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VerifierDataMapsTest {

    @Test
    void testSaveVerifierComponentAndGet() {
        VerifierData data = VerifierReportFactory.newVerifierData();

        VerifierRule rule = VerifierComponentMockFactory.createRule1();
        rule.setName("0");
        String rulePath = rule.getPath();

        data.add(rule);

        Collection<VerifierComponent> all = data.getAll();

        assertThat(all.size()).isEqualTo(1);
        assertThat(all.toArray()[0]).isEqualTo(rule);

        Collection<VerifierRule> rules = data.getAll(VerifierComponentType.RULE);

        assertThat(rules.size()).isEqualTo(1);
        assertThat(rules.toArray()[0]).isEqualTo(rule);

        VerifierRule rule2 = data.getVerifierObject(VerifierComponentType.RULE,
                rulePath);

        assertThat(rule2).isNotNull();
        assertThat(rule2).isEqualTo(rule);
    }

    @Test
    void testSaveVerifierComponentAndGetForAllComponentTypes() {

        RulePackage rulePackage = VerifierComponentMockFactory.createPackage1();
        saveVerifierComponentAndGet(rulePackage);

        VerifierRule rule = VerifierComponentMockFactory.createRule1();
        saveVerifierComponentAndGet(rule);

        Pattern pattern = VerifierComponentMockFactory.createPattern1();
        saveVerifierComponentAndGet(pattern);

        saveVerifierComponentAndGet(new InlineEvalDescr( pattern ));
        saveVerifierComponentAndGet(new ObjectType(new PackageDescr("testPackage1")));
        saveVerifierComponentAndGet(new RuleOperatorDescr( new AndDescr(), rule,
                OperatorDescrType.AND ));
        saveVerifierComponentAndGet(new PatternOperatorDescr( pattern,
                OperatorDescrType.AND ));
        saveVerifierComponentAndGet(new SubPattern( pattern,
                0 ));
        saveVerifierComponentAndGet(new ReturnValueFieldDescr( pattern ));
        saveVerifierComponentAndGet(new SubRule( rule,
                0 ));
        saveVerifierComponentAndGet(new TextConsequence( rule ));
        saveVerifierComponentAndGet(new PatternVariable( rule ));
        saveVerifierComponentAndGet(new VerifierAccessorDescr( rule ));
        saveVerifierComponentAndGet(new VerifierAccumulateDescr( pattern ));
        saveVerifierComponentAndGet(new VerifierCollectDescr( pattern ));
        saveVerifierComponentAndGet(new RuleEval( rule ));
        saveVerifierComponentAndGet(new VerifierFieldAccessDescr( rule ));
        saveVerifierComponentAndGet(new VerifierFromDescr( pattern ));
        saveVerifierComponentAndGet(new VerifierMethodAccessDescr( rule ));
        saveVerifierComponentAndGet(new PatternEval( pattern ));
    }

    @Test
    void testSaveVerifierComponentAndGetForAllFields() {
        saveVerifierComponentAndGet(new EnumField(new PackageDescr("testPackage1")));
        saveVerifierComponentAndGet(new Field(new PackageDescr("testPackage1")));
    }

    @Test
    void testSaveVerifierComponentAndGetForAllRestrictions() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        saveVerifierComponentAndGet(LiteralRestriction.createRestriction(pattern,
                ""));
        saveVerifierComponentAndGet(new EnumRestriction( pattern ));
        saveVerifierComponentAndGet(new QualifiedIdentifierRestriction( pattern ));
        saveVerifierComponentAndGet(new ReturnValueRestriction( pattern ));
        saveVerifierComponentAndGet(new ReturnValueRestriction( pattern ));
        saveVerifierComponentAndGet(new VariableRestriction( pattern ));
    }

    @Test
    void testSavePatternAndGet() {
        VerifierData data = VerifierReportFactory.newVerifierData();

        VerifierRule rule = VerifierComponentMockFactory.createRule1();
        assertThat(rule.getName()).isNotNull();
        assertThat(rule.getName()).isEqualTo("testRule1");

        ObjectType objectType = new ObjectType(new PackageDescr("testPackage1"));
        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        assertThat(pattern.getRulePath()).isNotNull();
        assertThat(pattern.getRulePath()).isEqualTo(rule.getPath());

        assertThat(pattern.getName()).isNotNull();
        assertThat(pattern.getRuleName()).isEqualTo(rule.getName());

        pattern.setObjectTypePath(objectType.getPath());
        assertThat(pattern.getObjectTypePath()).isNotNull();
        assertThat(pattern.getObjectTypePath()).isEqualTo(objectType.getPath());

        data.add(rule);
        data.add(objectType);
        data.add(pattern);

        Collection<VerifierComponent> all = data.getAll();

        assertThat(all.size()).isEqualTo(3);
        assertThat(all.contains(pattern)).isTrue();
        assertThat(all.contains(objectType)).isTrue();
        assertThat(all.contains(rule)).isTrue();

        Collection<VerifierComponent> components = data.getAll(pattern.getVerifierComponentType());

        assertThat(components.size()).isEqualTo(1);
        assertThat(components.toArray()[0]).isEqualTo(pattern);

        VerifierComponent objectType2 = data.getVerifierObject(objectType.getVerifierComponentType(),
                objectType.getPath());

        assertThat(objectType2).isNotNull();
        assertThat(objectType2).isEqualTo(objectType);

        VerifierComponent rule2 = data.getVerifierObject(rule.getVerifierComponentType(),
                rule.getPath());

        assertThat(rule2).isNotNull();
        assertThat(rule2).isEqualTo(rule);
    }

    private void saveVerifierComponentAndGet(Field field) {
        VerifierData data = VerifierReportFactory.newVerifierData();

        ObjectType objectType = new ObjectType(new PackageDescr("testPackage1"));

        field.setObjectTypePath( objectType.getPath() );

        data.add( objectType );
        data.add( field );

        Collection<VerifierComponent> all = data.getAll();

        assertThat(all.size()).isEqualTo(2);
        assertThat(all.contains(objectType)).isTrue();
        assertThat(all.contains(field)).isTrue();

        Collection<VerifierComponent> fields = data.getAll( field.getVerifierComponentType() );

        assertThat(fields.size()).isEqualTo(1);
        assertThat(fields.toArray()[0]).isEqualTo(field);

        VerifierComponent field2 = data.getVerifierObject( field.getVerifierComponentType(),
                                                           field.getPath() );

        assertThat(field2).isNotNull();
        assertThat(field2).isEqualTo(field);

        Collection<VerifierComponent> objectTypes = data.getAll( objectType.getVerifierComponentType() );

        assertThat(objectTypes.size()).isEqualTo(1);
        assertThat(objectTypes.toArray()[0]).isEqualTo(objectType);

        VerifierComponent objectType2 = data.getVerifierObject( objectType.getVerifierComponentType(),
                                                                objectType.getPath() );

        assertThat(objectType2).isNotNull();
        assertThat(objectType2).isEqualTo(objectType);
    }

    private void saveVerifierComponentAndGet(Restriction component) {
        VerifierData data = VerifierReportFactory.newVerifierData();

        ObjectType objectType = new ObjectType(new PackageDescr("testPackage1"));

        Field field = new Field(new PackageDescr("testPackage1"));
        field.setObjectTypePath( objectType.getPath() );

        component.setFieldPath( field.getPath() );

        assertThat(component.getFieldPath()).isNotNull();

        data.add( objectType );
        data.add( field );
        data.add( component );

        Collection<VerifierComponent> all = data.getAll();

        assertThat(all.size()).isEqualTo(3);
        assertThat(all.contains(objectType)).isTrue();
        assertThat(all.contains(field)).isTrue();
        assertThat(all.contains(component)).isTrue();

        Collection<VerifierComponent> components = data.getAll( component.getVerifierComponentType() );

        assertThat(components.size()).isEqualTo(1);
        assertThat(components.toArray()[0]).isEqualTo(component);

        VerifierComponent component2 = data.getVerifierObject( component.getVerifierComponentType(),
                                                               component.getPath() );

        assertThat(component2).isNotNull();
        assertThat(component2).isEqualTo(component);

        Collection<VerifierComponent> fields = data.getAll( field.getVerifierComponentType() );

        assertThat(fields.size()).isEqualTo(1);
        assertThat(fields.toArray()[0]).isEqualTo(field);

        VerifierComponent field2 = data.getVerifierObject( field.getVerifierComponentType(),
                                                           field.getPath() );

        assertThat(field2).isNotNull();
        assertThat(field2).isEqualTo(field);

        Collection<VerifierComponent> objectTypes = data.getAll( objectType.getVerifierComponentType() );

        assertThat(objectTypes.size()).isEqualTo(1);
        assertThat(objectTypes.toArray()[0]).isEqualTo(objectType);

        VerifierComponent objectType2 = data.getVerifierObject( objectType.getVerifierComponentType(),
                                                                objectType.getPath() );

        assertThat(objectType2).isNotNull();
        assertThat(objectType2).isEqualTo(objectType);
    }

    private void saveVerifierComponentAndGet(VerifierComponent component) {
        VerifierData data = VerifierReportFactory.newVerifierData();

        data.add( component );

        Collection<VerifierComponent> all = data.getAll();

        assertThat(all.size()).isEqualTo(1);
        assertThat(all.toArray()[0]).isEqualTo(component);

        Collection<VerifierComponent> components = data.getAll( component.getVerifierComponentType() );

        assertThat(components.size()).isEqualTo(1);
        assertThat(components.toArray()[0]).isEqualTo(component);

        VerifierComponent component2 = data.getVerifierObject( component.getVerifierComponentType(),
                                                               component.getPath() );

        assertThat(component2).isNotNull();
        assertThat(component2).isEqualTo(component);
    }

}
