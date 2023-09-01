package org.drools.mvel.compiler.rule.builder.dialect.asm;

import org.drools.mvel.asm.ClassGenerator;
import org.junit.Test;
import org.mvel2.asm.MethodVisitor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mvel2.asm.Opcodes.ACC_FINAL;
import static org.mvel2.asm.Opcodes.ACC_PRIVATE;
import static org.mvel2.asm.Opcodes.ACC_PUBLIC;
import static org.mvel2.asm.Opcodes.ALOAD;
import static org.mvel2.asm.Opcodes.ARETURN;
import static org.mvel2.asm.Opcodes.RETURN;

public class ClassGeneratorTest {

    @Test
    public void testGenerateBean() {
        final String MY_NAME = "myName";
        ClassGenerator generator = new ClassGenerator("pkg.Bean1", getClass().getClassLoader())
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
        assertThat(instance.toString()).isEqualTo(MY_NAME);
    }

    @Test
    public void testGenerateWithConstructorArg() {
        final String MY_NAME = "myName";
        ClassGenerator generator = new ClassGenerator("pkg.Bean2", getClass().getClassLoader())
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
        assertThat(instance.toString()).isEqualTo(MY_NAME);
    }
}
