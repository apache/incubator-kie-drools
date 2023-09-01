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
package org.drools.mvel.compiler.rule.builder.dialect.asm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.base.rule.Declaration;
import org.drools.mvel.asm.ClassGenerator;
import org.drools.mvel.asm.InvokerContext;
import org.drools.mvel.asm.InvokerGenerator;
import org.drools.mvel.asm.InvokerStub;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(stub.getPackageName()).isEqualTo("pkg");
        assertThat(stub.getRuleClassName()).isEqualTo("TestRule");
        assertThat(stub.getMethodName()).isEqualTo("testMethod");
        assertThat(stub.getGeneratedInvokerClassName()).isEqualTo("TestInvokerGenerated");
        assertThat(stub.hashCode()).isEqualTo(111);
        assertThat(Arrays.equals(new String[]{"globalList"}, stub.getGlobals())).isTrue();
        assertThat(Arrays.equals(new String[]{"java/util/List"}, stub.getGlobalTypes())).isTrue();
        List<String> importList = Arrays.asList(stub.getPackageImports());
        assertThat(importList.contains("p1")).isTrue();
        assertThat(importList.contains("p2")).isTrue();
    }
}
