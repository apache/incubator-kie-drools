package org.drools.rule.builder.dialect.asm;

import org.junit.*;

import java.util.*;

import static junit.framework.Assert.*;

public class ASMConsequenceStubBuilderTest {

    @Test
    public void testGenerate() {
        Map<String, Object> consequenceContext = new HashMap<String, Object>();
        consequenceContext.put("package", "pkg");
        consequenceContext.put("invokerClassName", "TestInvoker");
        consequenceContext.put("ruleClassName", "TestRule");
        consequenceContext.put("methodName", "testMethod");
        consequenceContext.put("consequenceName", "TestConsequence");
        consequenceContext.put("hashCode", 111);
        consequenceContext.put("declarationTypes", new String[] { "java/lang/String", "java/lang/Integer" });
        consequenceContext.put("globals", new String[] { "globalList" });
        consequenceContext.put("globalTypes", new String[] { "java/util/List" });
        consequenceContext.put("notPatterns", new Boolean[] { true, false });

        Set<String> imports = new HashSet<String>();
        imports.add("p1");
        imports.add("p2");

        final ClassGenerator generator = new ClassGenerator("pkg.TestInvoker", getClass().getClassLoader());
        new ASMConsequenceStubBuilder().generateConsequence(generator, consequenceContext, imports);

        ConsequenceStub stub = generator.newInstance();
        assertEquals("pkg", stub.getPackageName());
        assertEquals("TestRule", stub.getRuleClassName());
        assertEquals("testMethod", stub.getMethodName());
        assertEquals("TestInvokerGenerated", stub.getConsequenceClassName());
        assertEquals(111, stub.hashCode());
        assertTrue(Arrays.equals(new String[]{"java/lang/String", "java/lang/Integer"}, stub.getDeclarationTypes()));
        assertTrue(Arrays.equals(new String[] { "globalList" }, stub.getGlobals()));
        assertTrue(Arrays.equals(new String[] { "java/util/List" }, stub.getGlobalTypes()));
        assertTrue(Arrays.equals(new Boolean[] { true, false }, stub.getNotPatterns()));
        List<String> importList = Arrays.asList(stub.getPackageImports());
        assertTrue(importList.contains("p1"));
        assertTrue(importList.contains("p2"));
    }
}
