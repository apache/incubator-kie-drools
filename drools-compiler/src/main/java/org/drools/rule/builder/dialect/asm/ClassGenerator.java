package org.drools.rule.builder.dialect.asm;

import org.drools.base.*;
import org.mvel2.asm.*;

import java.io.*;
import java.util.*;

import static org.mvel2.asm.Opcodes.*;

public class ClassGenerator {

    private final String className;
    private final TypeResolver typeResolver;
    private final InternalClassLoader classLoader;

    private int version = V1_5;
    private int access = ACC_PUBLIC + ACC_SUPER;
    private String signature;
    private Class superClass = Object.class;
    private Class<?>[] interfaces;

    private final String classDescriptor;
    private String superDescriptor;

    private List<ClassPartDescr> classParts = new ArrayList<ClassPartDescr>();

    private byte[] bytecode;
    private Class<?> clazz;

    public ClassGenerator(String className, ClassLoader classLoader) {
        this(className, classLoader, null);
    }

    public ClassGenerator(String className, ClassLoader classLoader, TypeResolver typeResolver) {
        this.className = className;
        this.classDescriptor = className.replace('.', '/');
        this.classLoader = new InternalClassLoader(classLoader);
        this.typeResolver = typeResolver == null ? INTERNAL_TYPE_RESOLVER : typeResolver;
    }

    private interface ClassPartDescr {
        void write(ClassGenerator cg, ClassWriter cw);
    }

