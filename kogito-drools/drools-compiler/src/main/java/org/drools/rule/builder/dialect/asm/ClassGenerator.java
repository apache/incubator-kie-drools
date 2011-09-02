package org.drools.rule.builder.dialect.asm;

import org.mvel2.asm.*;

import java.util.*;

import static org.mvel2.asm.Opcodes.*;
import static org.mvel2.asm.Type.getDescriptor;

public class ClassGenerator {

    private final String className;
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
        this(className, null);
    }

    public ClassGenerator(String className, ClassLoader classLoader) {
        this.className = className;
        this.classLoader = classLoader == null ? INTERNAL_CLASS_LOADER : new InternalClassLoader(classLoader);
    }

    private interface ClassPartDescr {
        void write(ClassGenerator cg, ClassWriter cw);
    }

    public byte[] generateBytecode() {
        if (bytecode == null) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
            cw.visit(version, access, getClassDescriptor(), signature, getSuperClassDescriptor(), toInternalForm(interfaces));
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
        if (classDescriptor == null) classDescriptor = toInternalForm(className);
        return classDescriptor;
    }

    public String getSuperClassDescriptor() {
        if (superDescriptor == null) superDescriptor = toInternalForm(superName);
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

    private String toInternalForm(String className) {
        return className.replace('.', '/');
    }

    private String[] toInternalForm(String[] classNames) {
        if (classNames == null) return null;
        String[] internals = new String[classNames.length];
        for (int i = 0; i < classNames.length; i++) internals[i] = toInternalForm(classNames[i]);
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
            public void body(ClassGenerator cg, MethodVisitor mv) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, cg.getSuperClassDescriptor(), "<init>", "()V"); // super()
                body.body(cg, mv);
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
        void body(ClassGenerator cg, MethodVisitor mv);
    }

    private static class MethodDescr implements ClassPartDescr {
        private final int access;
        private final String name;
        private final String desc;
        private final String signature;
        private final String[] exceptions;
        private final MethodBody body;

        public MethodDescr(int access, String name, String desc, String signature, String[] exceptions, MethodBody body) {
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
            body.body(cg, mv);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
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
}
