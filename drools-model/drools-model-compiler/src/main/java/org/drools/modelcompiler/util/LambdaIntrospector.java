/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler.util;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.drools.core.util.asm.DumpMethodVisitor;
import org.drools.model.functions.IntrospectableLambda;
import org.drools.model.functions.LambdaPrinter;
import org.mvel2.asm.ClassReader;
import org.mvel2.asm.ClassVisitor;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Opcodes;

public class LambdaIntrospector implements LambdaPrinter {

    private static final int CACHE_SIZE = 32;

    private static final Map<ClassIdentifier, Map<String, String>> methodFingerprintsMap = new LinkedHashMap() {
        @Override
        protected boolean removeEldestEntry( Map.Entry eldest) {
            return (size() > CACHE_SIZE);
        }
    };

    @Override
    public String getLambdaFingerprint(Object lambda) {
        if(lambda.toString().equals("INSTANCE")) { // Materialized lambda
            return getExpressionHash(lambda);
        }

        if (lambda instanceof IntrospectableLambda ) {
            lambda = (( IntrospectableLambda ) lambda).getLambda();
        }
        SerializedLambda extracted = extractLambda( (Serializable) lambda );
        String result = getFingerprintsForClass( lambda, extracted ).get( extracted.getImplMethodName() );
        if (result == null) {
            if ( !extracted.getCapturingClass().equals( extracted.getImplClass() ) ) {
                // the lambda is a method reference
                result = extracted.getCapturingClass().replace( '/', '.' ) + "::" + extracted.getImplMethodName();
            } else {
                throw new UnsupportedOperationException( "Unable to introspect lambda " + lambda );
            }
        }
        return result;
    }

    private String getExpressionHash(Object lambda) {
        Field expressionHash;
        try {
            expressionHash = lambda.getClass().getDeclaredField("EXPRESSION_HASH");
            return (String) expressionHash.get(lambda);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException( e );
        }
    }

    private static SerializedLambda extractLambda( Serializable lambda ) {
        try {
            Method method = lambda.getClass().getDeclaredMethod( "writeReplace" );
            method.setAccessible( true );
            return ( SerializedLambda ) method.invoke( lambda );
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    private static Map<String, String> getFingerprintsForClass( Object lambda, SerializedLambda extracted) {
        ClassLoader lambdaClassLoader = lambda.getClass().getClassLoader();
        String className = extracted.getCapturingClass();
        ClassIdentifier id = new ClassIdentifier( lambdaClassLoader, className );
        Map<String, String> fingerprints = methodFingerprintsMap.get( id );

        if (fingerprints == null) {
            LambdaIntrospector.LambdaClassVisitor visitor = new LambdaIntrospector.LambdaClassVisitor();
            try (InputStream classStream = lambdaClassLoader.getResourceAsStream( className.replace( '.', '/' ) + ".class" )) {
                ClassReader reader = new ClassReader( classStream);
                reader.accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            } catch (Exception e) {
                throw new RuntimeException( e );
            }
            fingerprints = visitor.getMethodsMap();
            methodFingerprintsMap.put( id, fingerprints );
        }
        return fingerprints;
    }

    private static class ClassIdentifier {
        private final ClassLoader classLoader;
        private final String className;

        private ClassIdentifier( ClassLoader classLoader, String className ) {
            this.classLoader = classLoader;
            this.className = className;
        }

        @Override
        public boolean equals( Object o ) {
            if (o instanceof ClassIdentifier) {
                ClassIdentifier that = ( ClassIdentifier ) o;
                return className.equals( that.className ) && classLoader == that.classLoader;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return 31 * className.hashCode() + classLoader.hashCode();
        }
    }

    private static class LambdaClassVisitor extends ClassVisitor {

        private final Map<String, String> methodsMap = new HashMap<>();

        LambdaClassVisitor() {
            super( Opcodes.ASM7 );
        }

        @Override
        public MethodVisitor visitMethod( int access, String name, String desc, String signature, String[] exceptions ) {
            return name.startsWith( "lambda$" ) ? new DumpMethodVisitor(s -> setMethodFingerprint(name, s)) : super.visitMethod(access, name, desc, signature, exceptions);
        }

        void setMethodFingerprint( String methodname, String methodFingerprint ) {
            methodsMap.put( methodname, methodFingerprint );
        }

        Map<String, String> getMethodsMap() {
            return methodsMap;
        }
    }
}
