/**
 * Copyright 2010 JBoss Inc
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

package org.drools.rule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.security.CodeSource;

import junit.framework.TestCase;

import org.drools.base.ClassFieldAccessorCache;
import org.drools.common.InternalRuleBase;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.WorkingMemory;
import org.drools.spi.EvalExpression;
import org.drools.spi.Tuple;

public class PackageCompilationDataTest extends TestCase {
    public static class TestEvalExpression implements EvalExpression {
        public Object createContext() { return null; }
        public boolean evaluate(Tuple t, Declaration[] d, WorkingMemory w, Object context ) {
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

    public void testCodeSourceUrl() throws Exception {
        final String className = TestEvalExpression.class.getName();
        
        ReteooRuleBase rb = new ReteooRuleBase( "xxx" );
        
        Package pkg = new Package( "org.drools" );
        pkg.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        JavaDialectRuntimeData data = new JavaDialectRuntimeData();
        data.onAdd( pkg.getDialectRuntimeRegistry(), rb.getRootClassLoader()  );
        pkg.getDialectRuntimeRegistry().setDialectData( "java", data );
        
        rb.addPackage( pkg );
        
        final JavaDialectRuntimeData pcData = ( JavaDialectRuntimeData ) pkg.getDialectRuntimeRegistry().getDialectData( "java" );
        
        
        final EvalCondition invoker = new EvalCondition(null);
        pcData.putInvoker(className, invoker);
        final InputStream is = getClass().getClassLoader().getResourceAsStream(className.replace('.', '/') + ".class");
        try {
            pcData.write(className.replace('.', '/') + ".class", read(is));
        } finally {
            is.close();
        }                
        
        pcData.onAdd( pkg.getDialectRuntimeRegistry(), rb.getRootClassLoader() );
        pcData.onBeforeExecute();
        
        Class cls = ((InternalRuleBase)rb).getRootClassLoader().loadClass( "org.drools.rule.PackageCompilationDataTest$TestEvalExpression" );
        
        System.out.println( cls );
        
        final CodeSource codeSource = invoker.getEvalExpression().getClass().getProtectionDomain().getCodeSource();
        assertNotNull(codeSource.getLocation());
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