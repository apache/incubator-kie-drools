/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.score.director.drools.testgen.fact;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.collection.TestdataEntityCollectionPropertyEntity;
import org.optaplanner.core.impl.testdata.domain.testgen.TestdataGetterSetterTypeMismatch;

public class TestGenValueFactTest {

    @Test
    public void initialization() {
        TestdataValue instance = new TestdataValue();
        TestGenValueFact fact = new TestGenValueFact(321, instance);

        assertThat(fact.getInstance()).isSameAs(instance);
        assertThat(fact.toString()).isEqualTo("testdataValue_321");

        StringBuilder sb = new StringBuilder(100);
        fact.printInitialization(sb);
        assertThat(sb.toString().trim()).isEqualTo("TestdataValue testdataValue_321 = new TestdataValue();");
    }

    @Test
    public void importsAndDependenciesSimple() {
        HashMap<Object, TestGenFact> instances = new HashMap<>();
        TestdataEntity entity = new TestdataEntity();
        TestGenValueFact f1 = new TestGenValueFact(0, entity);
        TestdataValue value = new TestdataValue();
        TestGenValueFact f2 = new TestGenValueFact(1, value);

        instances.put(entity, f1);

        f1.setUp(instances);
        assertThat(f1.getImports().size()).isEqualTo(1);
        assertThat(f1.getImports().contains(TestdataEntity.class)).isTrue();
        assertThat(f1.getDependencies().size()).isEqualTo(0);

        entity.setValue(value);
        instances.put(value, f2);

        f2.setUp(instances);
        assertThat(f2.getImports().size()).isEqualTo(1);
        assertThat(f2.getImports().contains(TestdataValue.class)).isTrue();
        assertThat(f1.getDependencies().isEmpty()).isTrue();

        f1.setUp(instances);
        assertThat(f1.getImports().size()).isEqualTo(1);
        assertThat(f1.getImports().contains(TestdataEntity.class)).isTrue();
        assertThat(f1.getDependencies().size()).isEqualTo(1);
        assertThat(f1.getDependencies().contains(f2)).isTrue();
    }

    @Test
    public void importsAndDependenciesComplex() {
        TestdataEntityCollectionPropertyEntity a = new TestdataEntityCollectionPropertyEntity("a", null);
        TestdataEntityCollectionPropertyEntity b = new TestdataEntityCollectionPropertyEntity("b", null);
        TestdataEntityCollectionPropertyEntity c = new TestdataEntityCollectionPropertyEntity("c", null);
        TestdataEntityCollectionPropertyEntity d = new TestdataEntityCollectionPropertyEntity("d", null);
        TestdataEntityCollectionPropertyEntity e = new TestdataEntityCollectionPropertyEntity("e", null);
        TestdataEntityCollectionPropertyEntity f = new TestdataEntityCollectionPropertyEntity("f", null);
        TestdataEntityCollectionPropertyEntity g = new TestdataEntityCollectionPropertyEntity("g", null);
        TestdataEntityCollectionPropertyEntity h = new TestdataEntityCollectionPropertyEntity("h", null);
        TestdataEntityCollectionPropertyEntity i = new TestdataEntityCollectionPropertyEntity("i", null);
        a.setEntityList(Arrays.asList(b, c));
        a.setEntitySet(new HashSet<>(Arrays.asList(d, e)));
        a.setStringToEntityMap(new HashMap<>());
        a.getStringToEntityMap().put("f", f);
        a.getStringToEntityMap().put("g", g);
        a.setEntityToStringMap(new HashMap<>());
        a.getEntityToStringMap().put(h, "h");
        a.getEntityToStringMap().put(i, "i");

        TestGenValueFact fa = new TestGenValueFact(0, a);
        TestGenValueFact fb = new TestGenValueFact(1, b);
        TestGenValueFact fc = new TestGenValueFact(2, c);
        TestGenValueFact fd = new TestGenValueFact(3, d);
        TestGenValueFact fe = new TestGenValueFact(4, e);
        TestGenValueFact ff = new TestGenValueFact(5, f);
        TestGenValueFact fg = new TestGenValueFact(6, g);
        TestGenValueFact fh = new TestGenValueFact(7, h);
        TestGenValueFact fi = new TestGenValueFact(8, i);

        HashMap<Object, TestGenFact> instances = new HashMap<>();
        instances.put(a, fa);
        instances.put(b, fb);
        instances.put(c, fc);
        instances.put(d, fd);
        instances.put(e, fe);
        instances.put(f, ff);
        instances.put(g, fg);
        instances.put(h, fh);
        instances.put(i, fi);

        fa.setUp(instances);

        List<TestGenFact> dependencies = fa.getDependencies();
        assertThat(dependencies.size()).isEqualTo(8);
        assertThat(dependencies.contains(fb)).isTrue();
        assertThat(dependencies.contains(fc)).isTrue();
        assertThat(dependencies.contains(fd)).isTrue();
        assertThat(dependencies.contains(fe)).isTrue();
        assertThat(dependencies.contains(ff)).isTrue();
        assertThat(dependencies.contains(fg)).isTrue();
        assertThat(dependencies.contains(fh)).isTrue();
        assertThat(dependencies.contains(fi)).isTrue();

        List<Class<?>> imports = fa.getImports();
        assertThat(imports.contains(TestdataEntityCollectionPropertyEntity.class)).isTrue();
        assertThat(imports.contains(ArrayList.class)).isTrue();
        assertThat(imports.contains(HashSet.class)).isTrue();
        assertThat(imports.contains(HashMap.class)).isTrue();
        assertThat(imports.contains(String.class)).isTrue();
    }

