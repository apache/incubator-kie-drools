package org.drools.compiler.xpath;

import org.drools.core.common.InternalFactHandle;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class XpathBenchmarkTest {

    private static final String RELATIONAL_DRL =
            "import org.drools.compiler.xpath.*;\n" +
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
            "import org.drools.compiler.xpath.*;\n" +
            "global java.util.List list\n" +
            "\n" +
            "rule R when\n" +
            "    $man: Man( $wife: wife )\n" +
            "    $child: Child( age > 10 ) from $wife.children\n" +
            "    $toy: Toy() from $child.toys\n" +
            "then\n" +
            "    list.add( $toy.getName() );\n" +
            "end\n";

    private static final String XPATH_DRL =
            "import org.drools.compiler.xpath.*;\n" +
            "global java.util.List list\n" +
            "\n" +
            "rule R when\n" +
            "  Man( $toy: /wife/children[age > 10]/toys )\n" +
            "then\n" +
            "  list.add( $toy.getName() );\n" +
            "end\n";


    public static void main(String[] args) {
        int n = 50000;
        System.out.println("Relational version");
        runTest(new RelationalTest(), n);
        System.out.println("-------------------------------------");
        System.out.println("From version");
        runTest(new FromTest(), n);
        System.out.println("-------------------------------------");
        System.out.println("Xpath version");
        runTest(new XpathTest(), n);
    }

    private static void runTest(Test test, int n) {
        KieBase kbase = getKieBase(test.getDrl());

        // warmup
        for (int i = 0; i < 3; i++) {
            test.runTest(kbase, n);
            System.gc();
        }

        BenchmarkResult batch = new BenchmarkResult("Batch");
        BenchmarkResult incremental = new BenchmarkResult("Incremental");
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

    private static class XpathTest implements Test {
        @Override
        public long[] runTest(KieBase kbase, int n) {
            return testXPath(kbase, n);
        }

        @Override
        public String getDrl() {
            return XPATH_DRL;
        }
    }

    public static long[] testRelational(KieBase kbase, int n) {
        long[] result = new long[2];

        KieSession ksession = kbase.newKieSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        List<Man> model = generateModel(n);
        List<Child> toBeModified = getChildToBeModified(model);

        long start = System.nanoTime();
        List<InternalFactHandle> fhs = insertFullModel(ksession, model);
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

        assertEquals(n, list.size());

        return result;
    }

    public static long[] testFrom(KieBase kbase, int n) {
        long[] result = new long[2];

        KieSession ksession = kbase.newKieSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        List<Man> model = generateModel(n);
        List<Child> toBeModified = getChildToBeModified(model);

        long start = System.nanoTime();
        List<InternalFactHandle> fhs = insertModel(ksession, model);
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

        assertEquals(n * 3, list.size());

        return result;
    }

    public static long[] testXPath(KieBase kbase, int n) {
        long[] result = new long[2];

        KieSession ksession = kbase.newKieSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        List<Man> model = generateModel(n);
        List<Child> toBeModified = getChildToBeModified(model);

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

        assertEquals(n, list.size());

        return result;
    }

    private static KieBase getKieBase(String drl) {
        return new KieHelper().addContent(drl, ResourceType.DRL).build();
    }

    private static List<Man> generateModel(int nr) {
        List<Man> model = new ArrayList<Man>();
        for (int i = 0; i < nr; i++) {
            Man man = new Man("m" + i, 40);
            model.add(man);
            Woman woman = new Woman("w" + i, 35);
            man.setWife(woman);
            woman.setHusband(man.getName());

            Child childA = new Child("cA" + i, 12);
            woman.addChild(childA);
            childA.setMother(woman.getName());
            Child childB = new Child("cB" + i, 10);
            woman.addChild(childB);
            childB.setMother(woman.getName());

            Toy toyA = new Toy("tA" + i);
            toyA.setOwner(childA.getName());
            childA.addToy(toyA);
            Toy toyB = new Toy("tB" + i);
            toyB.setOwner(childA.getName());
            childA.addToy(toyB);
            Toy toyC = new Toy("tC" + i);
            toyC.setOwner(childB.getName());
            childB.addToy(toyC);
        }
        return model;
    }

    private static List<Child> getChildToBeModified(List<Man> model) {
        List<Child> toBeModified = new ArrayList<Child>();
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
        List<InternalFactHandle> fhs = new ArrayList<InternalFactHandle>();
        for (Man man : model) {
            fhs.add((InternalFactHandle)ksession.insert(man));
        }
        return fhs;
    }

    private static List<InternalFactHandle> insertFullModel(KieSession ksession, List<Man> model) {
        List<InternalFactHandle> toBeModified = new ArrayList<InternalFactHandle>();
        for (Man man : model) {
            ksession.insert(man);
            ksession.insert(man.getWife());
            for (Child child : man.getWife().getChildren()) {
                InternalFactHandle fh = (InternalFactHandle)ksession.insert(child);
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