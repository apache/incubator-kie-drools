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
import java.util.WeakHashMap;

import org.drools.core.util.asm.DumpMethodVisitor;
import org.drools.model.functions.IntrospectableLambda;
import org.drools.model.functions.LambdaPrinter;
import org.mvel2.asm.ClassReader;
import org.mvel2.asm.ClassVisitor;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Opcodes;

public class LambdaIntrospector implements LambdaPrinter {

    public static final String LAMBDA_INTROSPECTOR_CACHE_SIZE = "drools.lambda.introspector.cache.size";
    private static final int CACHE_SIZE = Integer.parseInt(System.getProperty(LAMBDA_INTROSPECTOR_CACHE_SIZE, "32"));

    private static final Map<ClassLoader, Map<String, Map<String, String>>> methodFingerprintsMapPerClassLoader = new WeakHashMap<>();

    static Map<ClassLoader, Map<String, Map<String, String>>> getMethodFingerprintsMapPerClassLoader() {
        return methodFingerprintsMapPerClassLoader;
    }

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

    private static Map<String, String> getFingerprintsForClass(Object lambda, SerializedLambda extracted) {
        ClassLoader lambdaClassLoader = lambda.getClass().getClassLoader();
        String className = extracted.getCapturingClass();
        if (CACHE_SIZE <= 0) {
            return getFingerPrints(lambdaClassLoader, className); // don't even create an entry
        }
        Map<String, Map<String, String>> methodFingerprintsMap = methodFingerprintsMapPerClassLoader.computeIfAbsent(lambdaClassLoader, k -> new LinkedHashMap<String, Map<String, String>>() {

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Map<String, String>> eldest) {
                return (size() > CACHE_SIZE);
            }
        });
        return methodFingerprintsMap.computeIfAbsent(className, k -> getFingerPrints(lambdaClassLoader, className));
    }

    private static Map<String, String> getFingerPrints(ClassLoader lambdaClassLoader, String className) {
        Map<String, String> fingerprints;
        LambdaIntrospector.LambdaClassVisitor visitor = new LambdaIntrospector.LambdaClassVisitor();
        try (InputStream classStream = lambdaClassLoader.getResourceAsStream( className.replace( '.', '/' ) + ".class" )) {
            ClassReader reader = new ClassReader( classStream);
            reader.accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
        fingerprints = visitor.getMethodsMap();
        return fingerprints;
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