    /**
     * Covers situations where getter returns a supertype of the property type. This occurs in the Coach Shuttle
     * Gathering example.
     */
    @Test
    public void getterSetterTypeMismatch() {
        TestdataGetterSetterTypeMismatch instance = new TestdataGetterSetterTypeMismatch();
        instance.setDescription("desc");
        TestGenValueFact fact = new TestGenValueFact(0, instance);
        HashMap<Object, TestGenFact> instances = new HashMap<>();
        fact.setUp(instances);
        instance.setDescription("");
        assertThat(instance.getDescription()).isEqualTo((CharSequence) "");
        fact.reset();
        assertThat(instance.getDescription()).isEqualTo((CharSequence) "desc");
    }

    @Test
    public void inlineValueList() {
        // prepare instances and facts
        TestdataEntity a = new TestdataEntity();
        TestdataEntity b = new TestdataEntity();
        TestGenValueFact fa = new TestGenValueFact(0, a);
        TestGenValueFact fb = new TestGenValueFact(1, b);
        HashMap<Object, TestGenFact> instances = new HashMap<>();
        instances.put(a, fa);
        instances.put(b, fb);

        // create the "inverse list" (simulation of inverse relationship shadow variable)
        ArrayList<TestdataEntity> inverseList = new ArrayList<>();
        inverseList.add(a);
        inverseList.add(b);

        // create the inline value of the inverse list
        TestGenInlineValue val = new TestGenInlineValue(inverseList, instances);
        assertThat(val.getImports().contains(Arrays.class)).isTrue();
        assertThat(val.toString()).isEqualTo("Arrays.asList(" + fa.getVariableName() + ", " + fb.getVariableName() + ")");
    }

    @Test
    public void parseMethodTakingStringAsParameter() {
        parseMethodOnField("fieldWithParseMethodTakingString");
    }

    @Test
    public void parseMethodTakingCharSequenceAsParameter() {
        parseMethodOnField("fieldWithParseMethodTakingCharSequence");
    }

    private void parseMethodOnField(String fieldName) {
        try {
            Field testField = TestClassWithFields.class.getDeclaredField(fieldName);
            Method m = TestGenValueFact.getParseMethod(testField);
            assertThat(m).isNotNull();
        } catch (UnsupportedOperationException | NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
    }

    static class TestClassWithParseMethodTakingCharSequence {

        public static TestClassWithParseMethodTakingCharSequence parse(CharSequence parameter) {
            return null;
        }
    }

    static class TestClassWithParseMethodTakingString {

        public static TestClassWithParseMethodTakingString parse(String parameter) {
            return null;
        }
    }

    static class TestClassWithFields {

        private TestClassWithParseMethodTakingCharSequence fieldWithParseMethodTakingCharSequence = null;
        private TestClassWithParseMethodTakingString fieldWithParseMethodTakingString = null;
    }
}
