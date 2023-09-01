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
package org.drools.mvel.rule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.security.CodeSource;

import org.drools.base.base.ValueResolver;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.base.reteoo.BaseTuple;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.EvalCondition;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.base.rule.accessor.EvalExpression;
import org.junit.Test;
import org.kie.internal.conf.CompositeBaseConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

public class PackageCompilationDataTest {
    public static class TestEvalExpression implements EvalExpression {
        public Object createContext() { return null; }
        public boolean evaluate(BaseTuple t, Declaration[] d, ValueResolver valueResolver, Object context) {
            return false;
        }
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        }

        public void writeExternal(ObjectOutput out) throws IOException {

        }
        public Declaration[] getRequiredDeclarations() {
            return null;
        }
        public void replaceDeclaration(Declaration declaration,
                                       Declaration resolved) {
        }
        public EvalExpression clone() {
            return this;
        }
    }

    @Test
    public void testCodeSourceUrl() throws Exception {
        final String className = TestEvalExpression.class.getName();

        KnowledgeBaseImpl kBase = new KnowledgeBaseImpl("xxx", (CompositeBaseConfiguration) RuleBaseFactory.newKnowledgeBaseConfiguration());

        InternalKnowledgePackage pkg = CoreComponentFactory.get().createKnowledgePackage("org.drools");
        pkg.setClassLoader( Thread.currentThread().getContextClassLoader() );
        JavaDialectRuntimeData data = new JavaDialectRuntimeData();
        data.onAdd(pkg.getDialectRuntimeRegistry(), kBase.getRootClassLoader());
        pkg.getDialectRuntimeRegistry().setDialectData("java", data);
        
        kBase.addPackage(pkg);
        
        final JavaDialectRuntimeData pcData = ( JavaDialectRuntimeData ) pkg.getDialectRuntimeRegistry().getDialectData( "java" );
        
        
        final EvalCondition invoker = new EvalCondition(null);
        pcData.putInvoker(className, invoker);
        final InputStream is = getClass().getClassLoader().getResourceAsStream(className.replace('.', '/') + ".class");
        try {
            pcData.write(className.replace('.', '/') + ".class", read(is));
        } finally {
            is.close();
        }
        
        pcData.onAdd(pkg.getDialectRuntimeRegistry(), kBase.getRootClassLoader());
        pcData.onBeforeExecute();
        
        Class cls = kBase.getRootClassLoader().loadClass( "org.drools.mvel.rule.PackageCompilationDataTest$TestEvalExpression" );
        
        final CodeSource codeSource = invoker.getEvalExpression().getClass().getProtectionDomain().getCodeSource();
        assertThat(codeSource.getLocation()).isNotNull();
    }

    private static byte[] read(final InputStream is) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final byte[] b = new byte[1024];
        int len;
        while ((len = is.read(b)) > 0) {
            os.write(b, 0, len);
        }
        return os.toByteArray();
    }
}
