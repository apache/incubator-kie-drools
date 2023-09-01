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
package org.drools.traits.core.factmodel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.impl.InternalRuleBase;
import org.drools.mvel.accessors.ClassFieldAccessor;
import org.drools.mvel.accessors.ClassFieldAccessorStore;
import org.drools.compiler.builder.impl.classbuilder.BuildUtils;
import org.drools.base.factmodel.ClassDefinition;
import org.drools.base.factmodel.FieldDefinition;
import org.drools.base.factmodel.traits.CoreWrapper;
import org.drools.base.factmodel.traits.Thing;
import org.drools.base.factmodel.traits.Trait;
import org.drools.base.factmodel.traits.TraitType;
import org.drools.base.factmodel.traits.Traitable;
import org.drools.base.factmodel.traits.TraitableBean;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.mvel.asm.AsmUtil;
import org.drools.mvel.asm.ClassFieldInspectorImpl;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Opcodes;
import org.mvel2.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTraitFactory<T extends Thing<K>, K extends TraitableBean> implements Opcodes, Externalizable {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTraitFactory.class);

    protected VirtualPropertyMode mode = VirtualPropertyMode.MAP;

    public static final String SUFFIX = "_Trait__Extension";

    protected static final String PACKAGE = "org.drools.base.factmodel.traits.";

    protected Map<String, Constructor> factoryCache = new HashMap<>();

    protected Map<Class, Class<? extends CoreWrapper<?>>> wrapperCache = new HashMap<>();

    private final static TraitClassBuilderFactory traitClassBuilderFactory = new TraitClassBuilderFactory();

    public AbstractTraitFactory() {
    }

    protected static void setMode(VirtualPropertyMode newMode, InternalRuleBase kBase, RuntimeComponentFactory rcf) {
        TraitFactoryImpl traitFactory = (TraitFactoryImpl) rcf.getTraitFactory(kBase);
        traitFactory.mode = newMode;
        switch (newMode) {
            case MAP:
                if (!(traitClassBuilderFactory.getPropertyWrapperBuilder() instanceof TraitMapProxyClassBuilderImpl)) {
                    traitClassBuilderFactory.setPropertyWrapperBuilder(new TraitMapPropertyWrapperClassBuilderImpl());
                }
                if (!(traitClassBuilderFactory.getTraitProxyBuilder() instanceof TraitMapProxyClassBuilderImpl)) {
                    traitClassBuilderFactory.setTraitProxyBuilder(new TraitMapProxyClassBuilderImpl());
                }
                break;
            case TRIPLES:
                if (!(traitClassBuilderFactory.getPropertyWrapperBuilder() instanceof TraitTriplePropertyWrapperClassBuilderImpl)) {
                    traitClassBuilderFactory.setPropertyWrapperBuilder(new TraitTriplePropertyWrapperClassBuilderImpl());
                }
                if (!(traitClassBuilderFactory.getTraitProxyBuilder() instanceof TraitTripleProxyClassBuilderImpl)) {
                    traitClassBuilderFactory.setTraitProxyBuilder(new TraitTripleProxyClassBuilderImpl());
                }
                break;
            default:
                throw new RuntimeException(" This should not happen : unexpected property wrapping method " + newMode);
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(mode);
        out.writeObject(factoryCache);
        out.writeObject(wrapperCache);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        mode = (VirtualPropertyMode) in.readObject();
        factoryCache = (Map<String, Constructor>) in.readObject();
        wrapperCache = (Map<Class, Class<? extends CoreWrapper<?>>>) in.readObject();
    }

    @Deprecated()
    /*
     * Test compatiblity only, do not use
     */
    public T getProxy(K core, Class<?> trait) throws LogicalTypeInconsistencyException {
        return getProxy(core, trait, false);
    }

    public T getProxy(K core, Class<?> trait, boolean logical) throws LogicalTypeInconsistencyException {
        String traitName = trait.getName();

        if (core.hasTrait(traitName)) {
            return (T) core.getTrait(traitName);
        }

        String key = getKey(core.getClass(), trait);

        Constructor<T> konst;
        synchronized (this) {
            konst = factoryCache.get(key);
            if (konst == null) {
                konst = cacheConstructor(key, core, trait);
            }
        }

        T proxy;
        HierarchyEncoder hier = getHierarchyEncoder();
        try {
            switch (mode) {
                case MAP:
                    proxy = konst.newInstance(core, core._getDynamicProperties(), hier.getCode(trait.getName()), hier.getBottom(), logical);
                    break;
                case TRIPLES:
                    proxy = konst.newInstance(core, getTripleStore(), getTripleFactory(), hier.getCode(trait.getName()), hier.getBottom(), logical);
                    break;
                default:
                    throw new RuntimeException(" This should not happen : unexpected property wrapping method " + mode);
            }

            return proxy;
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            LOG.error("Exception", e);
        }
        throw new LogicalTypeInconsistencyException("Could not apply trait " + trait + " to object " + core, trait, core.getClass());
    }

    protected Constructor<T> cacheConstructor(String key, K core, Class<?> trait) {
        Class<T> proxyClass = buildProxyClass(core, trait);
        if (proxyClass == null) {
            return null;
        }
        try {
            Constructor<T> konst;

            switch (mode) {
                case MAP:
                    konst = proxyClass.getConstructor(core.getClass(), Map.class, BitSet.class, BitSet.class, boolean.class);
                    break;
                case TRIPLES:
                    konst = proxyClass.getConstructor(core.getClass(), TripleStore.class, TripleFactory.class, BitSet.class, BitSet.class, boolean.class);
                    break;
                default:
                    throw new RuntimeException(" This should not happen : unexpected property wrapping method " + mode);
            }

            factoryCache.put(key, konst);
            return konst;
        } catch (NoSuchMethodException e) {
            LOG.error("Exception", e);
            return null;
        }
    }

    public static String getProxyName(ClassDefinition trait, ClassDefinition core) {
        return getKey(core.getDefinedClass(), trait.getDefinedClass()) + "_Proxy";
    }

    public static String getPropertyWrapperName(ClassDefinition trait, ClassDefinition core) {
        return getKey(core.getDefinedClass(), trait.getDefinedClass()) + "_ProxyWrapper";
    }

    protected static String getKey(Class core, Class trait) {
        return (trait.getName() + "." + core.getName());
    }

    protected Class<T> buildProxyClass(K core, Class<?> trait) {

        Class coreKlass = core.getClass();

        // get the trait classDef
        ClassDefinition tdef = getTraitRegistry().getTrait(trait.getName());
        ClassDefinition cdef = getTraitRegistry().getTraitable(coreKlass.getName());

        if (tdef == null) {
            if (trait.getAnnotation(Trait.class) != null) {
                try {
                    if (Thing.class.isAssignableFrom(trait)) {
                        tdef = buildClassDefinition(trait, null);
                    } else {
                        throw new RuntimeException("Unable to create definition for class " + trait +
                                                           " : trait interfaces should extend " + Thing.class.getName() + " or be DECLARED as traits explicitly");
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException("Unable to create definition for class " + trait + " : " + e.getMessage(), e);
                }
                getTraitRegistry().addTrait(tdef);
            } else {
                throw new RuntimeException("Unable to find Trait definition for class " + trait.getName() + ". It should have been DECLARED as a trait");
            }
        }
        if (cdef == null) {
            if (core.getClass().getAnnotation(Traitable.class) != null) {
                try {
                    cdef = buildClassDefinition(core.getClass(), core.getClass());
                } catch (IOException e) {
                    throw new UncheckedIOException("Unable to create definition for class " + coreKlass.getName() + " : " + e.getMessage(), e);
                }
                getTraitRegistry().addTraitable(cdef);
            } else {
                throw new RuntimeException("Unable to find Core class definition for class " + coreKlass.getName() + ". It should have been DECLARED as a trait");
            }
        }

        String proxyName = getProxyName(tdef, cdef);
        String wrapperName = getPropertyWrapperName(tdef, cdef);

        TraitPropertyWrapperClassBuilder propWrapperBuilder = (TraitPropertyWrapperClassBuilder) traitClassBuilderFactory.getPropertyWrapperBuilder();

        propWrapperBuilder.init(tdef, getTraitRegistry());
        try {
            byte[] propWrapper = propWrapperBuilder.buildClass(cdef, getRootClassLoader());
            registerAndLoadTypeDefinition(wrapperName, propWrapper);
        } catch (Exception e) {
            LOG.error("Exception", e);
        }

        TraitProxyClassBuilder proxyBuilder = traitClassBuilderFactory.getTraitProxyBuilder();

        proxyBuilder.init(tdef, TraitProxyImpl.class, getTraitRegistry());
        try {
            byte[] proxy = proxyBuilder.buildClass(cdef, getRootClassLoader());
            registerAndLoadTypeDefinition(proxyName, proxy);
        } catch (Exception e) {
            LOG.error("Exception", e);
        }

        try {
            getTraitRegistry().getFieldMask(trait.getName(), cdef.getDefinedClass().getName());
            getRootClassLoader().loadClass(wrapperName);
            return (Class<T>) getRootClassLoader().loadClass(proxyName);
        } catch (ClassNotFoundException e) {
            LOG.error("Exception", e);
            return null;
        }
    }

    public synchronized <K> CoreWrapper<K> getCoreWrapper(Class<K> coreKlazz, ClassDefinition coreDef) {
        if (wrapperCache == null) {
            wrapperCache = new HashMap<>();
        }
        Class<? extends CoreWrapper<K>> wrapperClass;
        if (wrapperCache.containsKey(coreKlazz)) {
            wrapperClass = (Class<? extends CoreWrapper<K>>) wrapperCache.get(coreKlazz);
        } else {
            try {
                wrapperClass = buildCoreWrapper(coreKlazz, coreDef);
            } catch (IOException | ClassNotFoundException e) {
                return null;
            }
            wrapperCache.put(coreKlazz, wrapperClass);
        }

        try {
            getTraitRegistry().addTraitable(buildClassDefinition(coreKlazz, wrapperClass));
            return wrapperClass != null ? wrapperClass.newInstance() : null;
        } catch (InstantiationException | IllegalAccessException | IOException e) {
            return null;
        }
    }

    public <K> TraitableBean<K, CoreWrapper<K>> asTraitable(K core, ClassDefinition coreDef) {
        if (coreDef == null || coreDef.getDefinedClass() != core.getClass()) {
            // ensure that a compatible interface cDef is not replaced for the missing actual definition
            try {
                coreDef = buildClassDefinition(core.getClass(), core.getClass());
            } catch (IOException e) {
                LOG.error("Exception", e);
            }
        }
        if (coreDef == null) {
            throw new IllegalArgumentException("Class definition is not specified!");
        } else {
            if (core instanceof Map) {
                if (!coreDef.isTraitable()) {
                    throw new UnsupportedOperationException("Error: cannot apply a trait to non-traitable class " + core.getClass() + ". Was it declared as @Traitable? ");
                }
                return coreDef.isFullTraiting() ? new LogicalMapCore((Map) core) : new MapCore((Map) core);
            }

            CoreWrapper<K> wrapper = (CoreWrapper<K>) getCoreWrapper(core.getClass(), coreDef);
            if (wrapper == null) {
                throw new UnsupportedOperationException("Error: cannot apply a trait to non-traitable class " + core.getClass() + ". Was it declared as @Traitable? ");
            }
            wrapper.init(core);
            return wrapper;
        }
    }

    public ClassDefinition buildClassDefinition(Class<?> klazz, Class<?> wrapperClass) throws IOException {
        ClassFieldInspectorImpl inspector = new ClassFieldInspectorImpl(klazz);

        ClassFieldAccessorStore store = getClassFieldAccessorStore();

        ClassDefinition def;
        if (!klazz.isInterface()) {
            String className = wrapperClass.getName();
            String superClass = wrapperClass != klazz ? klazz.getName() : klazz.getSuperclass().getName();
            String[] interfaces = new String[klazz.getInterfaces().length + 1];
            for (int j = 0; j < klazz.getInterfaces().length; j++) {
                interfaces[j] = klazz.getInterfaces()[j].getName();
            }
            interfaces[interfaces.length - 1] = CoreWrapper.class.getName();
            def = new ClassDefinition(className, superClass, interfaces);
            def.setDefinedClass(wrapperClass);

            Traitable tbl = wrapperClass.getAnnotation(Traitable.class);
            def.setTraitable(true, tbl != null && tbl.logical());
            Map<String, Field> fields = inspector.getFieldTypesField();
            for (Field f : fields.values()) {
                if (f != null) {
                    FieldDefinition fld = new FieldDefinition();
                    fld.setName(f.getName());
                    fld.setTypeName(f.getType().getName());
                    fld.setInherited(true);
                    ClassFieldAccessor accessor = store.getAccessor(def.getDefinedClass().getName(),
                                                                    fld.getName());
                    fld.setReadWriteAccessor(accessor);
                    if (inspector.getGetterMethods().containsKey(f.getName())) {
                        fld.setGetterName(inspector.getGetterMethods().get(f.getName()).getName());
                    }
                    if (inspector.getSetterMethods().containsKey(f.getName())) {
                        fld.setSetterName(inspector.getSetterMethods().get(f.getName()).getName());
                    }

                    def.addField(fld);
                }
            }
        } else {
            String className = klazz.getName();
            String superClass = Object.class.getName();
            String[] interfaces = new String[klazz.getInterfaces().length];
            for (int j = 0; j < klazz.getInterfaces().length; j++) {
                interfaces[j] = klazz.getInterfaces()[j].getName();
            }
            def = new ClassDefinition(className, superClass, interfaces);
            def.setDefinedClass(klazz);

            Map<String, Method> properties = inspector.getGetterMethods();
            for (Map.Entry<String, Method> propEntry : properties.entrySet()) {
                Method m = propEntry.getValue();
                if (m != null && m.getDeclaringClass() != TraitType.class && m.getDeclaringClass() != Thing.class && inspector.getSetterMethods().containsKey(propEntry.getKey())) {
                    FieldDefinition fld = new FieldDefinition();
                    fld.setName(getterToFieldName(m.getName()));
                    fld.setTypeName(m.getReturnType().getName());
                    fld.setInherited(true);
                    ClassFieldAccessor accessor = store.getAccessor(def.getDefinedClass().getName(),
                                                                    fld.getName());
                    fld.setReadWriteAccessor(accessor);
                    fld.setGetterName(m.getName());
                    fld.setSetterName(inspector.getSetterMethods().get(propEntry.getKey()).getName());

                    def.addField(fld);
                }
            }
        }

        return def;
    }

    private String getterToFieldName(String getter) {
        getter = getter.startsWith("is") ? getter.substring(2) : getter.substring(3);
        getter = getter.substring(0, 1).toLowerCase() + getter.substring(1);
        return getter;
    }

    protected <K> Class<CoreWrapper<K>> buildCoreWrapper(Class<K> coreKlazz, ClassDefinition coreDef) throws IOException, ClassNotFoundException {

        String coreName = coreKlazz.getName();
        String wrapperName = coreName + "Wrapper";

        try {
            byte[] wrapper = new TraitCoreWrapperClassBuilderImpl().buildClass(coreDef, getRootClassLoader());
            registerAndLoadTypeDefinition(wrapperName, wrapper);
        } catch (Exception e) {
            LOG.error("Exception", e);
        }

        return (Class<CoreWrapper<K>>) getRootClassLoader().loadClass(wrapperName);
    }

    public static void valueOf(MethodVisitor mv, String type) {
        mv.visitMethodInsn(INVOKESTATIC,
                           BuildUtils.getInternalType(BuildUtils.box(type)),
                           "valueOf",
                           "(" + BuildUtils.getTypeDescriptor(type) + ")" +
                                   BuildUtils.getTypeDescriptor(BuildUtils.box(type)),
                           false
        );
    }

    public static void primitiveValue(MethodVisitor mv, String fieldType) {
        mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType(BuildUtils.box(fieldType)));
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                BuildUtils.getInternalType(BuildUtils.box(fieldType)),
                fieldType + "Value",
                "()" + BuildUtils.getTypeDescriptor(fieldType),
                false);
    }

    public static void invokeExtractor(MethodVisitor mv, String proxyName, ClassDefinition core, FieldDefinition field) {
        FieldDefinition tgtField = core.getFieldByAlias(field.resolveAlias());
        String fieldType = tgtField.getTypeName();
        String returnType = BuildUtils.getTypeDescriptor(fieldType);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD,
                          BuildUtils.getInternalType(proxyName),
                          "object",
                          BuildUtils.getTypeDescriptor(core.getClassName()));

        mv.visitMethodInsn(INVOKEVIRTUAL,
                           Type.getInternalName(core.getDefinedClass()),
                           tgtField.getReadMethod(),
                           Type.getMethodDescriptor(Type.getType(returnType)),
                           false);
    }

    public static void invokeInjector(MethodVisitor mv, String proxyName, ClassDefinition core, FieldDefinition field, boolean toNull, int pointer) {
        FieldDefinition tgtField = core.getFieldByAlias(field.resolveAlias());
        String fieldType = tgtField.getTypeName();
        String returnType = BuildUtils.getTypeDescriptor(fieldType);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD,
                          BuildUtils.getInternalType(proxyName),
                          "object",
                          BuildUtils.getTypeDescriptor(core.getName()));

        if (toNull) {
            mv.visitInsn( AsmUtil.zero(field.getTypeName()));
        } else {
            mv.visitVarInsn(AsmUtil.varType(fieldType), pointer);
        }

        if (!BuildUtils.isPrimitive(fieldType)) {
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType(fieldType));
        }

        mv.visitMethodInsn(INVOKEVIRTUAL,
                           Type.getInternalName(core.getDefinedClass()),
                           tgtField.getWriteMethod(),
                           Type.getMethodDescriptor(Type.getType(void.class), Type.getType(returnType)),
                           false);
    }

    public static String buildSignature(Method method) {
        StringBuilder sig = new StringBuilder("(");
        for (Class arg : method.getParameterTypes()) {
            sig.append(BuildUtils.getTypeDescriptor(arg.getName()));
        }
        sig.append(")");
        sig.append(BuildUtils.getTypeDescriptor(method.getReturnType().getName()));
        return sig.toString();
    }

    public static int getStackSize(Method m) {
        int stack = 1;
        for (Class klass : m.getParameterTypes()) {
            stack += BuildUtils.sizeOf(klass.getName());
        }
        return stack;
    }

    public static boolean isCompatible(Method m, Method q) {
        if (!m.getName().equals(q.getName())) {
            return false;
        }
        if (!m.getReturnType().isAssignableFrom(q.getReturnType())) {
            return false;
        }
        if (m.getParameterTypes().length != q.getParameterTypes().length) {
            return false;
        }
        for (int j = 0; j < q.getParameterTypes().length; j++) {
            if (!q.getParameterTypes()[j].isAssignableFrom(m.getParameterTypes()[j])) {
                return false;
            }
        }
        return true;
    }

    protected static boolean excludeFromShadowing(Method m, ClassDefinition cdef) {
        return Object.class.equals(m.getDeclaringClass()) ||
                "getFields".equals(m.getName()) || "getCore".equals(m.getName()) || "isTop".equals(m.getName()) ||
                isGetter(m, cdef) ||
                isSetter(m, cdef);
    }

    protected static boolean isGetter(Method m, ClassDefinition cdef) {
        return (m.getParameterTypes().length == 0) &&
                (!void.class.equals(m.getReturnType())) &&
                (m.getName().startsWith("get") || m.getName().startsWith("is")) &&
                (cdef.getField(toFieldName(m.getName())) != null);
    }

    private static String toFieldName(String name) {
        if ((name.startsWith("get") || name.startsWith("set")) && name.length() > 3) {
            return name.substring(3, 4).toLowerCase() + name.substring(4);
        }
        if (name.startsWith("is") && name.length() > 2) {
            return name.substring(2, 3).toLowerCase() + name.substring(3);
        }
        return name;
    }

    protected static boolean isSetter(Method m, ClassDefinition cdef) {
        return (m.getParameterTypes().length == 1) &&
                (void.class.equals(m.getReturnType())) &&
                (m.getName().startsWith("set")) &&
                (cdef.getField(toFieldName(m.getName())) != null);
    }

    protected abstract Class<?> registerAndLoadTypeDefinition(String proxyName, byte[] proxy) throws ClassNotFoundException;

    protected abstract ClassLoader getRootClassLoader();

    protected abstract TraitRegistryImpl getTraitRegistry();

    protected abstract HierarchyEncoder getHierarchyEncoder();

    protected abstract TripleStore getTripleStore();

    protected abstract TripleFactory getTripleFactory();

    protected abstract ClassFieldAccessorStore getClassFieldAccessorStore();
}