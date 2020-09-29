/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.traits.core.factmodel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.core.factmodel.BuildUtils;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.Trait;
import org.drools.mvel.asm.AsmUtil;
import org.drools.mvel.asm.ClassFieldInspectorImpl;
import org.mvel2.asm.ClassWriter;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Type;

import static java.util.Arrays.asList;

import static org.mvel2.asm.Opcodes.ACC_PUBLIC;
import static org.mvel2.asm.Opcodes.ALOAD;
import static org.mvel2.asm.Opcodes.GETFIELD;
import static org.mvel2.asm.Opcodes.INVOKEVIRTUAL;

public class TraitBuilderUtil {

    static MixinInfo findMixinInfo(Class<?> traitClass) {
        if ( traitClass == null ) {
            return null;
        }
        Map<Class<?>, List<Method>> mixinMethodMap = findMixinMethodImpls( traitClass );
        if ( mixinMethodMap.isEmpty() ) {
            return null;
        }

        MixinInfo mixinInfo = new MixinInfo( traitClass );
        try {
            mixinInfo.mixinClasses = new ArrayList<>();
            mixinInfo.mixinClasses.addAll( mixinMethodMap.keySet() );
            mixinInfo.mixinMethods = new HashMap<>();
            mixinInfo.mixinGetSet = new HashMap<>();

            for (Map.Entry<Class<?>, List<Method>> entry : mixinMethodMap.entrySet()) {
                Class<?> mixinClass = entry.getKey();
                ClassFieldInspectorImpl cfi = new ClassFieldInspectorImpl( mixinClass );

                for ( Method m : entry.getValue() ) {
                    try {
                        traitClass.getMethod( m.getName(), m.getParameterTypes() );
                        if ( cfi.getGetterMethods().containsValue( m ) || cfi.getSetterMethods().containsValue( m )) {
	                        Map<String, Method> map = mixinInfo.mixinGetSet.computeIfAbsent( mixinClass, k -> new HashMap<>() );
	                        map.put( m.getName(), m );
                        } else {
	                        Set<Method> set = mixinInfo.mixinMethods.computeIfAbsent( mixinClass, k -> new HashSet<>() );
	                        set.add( m );
                        }
                    } catch ( NoSuchMethodException ignored ) {

                    }
                }

            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return mixinInfo;
    }

    private static Map<Class<?>, List<Method>> findMixinMethodImpls(Class<?> traitClass) {
        // Use a LinkedHashMap to preserve the order of the names in the extends clause of declaration
        Map<Class<?>, List<Method>> map = new LinkedHashMap<>();
        findMixinMethodImpls(traitClass, map);
        return map;
    }

    private static void findMixinMethodImpls(Class<?> traitClass, Map<Class<?>, List<Method>> map) {
        Trait annTrait = getAnnotation(traitClass, Trait.class );
        if ( hasImpl( annTrait ) ) {
            Class<?> mixinClass = annTrait.impl();
            map.put( mixinClass, asList( mixinClass.getMethods() ) );
        }
        if (traitClass.getSuperclass() != null) {
            findMixinMethodImpls(traitClass.getSuperclass(), map);
        }
        for (Class<?> intf : traitClass.getInterfaces()) {
            findMixinMethodImpls(intf, map);
        }
    }

    static class MixinInfo {
        final Class<?> traitClass;

        List<Class<?>> mixinClasses = null;
        Map<Class<?>, Set<Method>> mixinMethods = null;
        Map<Class<?>, Map<String, Method>> mixinGetSet = null;

        MixinInfo( Class<?> traitClass ) {
            this.traitClass = traitClass;
        }

        boolean isMixinGetter( FieldDefinition field ) {
            String getter = BuildUtils.getterName( field.getName(), field.getTypeName() );
            for (Map<String, Method> map : mixinGetSet.values()) {
                if ( map.containsKey( getter ) ) {
                    return true;
                }
            }
            return false;
        }

        public boolean throwsErrorOnConflict() {
            return traitClass.getAnnotation( Trait.class ).mixinSolveConflicts() == Trait.MixinConflictResolutionStrategy.ERROR_ON_CONFLICT;
        }
    }

    static String getMixinName(Class<?> mixinClass) {
        return mixinClass.getSimpleName().substring(0,1).toLowerCase() + mixinClass.getSimpleName().substring(1);
    }

    private static boolean hasImpl( Trait annTrait ) {
        return annTrait != null && ! annTrait.impl().equals( Trait.NullMixin.class );
    }

    private static <K extends Annotation> K getAnnotation( Class<?> klass, Class<K> annotationClass ) {
        if ( klass.equals( Thing.class ) ) {
            return null;
        }
        K ann = klass.getAnnotation( annotationClass );

        if ( ann == null ) {
            for ( Class sup : klass.getInterfaces() ) {
                ann = getAnnotation( sup, annotationClass );
                if ( ann != null ) {
                    return ann;
                }
            }
            return null;
        } else {
            return ann;
        }
    }

    static void buildMixinMethods( String masterName, MixinInfo mixinInfo, ClassWriter cw ) {
        if ( mixinInfo == null ) {
            return;
        }
        Set<String> createdSignatures = new HashSet<>();
        for ( Class<?> mixinClass : mixinInfo.mixinClasses ) {
            String mixin = getMixinName( mixinClass );

            Set<Method> methods = mixinInfo.mixinMethods.get( mixinClass );
            if (methods != null) {
                buildMixinMethods( cw, masterName, mixin, mixinClass, mixinInfo, methods, createdSignatures );
            }

            Map<String, Method> map = mixinInfo.mixinGetSet.get( mixinClass );
            if (map != null) {
                buildMixinMethods( cw, masterName, mixin, mixinClass, mixinInfo, map.values(), createdSignatures );
            }
        }
    }

    private static void buildMixinMethods( ClassWriter cw, String wrapperName, String mixin, Class mixinClass,
                                           MixinInfo mixinInfo, Collection<Method> mixinMethods, Set<String> createdSignatures ) {
        for ( Method method : mixinMethods ) {
            String signature = TraitFactoryImpl.buildSignature(method );
            String methodSignature = method.getName() + signature;
            if (createdSignatures.contains( methodSignature )) {
                if (mixinInfo.throwsErrorOnConflict()) {
                    throw new RuntimeException( "Conflict on method: " + method.getName() );
                }
                continue;
            }
            createdSignatures.add(methodSignature);

            {
                MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                                                   method.getName(),
                                                   signature,
                                                   null,
                                                   null );
                mv.visitCode();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), mixin, Type.getDescriptor( mixinClass ) );
                int j = 1;
                for ( Class arg : method.getParameterTypes() ) {
                    mv.visitVarInsn( AsmUtil.varType( arg.getName() ), j++ );
                }
                mv.visitMethodInsn( INVOKEVIRTUAL,
                                    Type.getInternalName( mixinClass ),
                                    method.getName(),
                                    signature,
                                    false );

                mv.visitInsn( AsmUtil.returnType( method.getReturnType().getName() ) );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();
            }
        }
    }
}

