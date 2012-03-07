package org.drools.rule.builder.dialect.asm;

import org.junit.*;
import org.mvel2.asm.*;

import static org.junit.Assert.assertEquals;
import static org.mvel2.asm.Opcodes.*;

public class ClassGeneratorTest {

    @Test
    public void testGenerateBean() {
        final String MY_NAME = "myName";
        ClassGenerator generator = new ClassGenerator("pkg.Bean", getClass().getClassLoader())
                .addField(ACC_PRIVATE | ACC_FINAL, "name", String.class);

        generator.addDefaultConstructor(new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitVarInsn(ALOAD, 0);// read local variable 0 (initialized to this) and push it on the stack
                mv.visitLdcInsn(MY_NAME); // push the String MY_NAME on the stack
                putFieldInThis("name", String.class);
                mv.visitInsn(RETURN);
            }
        }).addMethod(ACC_PUBLIC, "toString", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                getFieldFromThis("name", String.class);
                mv.visitInsn(ARETURN); // return the first object on the stack
            }
        });

        Object instance = generator.newInstance();
        assertEquals(MY_NAME, instance.toString());
    }

    @Test
    public void testGenerateWithConstructorArg() {
        final String MY_NAME = "myName";
        ClassGenerator generator = new ClassGenerator("pkg.Bean", getClass().getClassLoader())
                .addField(ACC_PRIVATE | ACC_FINAL, "name", String.class);

        generator.addDefaultConstructor(new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                putFieldInThisFromRegistry("name", String.class, 1);
                mv.visitInsn(RETURN);
            }
        }, String.class);

        generator.addMethod(ACC_PUBLIC, "toString", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                getFieldFromThis("name", String.class);
                mv.visitInsn(ARETURN); // return the first object on the stack
            }
        });

        Object instance = generator.newInstance(String.class, MY_NAME);
        assertEquals(MY_NAME, instance.toString());
    }
}
