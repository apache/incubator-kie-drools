package org.drools.compiler.rule.builder.dialect.asm;

import org.drools.core.rule.Declaration;
import org.drools.core.rule.builder.dialect.asm.ClassGenerator;
import org.drools.core.rule.builder.dialect.asm.InvokerStub;
import org.junit.*;

import java.util.*;

import static org.junit.Assert.*;

public class InvokerGeneratorTest {

    @Test
    public void testGenerate() {
        Map<String, Object> invokerContext = new HashMap<String, Object>();
        invokerContext.put("package", "pkg");
        invokerContext.put("invokerClassName", "TestInvoker");
        invokerContext.put("ruleClassName", "TestRule");
        invokerContext.put("methodName", "testMethod");
        invokerContext.put("consequenceName", "TestConsequence");
        invokerContext.put("hashCode", 111);
        invokerContext.put("declarations", new Declaration[0]);
        invokerContext.put("globals", new String[]{"globalList"});
        invokerContext.put("globalTypes", new String[]{"java/util/List"});

        Set<String> imports = new HashSet<String>();
        imports.add("p1");
        imports.add("p2");

        final ClassGenerator generator = InvokerGenerator.createStubGenerator(new InvokerContext(invokerContext), getClass().getClassLoader(), null, imports);
        generator.setInterfaces(InvokerStub.class);

        InvokerStub stub = generator.newInstance();
        assertEquals("pkg", stub.getPackageName());
        assertEquals("TestRule", stub.getRuleClassName());
        assertEquals("testMethod", stub.getMethodName());
        assertEquals("TestInvokerGenerated", stub.getGeneratedInvokerClassName());
        assertEquals(111, stub.hashCode());
        assertTrue(Arrays.equals(new String[] { "globalList" }, stub.getGlobals()));
        assertTrue(Arrays.equals(new String[] { "java/util/List" }, stub.getGlobalTypes()));
        List<String> importList = Arrays.asList(stub.getPackageImports());
        assertTrue(importList.contains("p1"));
        assertTrue(importList.contains("p2"));
    }
}
