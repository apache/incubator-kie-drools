/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.oopath;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.drools.compiler.oopath.model.Child;
import org.drools.compiler.oopath.model.Man;
import org.drools.compiler.oopath.model.Toy;
import org.drools.compiler.oopath.model.Woman;
import org.drools.core.common.InternalFactHandle;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

public class OOPathBenchmarkTest {

    private static final String RELATIONAL_DRL =
            "import org.drools.compiler.oopath.model.*;\n" +
            "global java.util.List list\n" +
            "\n" +
            "rule R when\n" +
            "    $man : Man()\n" +
            "    $wife : Woman( husband == $man.name )\n" +
            "    $child : Child( mother == $wife.name, age > 10 )\n" +
            "    $toy : Toy( owner == $child.name )\n" +
            "then\n" +
            "    list.add( $toy.getName() );\n" +
            "end\n";

    private static final String FROM_DRL =
            "import org.drools.compiler.oopath.model.*;\n" +
            "global java.util.List list\n" +
            "\n" +
            "rule R when\n" +
            "    $man: Man( $wife: wife )\n" +
            "    $child: Child( age > 10 ) from $wife.children\n" +
            "    $toy: Toy() from $child.toys\n" +
            "then\n" +
            "    list.add( $toy.getName() );\n" +
            "end\n";

    private static final String OOPATH_DRL =
            "import org.drools.compiler.oopath.model.*;\n" +
            "global java.util.List list\n" +
            "\n" +
            "rule R when\n" +
            "  Man( $toy: /wife/children{age > 10}/toys )\n" +
            "then\n" +
            "  list.add( $toy.getName() );\n" +
            "end\n";


    public static void main(String[] args) {
        int n = 100000;
        System.out.println("Relational version");
        runTest(new RelationalTest(), n);
        System.out.println("-------------------------------------");
        System.out.println("From version");
        runTest(new FromTest(), n);
        System.out.println("-------------------------------------");
        System.out.println("OOPath version");
        runTest(new OOPathTest(), n);
    }

    private static void runTest(Test test, int n) {
        final KieBase kbase = getKieBase(test.getDrl());

        // warmup
        for (int i = 0; i < 3; i++) {
            test.runTest(kbase, n);
            System.gc();
        }

        final BenchmarkResult batch = new BenchmarkResult("Batch");
        final BenchmarkResult incremental = new BenchmarkResult("Incremental");
        for (int i = 0; i < 10; i++) {
            long[] result = test.runTest(kbase, n);
            batch.accumulate(result[0]);
            incremental.accumulate(result[1]);
            System.gc();
        }

        System.out.println(batch);
        System.out.println(incremental);
    }

    interface Test {
        long[] runTest(KieBase kbase, int n);
        String getDrl();
    }

    private static class RelationalTest implements Test {
        @Override
        public long[] runTest(KieBase kbase, int n) {
            return testRelational(kbase, n);
        }

        @Override
        public String getDrl() {
            return RELATIONAL_DRL;
        }
    }

    private static class FromTest implements Test {
        @Override
        public long[] runTest(KieBase kbase, int n) {
            return testFrom(kbase, n);
        }

        @Override
        public String getDrl() {
            return FROM_DRL;
        }
    }

    private static class OOPathTest implements Test {
        @Override
        public long[] runTest(KieBase kbase, int n) {
            return testOOPath(kbase, n);
        }

        @Override
        public String getDrl() {
            return OOPATH_DRL;
        }
    }

    public static long[] testRelational(KieBase kbase, int n) {
        final long[] result = new long[2];

        final KieSession ksession = kbase.newKieSession();
        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        final List<Man> model = generateModel(n);
        final List<Child> toBeModified = getChildToBeModified(model);

        long start = System.nanoTime();
        final List<InternalFactHandle> fhs = insertFullModel(ksession, model);
        ksession.fireAllRules();
        result[0] = System.nanoTime() - start;

        list.clear();

        start = System.nanoTime();
        for (Child child : toBeModified) {
            child.setAge(11);
        }
        for (InternalFactHandle fh : fhs) {
            ksession.update(fh, fh.getObject());
        }
        ksession.fireAllRules();
        result[1] = System.nanoTime() - start;

        Assertions.assertThat(n).isEqualTo(list.size());
        ksession.dispose();

        return result;
    }