    public byte[] generateBytecode() {
        if (bytecode == null) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
            cw.visit(version, access, getClassDescriptor(), signature, getSuperClassDescriptor(), toInteralNames(interfaces));
            for (ClassPartDescr part : classParts) part.write(this, cw);
            cw.visitEnd();
            bytecode = cw.toByteArray();
        }
        return bytecode;
    }

    public Class<?> generateClass() {
        if (clazz == null) clazz = classLoader.defineClass(className, generateBytecode());
        return clazz;
    }

    public <T> T newInstance() {
        try {
            return (T)generateClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Accessors

    public String getClassDescriptor() {
        return classDescriptor;
    }

    public String getSuperClassDescriptor() {
        if (superDescriptor == null) superDescriptor = toInteralName(superClass);
        return superDescriptor;
    }

    public ClassGenerator setVersion(int version) {
        this.version = version;
        return this;
    }

    public ClassGenerator setAccess(int access) {
        this.access = access;
        return this;
    }

    public ClassGenerator setSignature(String signature) {
        this.signature = signature;
        return this;
    }

    public ClassGenerator setSuperClass(Class superClass) {
        this.superClass = superClass;
        return this;
    }

    public ClassGenerator setInterfaces(Class<?>... interfaces) {
        this.interfaces = interfaces;
        return this;
    }

    // Utility

    private Map<Class<?>, String> descriptorsCache = new HashMap<Class<?>, String>();

    private String descriptorOf(Class<?> type) {
        String descriptor = descriptorsCache.get(type);
        if (descriptor == null) {
            descriptor = Type.getDescriptor(type);
            descriptorsCache.put(type, descriptor);
        }
        return descriptor;
    }

    public String methodDescr(Class<?> type, Class<?>... args) {
        StringBuilder desc = new StringBuilder("(");
        if (args != null) for (Class<?> arg : args) desc.append(descriptorOf(arg));
        desc.append(")").append(type == null ? "V" : descriptorOf(type));
        return desc.toString();
    }

    private Type toType(String typeName) {
        return Type.getType(toTypeDescriptor(typeName));
    }

    public String toTypeDescriptor(Class<?> clazz) {
        return descriptorOf(clazz);
    }

    public String toTypeDescriptor(String className) {
        String arrayPrefix = "";
        while (className.endsWith("[]")) {
            arrayPrefix += "[";
            className = className.substring(0, className.length()-2);
        }
        String typeDescriptor;
        try {
            typeDescriptor = toTypeDescriptor(typeResolver.resolveType(className));
        } catch (ClassNotFoundException e) {
            typeDescriptor = "L" + className.replace('.', '/') + ";";
        }
        return arrayPrefix + typeDescriptor;
    }

    public String toInteralName(Class<?> clazz) {
        return clazz.isPrimitive() ? descriptorOf(clazz) : Type.getType(clazz).getInternalName();
    }

    public String toInteralName(String className) {
        String arrayPrefix = "";
        while (className.endsWith("[]")) {
            arrayPrefix += "[";
            className = className.substring(0, className.length()-2);
        }
        String typeDescriptor;
        boolean isPrimitive = false;
        try {
            Class<?> clazz = typeResolver.resolveType(className);
            isPrimitive = clazz.isPrimitive();
            typeDescriptor = toInteralName(clazz);
        } catch (ClassNotFoundException e) {
            typeDescriptor = className.replace('.', '/');
        }
        if (!isPrimitive && arrayPrefix.length() > 0) typeDescriptor = "L" + typeDescriptor + ";";
        return arrayPrefix + typeDescriptor;
    }

    private String[] toInteralNames(Class<?>[] classes) {
        if (classes == null) return null;
        String[] internals = new String[classes.length];
        for (int i = 0; i < classes.length; i++) internals[i] = toInteralName(classes[i]);
        return internals;
    }

    // FieldDescr

    public ClassGenerator addField(int access, String name, Class<?> type) {
        return addField(access, name, type, null, null);
    }

    public ClassGenerator addField(int access, String name, Class<?> type, String signature) {
        return addField(access, name, type, signature, null);
    }

    public ClassGenerator addStaticField(int access, String name, Class<?> type, Object value) {
        return addField(access + ACC_STATIC, name, type, null, value);
    }

    public ClassGenerator addStaticField(int access, String name, Class<?> type, String signature, Object value) {
        return addField(access + ACC_STATIC, name, type, signature, value);
    }

    private ClassGenerator addField(int access, String name, Class<?> type, String signature, Object value) {
        classParts.add(new FieldDescr(access, name, descriptorOf(type), signature, value));
        return this;
    }

    private static class FieldDescr implements ClassPartDescr {
        private final int access;
        private final String name;
        private final String desc;
        private final String signature;
        private final Object value;

        FieldDescr(int access, String name, String desc, String signature, Object value) {
            this.access = access;
            this.name = name;
            this.desc = desc;
            this.signature = signature;
            this.value = value;
        }

        public void write(ClassGenerator cg, ClassWriter cw) {
            cw.visitField(access, name, desc, signature, value).visitEnd();
        }
    }

    public ClassGenerator addDefaultConstructor() {
        return addDefaultConstructor(EMPTY_METHOD_BODY);
    }

    public ClassGenerator addDefaultConstructor(final MethodBody body) {
        MethodBody constructorBody = new MethodBody() {
            public void body(MethodVisitor mv) {
                body.setClassGenerator(cg);
                body.setMethodVisitor(mv);

                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, cg.getSuperClassDescriptor(), "<init>", "()V"); // super()
                body.body(mv);
            }
        };
        return addMethod(ACC_PUBLIC, "<init>", "()V", null, null, constructorBody);
    }

    public ClassGenerator addMethod(int access, String name, String desc) {
        return addMethod(access, name, desc, null, null, EMPTY_METHOD_BODY);
    }

    public ClassGenerator addMethod(int access, String name, String desc, MethodBody body) {
        return addMethod(access, name, desc, null, null, body);
    }

    public ClassGenerator addMethod(int access, String name, String desc, String signature, MethodBody body) {
        return addMethod(access, name, desc, signature, null, body);
    }

    public ClassGenerator addMethod(int access, String name, String desc, String[] exceptions, MethodBody body) {
        return addMethod(access, name, desc, null, exceptions, body);
    }

    public ClassGenerator addMethod(int access, String name, String desc, String signature, String[] exceptions, MethodBody body) {
        classParts.add(new MethodDescr(access, name, desc, signature, exceptions, body));
        return this;
    }

    private static final MethodBody EMPTY_METHOD_BODY = new MethodBody() {
        public final void body(MethodVisitor mv) {
            mv.visitInsn(RETURN); // return
        }
    };

    // MethodBody

    public abstract static class MethodBody {
        ClassGenerator cg;
        MethodVisitor mv;
        private Map<Integer, Type> storedTypes;

        public abstract void body(MethodVisitor mv);

        private void setClassGenerator (ClassGenerator cg) {
            this.cg = cg;
        }
        private void setMethodVisitor (MethodVisitor mv) {
            this.mv = mv;
        }

        public int store(int registry, String typeName) {
            if (storedTypes == null) storedTypes = new HashMap<Integer, Type>();
            Type t = cg.toType(typeName);
            mv.visitVarInsn(t.getOpcode(ISTORE), registry);
            storedTypes.put(registry, t);
            return t.getSize();
        }

        public void load(int registry) {
            mv.visitVarInsn(storedTypes.get(registry).getOpcode(ILOAD), registry);
        }

        public void loadAsObject(int registry) {
            Type type = storedTypes.get(registry);
            mv.visitVarInsn(type.getOpcode(ILOAD), registry);
            String typeName = type.getClassName();
            if (typeName.equals("int"))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
            else if (typeName.equals("boolean"))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
            else if (typeName.equals("char"))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
            else if (typeName.equals("byte"))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
            else if (typeName.equals("short"))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
            else if (typeName.equals("float"))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
            else if (typeName.equals("long"))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
            else if (typeName.equals("double"))
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
        }

        public void println(String msg) {
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn(msg);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        }

        public void printRegistryValue(int reg, Class<?> clazz) {
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(Type.getType(clazz).getOpcode(ILOAD), reg);
            invokeVirtual(PrintStream.class, "println", null, clazz);
        }

        public void printLastRegistry(Class<?> clazz) {
            Type t = Type.getType(clazz);
            mv.visitVarInsn(t.getOpcode(ISTORE), 100);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(t.getOpcode(ILOAD), 100);
            invokeVirtual(PrintStream.class, "println", null, clazz);
        }

        public void printStack() {
            mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "()V");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/RuntimeException", "printStackTrace", "()V");
            mv.visitInsn(RETURN);
        }

        public <T> void returnAsArray(T[] array) {
            push(array.length);
            mv.visitTypeInsn(ANEWARRAY, internalName(array.getClass().getComponentType()));
            for (int i = 0; i < array.length; i++) {
                mv.visitInsn(DUP);
                push(i);
                push(array[i]);
                mv.visitInsn(AASTORE);
            }
            mv.visitInsn(ARETURN);
        }

        public <T> void returnAsArray(Collection<T> collection, Class<T> clazz) {
            push(collection.size());
            mv.visitTypeInsn(ANEWARRAY, internalName(clazz));
            int i = 0;
            for (T item : collection) {
                mv.visitInsn(DUP);
                push(i++);
                push(item);
                mv.visitInsn(AASTORE);
            }
            mv.visitInsn(ARETURN);
        }

        public void push(Object obj) {
            if (obj instanceof Boolean) push((Boolean)obj);
            else mv.visitLdcInsn(obj);
        }

        public void push(Boolean b) {
            mv.visitFieldInsn(GETSTATIC, "java/lang/Boolean", b ? "TRUE" : "FALSE", "Ljava/lang/Boolean;");
        }

        public void cast(Class<?> clazz) {
            mv.visitTypeInsn(CHECKCAST, internalName(clazz));
        }

        protected final void castToPrimitive(Class<?> clazz) {
            if (clazz == boolean.class) {
                cast(Boolean.class);
                invokeVirtual(Boolean.class, "booleanValue", boolean.class);
            } else if (clazz == char.class) {
                cast(Character.class);
                invokeVirtual(Character.class, "charValue", char.class);
            } else {
                cast(Number.class);
                invokeVirtual(Number.class, clazz.getName() + "Value", clazz);
            }
        }

        public void instanceOf(Class<?> clazz) {
            mv.visitTypeInsn(INSTANCEOF, internalName(clazz));
        }

        public void invokeThis(String methodName, Class<?> returnedType, Class<?>... paramsType) {
            mv.visitMethodInsn(INVOKEVIRTUAL, classDescriptor(), methodName, methodDescr(returnedType, paramsType));
        }

        public void invokeStatic(Class<?> clazz, String methodName, Class<?> returnedType, Class<?>... paramsType) {
            invoke(INVOKESTATIC, clazz, methodName, returnedType, paramsType);
        }

        public void invokeVirtual(Class<?> clazz, String methodName, Class<?> returnedType, Class<?>... paramsType) {
            invoke(INVOKEVIRTUAL, clazz, methodName, returnedType, paramsType);
        }

        public void invokeInterface(Class<?> clazz, String methodName, Class<?> returnedType, Class<?>... paramsType) {
            invoke(INVOKEINTERFACE, clazz, methodName, returnedType, paramsType);
        }

        public void invokeSpecial(Class<?> clazz, String methodName, Class<?> returnedType, Class<?>... paramsType) {
            invoke(INVOKESPECIAL, clazz, methodName, returnedType, paramsType);
        }

        private void invoke(int opCode, Class<?> clazz, String methodName, Class<?> returnedType, Class<?>... paramsType) {
            mv.visitMethodInsn(opCode, internalName(clazz), methodName, methodDescr(returnedType, paramsType));
        }

        public void putField(String name, Class<?> type) {
            mv.visitFieldInsn(PUTFIELD, classDescriptor(), name, cg.descriptorOf(type));
        }

        public void getField(String name, Class<?> type) {
            mv.visitFieldInsn(GETFIELD, classDescriptor(), name, cg.descriptorOf(type));
        }

        // ClassGenerator delegates

        public String classDescriptor() {
            return cg.getClassDescriptor();
        }

        public String superClassDescriptor() {
            return cg.getSuperClassDescriptor();
        }
        public String methodDescr(Class<?> type, Class<?>... args) {
            return cg.methodDescr(type, args);
        }

        private Type type(String typeName) {
            return cg.toType(typeName);
        }

        public String typeDescr(Class<?> clazz) {
            return cg.toTypeDescriptor(clazz);
        }

        public String typeDescr(String className) {
            return cg.toTypeDescriptor(className);
        }

        public String internalName(Class<?> clazz) {
            return cg.toInteralName(clazz);
        }

        public String internalName(String className) {
            return cg.toInteralName(className);
        }
    }

    // MethodDescr

    private static class MethodDescr implements ClassPartDescr {
        private final int access;
        private final String name;
        private final String desc;
        private final String signature;
        private final String[] exceptions;
        private final MethodBody body;

        private MethodDescr(int access, String name, String desc, String signature, String[] exceptions, MethodBody body) {
            this.access = access;
            this.name = name;
            this.desc = desc;
            this.signature = signature;
            this.exceptions = exceptions;
            this.body = body;
        }

        public void write(ClassGenerator cg, ClassWriter cw) {
            MethodVisitor mv = cw.visitMethod(access, name, desc, signature, exceptions);
            mv.visitCode();

            body.setClassGenerator(cg);
            body.setMethodVisitor(mv);
            body.body(mv);

            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
    }

    // InternalClassLoader

    private static class InternalClassLoader extends ClassLoader {

        InternalClassLoader(ClassLoader classLoader) {
            super(classLoader);
        }

        Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

    // InternalTypeResolver

    private static final InternalTypeResolver INTERNAL_TYPE_RESOLVER = new InternalTypeResolver();

    private static class InternalTypeResolver implements TypeResolver {
        public Set<String> getImports() {
            throw new RuntimeException("Not Implemented");
        }

        public void addImport(String importEntry) {
            throw new RuntimeException("Not Implemented");
        }

        public Class resolveType(String className) throws ClassNotFoundException {
            return Class.forName(className);
        }

        public String getFullTypeName(String shortName) throws ClassNotFoundException {
            throw new RuntimeException("Not Implemented");
        }
    }
}
