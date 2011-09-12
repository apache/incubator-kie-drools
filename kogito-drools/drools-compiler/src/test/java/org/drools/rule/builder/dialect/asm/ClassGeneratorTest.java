package org.drools.rule.builder.dialect.asm;

import org.junit.*;
import org.mvel2.asm.*;

import static org.junit.Assert.assertEquals;
import static org.mvel2.asm.Opcodes.*;
import static org.mvel2.asm.Type.*;

public class ClassGeneratorTest {

    @Test
    public void testGenerate() {
        final String MY_NAME = "myName";
        ClassGenerator generator = new ClassGenerator("pkg.Bean", getClass().getClassLoader());

        generator.addField(ACC_PRIVATE + ACC_FINAL, "name", String.class);

        generator.addDefaultConstructor(new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitVarInsn(ALOAD, 0);// read local variable 0 (initialized to this) and push it on the stack
                mv.visitLdcInsn(MY_NAME); // push the String MY_NAME on the stack
                putField("name", String.class);
            }
        }).addMethod(ACC_PUBLIC, "toString", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitVarInsn(ALOAD, 0); // read local variable 0 (initialized to this) and push it on the stack
                getField("name", String.class);
                mv.visitInsn(ARETURN); // return the first object on the stack
            }
        });

        Object instance = generator.newInstance();
        assertEquals(MY_NAME, instance.toString());
    }
}