    public static long[] testFrom(KieBase kbase, int n) {
        final long[] result = new long[2];

        final KieSession ksession = kbase.newKieSession();
        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        final List<Man> model = generateModel(n);
        final List<Child> toBeModified = getChildToBeModified(model);

        long start = System.nanoTime();
        final List<InternalFactHandle> fhs = insertModel(ksession, model);
        ksession.fireAllRules();
        result[0] = System.nanoTime() - start;

        list.clear();

        start = System.nanoTime();
        for (Child child : toBeModified) {
            child.setAge(11);
        }
        for (InternalFactHandle fh : fhs) {
            ksession.update(fh, fh.getObject());
        }
        ksession.fireAllRules();
        result[1] = System.nanoTime() - start;

        Assertions.assertThat(n * 3).isEqualTo(list.size());
        ksession.dispose();

        return result;
    }

    public static long[] testOOPath(KieBase kbase, int n) {
        final long[] result = new long[2];

        final KieSession ksession = kbase.newKieSession();
        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        final List<Man> model = generateModel(n);
        final List<Child> toBeModified = getChildToBeModified(model);

        long start = System.nanoTime();
        insertModel(ksession, model);
        ksession.fireAllRules();
        result[0] = System.nanoTime() - start;

        list.clear();

        start = System.nanoTime();
        for (Child child : toBeModified) {
            child.setAge(11);
        }
        ksession.fireAllRules();
        result[1] = System.nanoTime() - start;

        Assertions.assertThat(n).isEqualTo(list.size());
        ksession.dispose();

        return result;
    }

    private static KieBase getKieBase(String drl) {
        return new KieHelper().addContent(drl, ResourceType.DRL).build();
    }

    private static List<Man> generateModel(int nr) {
        final List<Man> model = new ArrayList<Man>();
        for (int i = 0; i < nr; i++) {
            final Man man = new Man("m" + i, 40);
            model.add(man);
            final Woman woman = new Woman("w" + i, 35);
            man.setWife(woman);
            woman.setHusband(man.getName());

            final Child childA = new Child("cA" + i, 12);
            woman.addChild(childA);
            childA.setMother(woman.getName());
            final Child childB = new Child("cB" + i, 10);
            woman.addChild(childB);
            childB.setMother(woman.getName());

            final Toy toyA = new Toy("tA" + i);
            toyA.setOwner(childA.getName());
            childA.addToy(toyA);
            final Toy toyB = new Toy("tB" + i);
            toyB.setOwner(childA.getName());
            childA.addToy(toyB);
            final Toy toyC = new Toy("tC" + i);
            toyC.setOwner(childB.getName());
            childB.addToy(toyC);
        }
        return model;
    }

    private static List<Child> getChildToBeModified(List<Man> model) {
        final List<Child> toBeModified = new ArrayList<Child>();
        for (Man man : model) {
            for (Child child : man.getWife().getChildren()) {
                if (child.getAge() == 10) {
                    toBeModified.add(child);
                }
            }
        }
        return toBeModified;
    }

    private static List<InternalFactHandle> insertModel(KieSession ksession, List<Man> model) {
        final List<InternalFactHandle> fhs = new ArrayList<InternalFactHandle>();
        for (Man man : model) {
            fhs.add((InternalFactHandle)ksession.insert(man));
        }
        return fhs;
    }

    private static List<InternalFactHandle> insertFullModel(KieSession ksession, List<Man> model) {
        final List<InternalFactHandle> toBeModified = new ArrayList<InternalFactHandle>();
        for (Man man : model) {
            ksession.insert(man);
            ksession.insert(man.getWife());
            for (Child child : man.getWife().getChildren()) {
                final InternalFactHandle fh = (InternalFactHandle)ksession.insert(child);
                if (child.getAge() == 10) {
                    toBeModified.add(fh);
                }
                for (Toy toy : child.getToys()) {
                    ksession.insert(toy);
                }
            }
        }
        return toBeModified;
    }

    public static class BenchmarkResult {
        private final String name;

        private long min = Long.MAX_VALUE;
        private long max = 0;
        private long sum = 0;
        private int counter = 0;

        public BenchmarkResult(String name) {
            this.name = name;
        }

        public void accumulate(long result) {
            if (result < min) {
                min = result;
            }
            if (result > max) {
                max = result;
            }
            sum += result;
            counter++;
        }

        private long getAverage() {
            return (sum - min - max) / (counter - 2);
        }

        @Override
        public String toString() {
            return name + " results: min = " + min + "; max = " + max + "; avg = " + getAverage();
        }
    }
}