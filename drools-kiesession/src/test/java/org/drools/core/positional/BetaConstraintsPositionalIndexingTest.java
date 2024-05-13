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

package org.drools.core.positional;

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ObjectType;
import org.drools.base.base.ValueType;
import org.drools.base.definitions.impl.KnowledgePackageImpl;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.accessor.TupleValueExtractor;
import org.drools.base.rule.constraint.Constraint.ConstraintType;
import org.drools.base.util.IndexedValueReader;
import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.core.positional.Functions.Function1;
import org.drools.core.positional.Functions.Function2;
import org.drools.core.positional.PositionalConstraint.PositionalTupleValueExtractor1;
import org.drools.core.positional.PositionalConstraint.PositionalTupleValueExtractor2;
import org.drools.core.positional.Predicates.Predicate1;
import org.drools.core.positional.VoidFunctions.VoidFunction1;
import org.drools.core.positional.VoidFunctions.VoidFunction2;
import org.drools.core.positional.VoidFunctions.VoidFunction3;
import org.drools.core.test.model.Person;
import org.drools.core.util.index.IndexSpec;
import org.drools.kiesession.rulebase.SessionsAwareKnowledgeBase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.conf.CompositeBaseConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

public class BetaConstraintsPositionalIndexingTest {
    @Test
    public void testAlphaConsraint() {
        // Note this test only sets indexes for the join, no tests
        RuleImpl r1 = new RuleImpl("r1");

        final ObjectType stringObjectType = new ClassObjectType(String.class );

        final Pattern spattern = new Pattern(0,
                                             stringObjectType,
                                             "s" );

        PositionalConstraint sconstraint = new PositionalConstraint(new Declaration[] {}, spattern);
        sconstraint.setPredicate((Predicate1<String>) (a) -> a.equals("London"));
        sconstraint.setType(ConstraintType.ALPHA);
        sconstraint.setConstraintTypeOperator(ConstraintTypeOperator.EQUAL);
        spattern.addConstraint(sconstraint);

        r1.addPattern(spattern);

        List<Object[]>           results = new ArrayList<>();
        PositionalConsequence<?> c       = new PositionalConsequence<>("default", new Declaration[] {spattern.getDeclaration()});
        c.setFunction((VoidFunction1<String>) (s) -> {results.add(new Object[] {s}); System.out.println(s);});
        r1.setConsequence(c);

        KnowledgeBaseImpl base = new KnowledgeBaseImpl("default", (CompositeBaseConfiguration)  RuleBaseFactory.newKnowledgeBaseConfiguration());
        KnowledgePackageImpl pkg = new KnowledgePackageImpl();
        pkg.addRule(r1);

        base.addPackage(pkg);

        SessionsAwareKnowledgeBase kbase = new SessionsAwareKnowledgeBase(base);
        KieSession session = kbase.newKieSession();
        session.insert("London");
        session.insert("Paris");
        session.fireAllRules();

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)[0]).isEqualTo("London");
    }

    @Test
    public void testSingleInputEqualityIndexExpressions() {
        VoidFunction2<Pattern, PositionalConstraint> f1 = (ppattern, pconstraint) -> {
            TupleValueExtractor leftExtractor = new PositionalTupleValueExtractor1((Function1<String, String>) (s) -> s, ValueType.STRING_TYPE );
            TupleValueExtractor rightExtractor = new PositionalTupleValueExtractor1((Function1<Person, String>) (p) -> p.getCity(), ValueType.STRING_TYPE );
            pconstraint.setIndex(new IndexSpec(new IndexedValueReader[]{new IndexedValueReader(leftExtractor, rightExtractor)}));

            pconstraint.setType(ConstraintType.BETA);
            pconstraint.setConstraintTypeOperator(ConstraintTypeOperator.EQUAL);
            ppattern.addConstraint(pconstraint);
        };

        List<Object[]>           results = new ArrayList<>();
        VoidFunction1<PositionalConsequence<?>> f2 = (c) -> {
            c.setFunction((VoidFunction2<String, Person>) (s, p) -> {results.add(new Object[] {s, p}); System.out.println(p.getName() + " lives in " + s);});
        };

        testBody(null, f1, f2, false);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)[0]).isEqualTo("London");
        assertThat(((Person)results.get(0)[1]).getName()).isEqualTo("yoda");
    }

    @Test
    public void testDoubleInputEqualityIndexExpressions() {
        VoidFunction2<Pattern, PositionalConstraint> f1 = (ppattern, pconstraint) -> {
            TupleValueExtractor  leftExtractor = new PositionalTupleValueExtractor2((Function2<String, Integer, Integer>) (s, i) -> s.length() + i, ValueType.INTEGER_TYPE );
            TupleValueExtractor rightExtractor = new PositionalTupleValueExtractor1((Function1<Person, Integer>) (p) -> p.getCity().length(), ValueType.INTEGER_TYPE );
            pconstraint.setIndex(new IndexSpec(new IndexedValueReader[]{new IndexedValueReader(leftExtractor, rightExtractor)}));

            pconstraint.setType(ConstraintType.BETA);
            pconstraint.setConstraintTypeOperator(ConstraintTypeOperator.EQUAL);
            ppattern.addConstraint(pconstraint);
        };

        List<Object[]>           results = new ArrayList<>();
        VoidFunction1<PositionalConsequence<?>> f2 = (c) -> {
            c.setFunction((VoidFunction3<String, Integer, Person>) (s, i, p) -> {results.add(new Object[] {s, i, p}); System.out.println(p.getName() + " lives in " + s);});
        };

        Person p1 = new Person("yoda", 300);
        p1.setCity("London");

        Person p2 = new Person("luke", 50);
        p2.setCity("Rome");

        testBody(null, f1, f2, true);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)[0]).isEqualTo("Paris");
        assertThat(results.get(0)[1]).isSameAs(1);
        assertThat(((Person)results.get(0)[2]).getName()).isEqualTo("yoda");
    }

    @Test
    public void testSingleInputRangeIndexExpressions() {
        VoidFunction1<Pattern> f0 = (spattern) -> {
            PositionalConstraint sconstraint = new PositionalConstraint(new Declaration[]{}, spattern);
            sconstraint.setPredicate((Predicate1<String>) (s) -> {try {
                Integer.valueOf(s);
                return true;
            } catch (Exception e) {
                return false;
            }});
            sconstraint.setType(ConstraintType.ALPHA);
            sconstraint.setConstraintTypeOperator(ConstraintTypeOperator.EQUAL);
            spattern.addConstraint(sconstraint);
        };

        VoidFunction2<Pattern, PositionalConstraint> f1 = (ppattern, pconstraint) -> {
            TupleValueExtractor leftExtractor = new PositionalTupleValueExtractor1((Function1<String, Integer>) (s) -> Integer.valueOf(s), ValueType.INTEGER_TYPE );
            TupleValueExtractor rightExtractor = new PositionalTupleValueExtractor1((Function1<Person, Integer>) (p) -> p.getAge(), ValueType.INTEGER_TYPE );
            pconstraint.setIndex(new IndexSpec(new IndexedValueReader[]{new IndexedValueReader(leftExtractor, rightExtractor)}, ConstraintTypeOperator.LESS_THAN));

            pconstraint.setType(ConstraintType.BETA);
            pconstraint.setConstraintTypeOperator(ConstraintTypeOperator.LESS_THAN);
            ppattern.addConstraint(pconstraint);
        };

        List<Object[]>           results = new ArrayList<>();
        VoidFunction1<PositionalConsequence<?>> f2 = (c) -> {
            c.setFunction((VoidFunction2<String, Person>) (s, p) -> {results.add(new Object[] {s, p}); System.out.println(p.getName() + " is younger than " + s);});
        };

        testBody(f0, f1, f2, false);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)[0]).isEqualTo("50");
        assertThat(((Person)results.get(0)[1]).getName()).isEqualTo("luke");
    }

    @Test
    public void testDoubleInputRangeIndexExpressions() {
        VoidFunction1<Pattern> f0 = (spattern) -> {
            PositionalConstraint sconstraint = new PositionalConstraint(new Declaration[]{}, spattern);
            sconstraint.setPredicate((Predicate1<String>) (s) -> {try {
                Integer.valueOf(s);
                return true;
            } catch (Exception e) {
                return false;
            }});
            sconstraint.setType(ConstraintType.ALPHA);
            sconstraint.setConstraintTypeOperator(ConstraintTypeOperator.EQUAL);
            spattern.addConstraint(sconstraint);
        };

        VoidFunction2<Pattern, PositionalConstraint> f1 = (ppattern, pconstraint) -> {
            TupleValueExtractor leftExtractor = new PositionalTupleValueExtractor2((Function2<String, Integer, Integer>) (s, i) -> Integer.valueOf(s) + i, ValueType.INTEGER_TYPE );
            TupleValueExtractor rightExtractor = new PositionalTupleValueExtractor1((Function1<Person, Integer>) (p) -> p.getAge(), ValueType.INTEGER_TYPE );
            pconstraint.setIndex(new IndexSpec(new IndexedValueReader[]{new IndexedValueReader(leftExtractor, rightExtractor)}, ConstraintTypeOperator.LESS_THAN));

            pconstraint.setType(ConstraintType.BETA);
            pconstraint.setConstraintTypeOperator(ConstraintTypeOperator.LESS_THAN);
            ppattern.addConstraint(pconstraint);
        };

        List<Object[]>           results = new ArrayList<>();
        VoidFunction1<PositionalConsequence<?>> f2 = (c) -> {
            c.setFunction((VoidFunction3<String, Integer, Person>) (s, i, p) -> {results.add(new Object[] {s, i, p}); System.out.println(p.getName() + " is younger than " + s);});
        };

        testBody(f0, f1, f2, true);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)[0]).isEqualTo("50");
        assertThat(results.get(0)[1]).isEqualTo(1);
        assertThat(((Person)results.get(0)[2]).getName()).isEqualTo("luke");
    }

    public void testBody(VoidFunction1<Pattern> sfunc, VoidFunction2<Pattern, PositionalConstraint>  pfunc, VoidFunction1<PositionalConsequence<?>> cfunc,
                         boolean addI) {
        // Note this test only sets indexes for the join, no tests

        RuleImpl r1 = new RuleImpl("r1");

        final ObjectType stringObjectType  = new ClassObjectType(String.class);
        final ObjectType integerObjectType = new ClassObjectType(Integer.class);
        final ObjectType personObjectType  = new ClassObjectType(Person.class);

        final Pattern spattern = new Pattern(0,
                                             stringObjectType,
                                             "s");

        final Pattern ipattern = new Pattern(0,
                                             integerObjectType,
                                             "i");

        final Pattern ppattern = new Pattern(1,
                                             personObjectType,
                                             "p");

        if (sfunc != null) {
            sfunc.apply(spattern);
        }

        r1.addPattern(spattern);

        PositionalConstraint pconstraint = null;
        if (addI) {
            r1.addPattern(ipattern);
            pconstraint = new PositionalConstraint(new Declaration[]{spattern.getDeclaration(), ipattern.getDeclaration()}, ppattern);
        } else {
            pconstraint = new PositionalConstraint(new Declaration[]{spattern.getDeclaration()}, ppattern);
        }
        pfunc.apply(ppattern, pconstraint);

        r1.addPattern(ppattern);

        PositionalConsequence<?> c;
        if (addI) {
            c = new PositionalConsequence<>("default", new Declaration[]{spattern.getDeclaration(), ipattern.getDeclaration(), ppattern.getDeclaration()});
        } else {
            c = new PositionalConsequence<>("default", new Declaration[]{spattern.getDeclaration(), ppattern.getDeclaration()});
        }

        cfunc.apply(c);

        r1.setConsequence(c);

        KnowledgeBaseImpl base = new KnowledgeBaseImpl("default", (CompositeBaseConfiguration)  RuleBaseFactory.newKnowledgeBaseConfiguration());
        KnowledgePackageImpl pkg = new KnowledgePackageImpl();
        pkg.addRule(r1);

        base.addPackage(pkg);

        SessionsAwareKnowledgeBase kbase = new SessionsAwareKnowledgeBase(base);
        KieSession session = kbase.newKieSession();
        session.insert("Paris");
        session.insert("London");
        session.insert(1);

        session.insert("1");
        session.insert("50");

        Person p1 = new Person("yoda", 300);
        p1.setCity("London");

        Person p2 = new Person("luke", 20);
        p2.setCity("Rome");

        session.insert(p1);
        session.insert(p2);
        session.fireAllRules();
    }

}
