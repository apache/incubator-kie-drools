package org.drools.mvel;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

public class MVEL {

    public static Serializable compileExpression(String s) {
        return s;
    }

    public static Serializable compileExpression(String compiledExpression, ParserContext parserContext) {
        Evaluator evaluator = new Evaluator();
        Map vars = parserContext.getMap();
        return new Evaluator().compileEvaluateWithDroolsMvelCompiler(compiledExpression, vars, evaluator.getClass().getClassLoader());
    }

    public static Serializable executeExpression(final Object compiledExpression, final Map vars) {
        Evaluator evaluator = new Evaluator();
        return evaluator.compileEvaluateWithDroolsMvelCompiler(compiledExpression, vars, evaluator.getClass().getClassLoader());
    }

    public static Serializable executeExpression(final Object compiledExpression) {
        return executeExpression(compiledExpression, createTestMap());
    }

    public static Object test(final Object compiledExpression) {
        return executeExpression(compiledExpression, createTestMap());
    }

    public static Object eval(String ex, Map vars) {
        return executeExpression(ex, vars);
    }

    public static Object eval(String ex, Class<?> vars) {
        return executeExpression(ex, createTestMap());
    }

    public static Object executeExpression(Object stmt, Object o, Map<String, Object> vars) {
        return executeExpression(stmt, createTestMap());
    }

    public static Object eval(String expression) {
        return executeExpression(expression, createTestMap());
    }

    public static Object testCompiledSimple(String ex, Map hashMap) {
        return executeExpression(ex, hashMap);
    }

    protected static Map createTestMap() {
        Map map = new HashMap();
        map.put("foo", new Foo());
        map.put("a", null);
        map.put("b", null);
        map.put("c", "cat");
        map.put("BWAH", "");

        map.put("misc", new ArithmeticTests.MiscTestClass());

        map.put("pi", "3.14");
        map.put("hour", 60);
        map.put("zero", 0);

        map.put("array", new String[]{"", "blip"});

        map.put("order", new ArithmeticTests.Order());
        map.put("$id", 20);

        map.put("five", 5);

        map.put("testImpl",
                new TestInterface() {

                    public String getName() {
                        return "FOOBAR!";
                    }

                    public boolean isFoo() {
                        return true;
                    }
                });

        map.put("derived", new DerivedClass());

        map.put("ipaddr", "10.1.1.2");

        map.put("dt1", new Date(currentTimeMillis() - 100000));
        map.put("dt2", new Date(currentTimeMillis()));
        return map;
    }
}
