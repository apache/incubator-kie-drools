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
package org.drools.mvel.asm;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import org.mvel2.asm.ClassReader;
import org.mvel2.asm.ClassVisitor;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Opcodes;

public class LambdaIntrospector implements Function<Object, String> {

    public static final String LAMBDA_INTROSPECTOR_CACHE_SIZE = "drools.lambda.introspector.cache.size";
    private static final int CACHE_SIZE = Integer.parseInt(System.getProperty(LAMBDA_INTROSPECTOR_CACHE_SIZE, "32"));

    private static final Map<ClassLoader, ClassesFingerPrintsCache> methodFingerprintsMapPerClassLoader = Collections.synchronizedMap( new WeakHashMap<>() );

    static Map<ClassLoader, ClassesFingerPrintsCache> getMethodFingerprintsMapPerClassLoader() {
        return methodFingerprintsMapPerClassLoader;
    }

    @Override
    public String apply(Object lambda) {
        if (lambda.toString().equals("INSTANCE")) { // Materialized lambda
            return getExpressionHash(lambda);
        }

        if (lambda instanceof Supplier) {
            lambda = (( Supplier ) lambda).get();
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

    private static String getExpressionHash(Object lambda) {
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

    private static MethodsFingerPrintsCache getFingerprintsForClass(Object lambda, SerializedLambda extracted) {
        ClassLoader lambdaClassLoader = lambda.getClass().getClassLoader();
        String className = extracted.getCapturingClass();
        if (CACHE_SIZE <= 0) {
            return MethodsFingerPrintsCache.getFingerPrints(lambdaClassLoader, className); // don't even create an entry
        }
        ClassesFingerPrintsCache methodFingerprintsMap = methodFingerprintsMapPerClassLoader.computeIfAbsent(lambdaClassLoader, ClassesFingerPrintsCache::new);
        return methodFingerprintsMap.registerMethodFingerPrints(className);
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

    static class ClassesFingerPrintsCache {
        private final ClassLoader lambdaClassLoader;

        private final Map<String, MethodsFingerPrintsCache> map = Collections.synchronizedMap( new LinkedHashMap<String, MethodsFingerPrintsCache>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, MethodsFingerPrintsCache> eldest) {
                return (size() > CACHE_SIZE);
            }
        });

        ClassesFingerPrintsCache( ClassLoader lambdaClassLoader ) {
            this.lambdaClassLoader = lambdaClassLoader;
        }

        public MethodsFingerPrintsCache registerMethodFingerPrints(String className) {
            return map.computeIfAbsent(className, k -> MethodsFingerPrintsCache.getFingerPrints(lambdaClassLoader, className));
        }

        public int size() {
            return map.size();
        }
    }

    static class MethodsFingerPrintsCache {
        private final Map<String, String> map;

        private MethodsFingerPrintsCache( Map<String, String> map ) {
            this.map = map;
        }

        private static MethodsFingerPrintsCache getFingerPrints(ClassLoader lambdaClassLoader, String className) {
            LambdaIntrospector.LambdaClassVisitor visitor = new LambdaIntrospector.LambdaClassVisitor();
            try (InputStream classStream = lambdaClassLoader.getResourceAsStream( className.replace( '.', '/' ) + ".class" )) {
                ClassReader reader = new ClassReader( classStream);
                reader.accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            } catch (Exception e) {
                throw new RuntimeException( e );
            }
            return new MethodsFingerPrintsCache( visitor.getMethodsMap() );
        }

        public String get( String implMethodName ) {
            return map.get(implMethodName);
        }
    }
}
