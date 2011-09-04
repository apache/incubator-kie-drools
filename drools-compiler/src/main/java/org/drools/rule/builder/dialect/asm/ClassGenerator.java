package org.drools.rule.builder.dialect.asm;

import org.drools.base.*;
import org.mvel2.asm.*;

import java.util.*;

import static org.mvel2.asm.Opcodes.*;
import static org.mvel2.asm.Type.getDescriptor;

public class ClassGenerator {

    private final String className;
    private final TypeResolver typeResolver;
    private final InternalClassLoader classLoader;

    private int version = V1_5;
    private int access = ACC_PUBLIC + ACC_SUPER;
    private String signature;
    private String superName = "java.lang.Object";
    private String[] interfaces;

    private String classDescriptor;
    private String superDescriptor;

    private List<ClassPartDescr> classParts = new ArrayList<ClassPartDescr>();

    private byte[] bytecode;
    private Class<?> clazz;

    public ClassGenerator(String className) {
        this(className, null, null);
    }

    public ClassGenerator(String className, ClassLoader classLoader) {
        this(className, classLoader, null);
    }

    public ClassGenerator(String className, TypeResolver typeResolver) {
        this(className, null, typeResolver);
    }

    public ClassGenerator(String className, ClassLoader classLoader, TypeResolver typeResolver) {
        this.className = className;
        this.classLoader = classLoader == null ? INTERNAL_CLASS_LOADER : new InternalClassLoader(classLoader);
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
        if (classDescriptor == null) classDescriptor = toInteralName(className);
        return classDescriptor;
    }

    public String getSuperClassDescriptor() {
        if (superDescriptor == null) superDescriptor = toInteralName(superName);
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

    public ClassGenerator setSuperName(String superName) {
        this.superName = superName;
        return this;
    }

    public ClassGenerator setInterfaces(String... interfaces) {
        this.interfaces = interfaces;
        return this;
    }

    // Utility

    public String toMethodDescriptor(Class<?> type, Class<?>... args) {
        StringBuilder desc = new StringBuilder("(");
        if (args != null) for (Class<?> arg : args) desc.append(getDescriptor(arg));
        desc.append(")").append(type == null ? "V" : getDescriptor(type));
        return desc.toString();
    }

    private Type toType(String typeName) {
        return Type.getType(toTypeDescriptor(typeName));
    }

    public String toTypeDescriptor(Class<?> clazz) {
        return Type.getType(clazz).getDescriptor();
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
        return clazz.isPrimitive() ? Type.getType(clazz).getDescriptor() : Type.getType(clazz).getInternalName();
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

    private String[] toInteralNames(String[] classNames) {
        if (classNames == null) return null;
        String[] internals = new String[classNames.length];
        for (int i = 0; i < classNames.length; i++) internals[i] = toInteralName(classNames[i]);
        return internals;
    }

    // FieldDescr

    public ClassGenerator addField(int access, String name, Class<?> type) {
        return addField(access, name, getDescriptor(type), null, null);
    }

    public ClassGenerator addField(int access, String name, String desc) {
        return addField(access, name, desc, null, null);
    }

    public ClassGenerator addField(int access, String name, String desc, String signature) {
        return addField(access, name, desc, signature, null);
    }

    public ClassGenerator addStaticField(int access, String name, String desc, Object value) {
        return addField(access + ACC_STATIC, name, desc, null, value);
    }

    public ClassGenerator addStaticField(int access, String name, String desc, String signature, Object value) {
        return addField(access + ACC_STATIC, name, desc, signature, value);
    }

    private ClassGenerator addField(int access, String name, String desc, String signature, Object value) {
        classParts.add(new FieldDescr(access, name, desc, signature, value));
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

    // MethodDescr

    public ClassGenerator addDefaultConstructor(final MethodBody body) {
        MethodBody constructorBody = new MethodBody() {
            public void body(ClassGenerator cg, MethodVisitor mv, MethodHelper mh) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, cg.getSuperClassDescriptor(), "<init>", "()V"); // super()
                body.body(cg, mv, mh);
                mv.visitInsn(RETURN); // return
            }
        };
        return addMethod(ACC_PUBLIC, "<init>", "()V", null, null, constructorBody);
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

    public interface MethodBody {
        void body(ClassGenerator cg, MethodVisitor mv, MethodHelper mh);
    }

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
            body.body(cg, mv, new MethodHelper(cg, mv));
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
    }

    // Method helpers

    public static class MethodHelper {
        private final ClassGenerator cg;
        private final MethodVisitor mv;
        private Map<Integer, Type> storedTypes;

        public MethodHelper(ClassGenerator cg, MethodVisitor mv) {
            this.cg = cg;
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
    }

    // InternalClassLoader

    private static final InternalClassLoader INTERNAL_CLASS_LOADER = new InternalClassLoader(ClassGenerator.class.getClassLoader());

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
